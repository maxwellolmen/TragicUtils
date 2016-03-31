package com.pizzaguy.itemmail;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pizzaguy.plugin.Plugin;
import com.pizzaguy.serialization.ItemStackSerialization;
import com.pizzaguy.sql.DefaultSql;

public class ItemMailSql extends DefaultSql {

    public ItemMailSql(Plugin plugin) {
        super(plugin);
    }

    public boolean addTransaction(Transaction transaction) {
        try {
            PreparedStatement pstmt = getConnection()
                    .prepareStatement("INSERT INTO Transactions (sender,receiver, items) VALUES (?,?,?)");
            pstmt.setString(1, transaction.getSender().toString().trim());
            pstmt.setString(2, transaction.getReceiver().toString().trim());
            pstmt.setBytes(3, ItemStackSerialization.serializeArray(transaction.getItems()));
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Transaction getTransaction(int id) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from Transactions where ID = " + id);
            Transaction trans = null;
            if(rs.next())
                trans = new Transaction(rs.getInt("ID"),UUID.fromString(rs.getString("sender")), UUID.fromString(rs.getString("receiver")), ItemStackSerialization.deserializeArray(rs.getBytes("items")));
            rs.close();
            stmt.close();
            return trans;
        } catch (SQLException e) {
        }
        return null;
    }
    
    public boolean deleteTransaction(int id){
        try {
            Statement stmt = getConnection().createStatement();
            stmt.executeUpdate("DELETE FROM Transactions where ID = " + id);
            stmt.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public List<Transaction> getAllTransactions() {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Transactions");
            List<Transaction> transactions = new ArrayList<Transaction>();
            while(rs.next()){
                transactions.add( new Transaction(rs.getInt("ID"),UUID.fromString(rs.getString("sender")), UUID.fromString(rs.getString("receiver")), ItemStackSerialization.deserializeArray(rs.getBytes("items"))));
            }
            rs.close();
            stmt.close();
            return transactions;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Transaction> getTransactions(UUID uuid) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Transactions where receiver = '" + uuid.toString().trim()+"'");
            List<Transaction> transactions = new ArrayList<Transaction>();
            while(rs.next()){
                transactions.add( new Transaction(rs.getInt("ID"),UUID.fromString(rs.getString("sender")), UUID.fromString(rs.getString("receiver")), ItemStackSerialization.deserializeArray(rs.getBytes("items"))));
            }
            rs.close();
            stmt.close();
            return transactions;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createTables() {
        try {
            Statement stmt = getConnection().createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS Transactions (" 
                    + " ID INTEGER PRIMARY KEY,"
                    + " sender VARCHAR(45) NOT NULL," 
                    + " receiver VARCHAR(45) NOT NULL," 
                    + " items BLOB NOT NULL)");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            getPlugin().getDataFolder().mkdirs();
            setConnection(DriverManager
                    .getConnection("jdbc:sqlite:" + getPlugin().getDataFolder().getAbsolutePath() + "/ItemMail.db"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
