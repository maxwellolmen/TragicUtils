package com.pizzaguy.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.pizzaguy.plugin.Plugin;

public abstract class DefaultSql {

    private Connection c = null;
    private Plugin plugin;

    public DefaultSql(Plugin plugin){
        this.plugin = plugin;
        this.connect();
        this.createTables();
    }
    
    public void close() {
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract void createTables();

    public abstract void connect();

    public Connection getConnection() {
        return c;
    }

    public void setConnection(Connection c) {
        this.c = c;
    }

    public Plugin getPlugin() {
        return plugin;
    }
    
}
