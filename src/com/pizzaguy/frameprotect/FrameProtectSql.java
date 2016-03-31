package com.pizzaguy.frameprotect;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.pizzaguy.plugin.Plugin;
import com.pizzaguy.sql.DefaultSql;

public class FrameProtectSql extends DefaultSql {

    public FrameProtectSql(Plugin plugin) {
        super(plugin);
    }

    public boolean addFrame(Frame frame) {
        try {
            PreparedStatement pstmt = getConnection().prepareStatement("INSERT INTO ItemFrames (X,Y,Z,OWNER) VALUES(?,?,?,?)");
            pstmt.setInt(1, frame.getX());
            pstmt.setInt(2, frame.getY());
            pstmt.setInt(3, frame.getZ());
            pstmt.setString(4, frame.getOwner().toString());
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Frame getFrame(int x, int y, int z) {
        try {
            PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM ItemFrames WHERE X = ? AND Y = ? AND Z = ?");
            pstmt.setInt(1, x);
            pstmt.setInt(2, y);
            pstmt.setInt(3, z);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return new Frame(rs.getInt(1), rs.getInt(2), rs.getInt(3), UUID.fromString(rs.getString(4)));
            else
                return null;
        } catch (SQLException e) {
            return null;
        }

    }

    public boolean hasFrame(Frame frame) {
        try {
            PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM ItemFrames WHERE X = ? AND Y = ? AND Z = ?");
            pstmt.setInt(1, frame.getX());
            pstmt.setInt(2, frame.getY());
            pstmt.setInt(3, frame.getZ());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                rs.close();
                pstmt.close();
                return true;
            } else {
                rs.close();
                pstmt.close();
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean removeFrame(Frame frame) {
        try {
            PreparedStatement pstmt = getConnection().prepareStatement("DELETE FROM ItemFrames WHERE X = ? AND Y = ? AND Z = ?");
            pstmt.setInt(1, frame.getX());
            pstmt.setInt(2, frame.getY());
            pstmt.setInt(3, frame.getZ());
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void createTables() {
        try {
            Statement stmt = getConnection().createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS ItemFrames (" + " X INTEGER NOT NULL," + " Y INTEGER NOT NULL," + " Z INTEGER NOT NULL," + " OWNER VARCHAR(45) NOT NULL," + " PRIMARY KEY(X,Y,Z));");
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
            setConnection(DriverManager.getConnection("jdbc:sqlite:" + getPlugin().getDataFolder().getAbsolutePath() + "/FrameProtect.db"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
