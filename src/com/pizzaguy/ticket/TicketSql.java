package com.pizzaguy.ticket;

import com.pizzaguy.plugin.Plugin;
import com.pizzaguy.sql.DefaultSql;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class TicketSql extends DefaultSql {

    public TicketSql(Plugin plugin) {
        super(plugin);
    }

    public int getTicketCount() {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM Tickets ");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void printTickets(CommandSender p) {
        p.sendMessage(ChatColor.GOLD + "Tickets: ");
        OfflinePlayer[] players = getPlugin().getServer().getOfflinePlayers().clone();
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Tickets ");
            while (rs.next()) {
                String name = null;
                for (int i = 0; i < players.length; i++) {
                    if (players[i].getUniqueId().toString().equals(rs.getString("UUID"))) {
                        name = players[i].getName();
                        break;
                    }
                }
                p.sendMessage(
                        ChatColor.AQUA + "" + rs.getInt("ID") + ". " + name + " '" + rs.getString("Message") + "'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTicket(String UUID, String ticket) {
        try {
            Statement stmt = getConnection().createStatement();
            stmt.executeUpdate("INSERT INTO Tickets (UUID,MESSAGE) VALUES (" + "'" + UUID + "'" + ",'" + ticket + "')");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTicket(String UUID) {
        try {
            Statement stmt = getConnection().createStatement();
            stmt.executeUpdate("DELETE FROM Tickets WHERE UUID = '" + UUID + "'");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTicket(int id) {
        try {
            Statement stmt = getConnection().createStatement();
            stmt.executeUpdate("DELETE FROM Tickets WHERE ID = " + id + "");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasTicket(String UUID) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT UUID FROM Tickets ");
            while (rs.next()) {
                if (UUID.equals(rs.getString("UUID"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getTicketOwner(int id) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT UUID FROM Tickets WHERE ID IS " + id);
            if (rs.next()) {
                return rs.getString("UUID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasModerator(String UUID) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT UUID FROM ModRanking ");
            while (rs.next()) {
                if (UUID.equals(rs.getString("UUID"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addModerator(String UUID) {
        try {
            Statement stmt = getConnection().createStatement();
            stmt.executeUpdate("INSERT INTO ModRanking (UUID,POINTS) VALUES (" + "'" + UUID + "'" + ",0)");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printModerators(CommandSender p) {
        p.sendMessage(ChatColor.GOLD + "Ranking: ");
        OfflinePlayer[] players = getPlugin().getServer().getOfflinePlayers().clone();
        int count = 0;
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ModRanking ORDER BY POINTS DESC ");
            while (rs.next()) {
                count++;
                String name = null;
                for (int i = 0; i < players.length; i++) {
                    if (players[i].getUniqueId().toString().equals(rs.getString("UUID"))) {
                        name = players[i].getName();
                        break;
                    }
                }
                p.sendMessage(ChatColor.AQUA + "" + count + ". " + name + " " + rs.getString("POINTS"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incModPoints(String UUID, int points) {
        try {
            Statement stmt = getConnection().createStatement();
            stmt.executeUpdate("UPDATE ModRanking SET POINTS = POINTS + " + points + " where UUID = '" + UUID + "'");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTables() {
        try {
            Statement stmt = getConnection().createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS Tickets (" + "  ID INTEGER PRIMARY KEY,"
                    + "  UUID VARCHAR(45) NOT NULL," + "  MESSAGE VARCHAR(300) NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS ModRanking (" + "  ID INTEGER PRIMARY KEY,"
                    + "  UUID VARCHAR(45) NOT NULL," + "  POINTS INT NOT NULL);");
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
                    .getConnection("jdbc:sqlite:" + getPlugin().getDataFolder().getAbsolutePath() + "/Tickets.db"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
