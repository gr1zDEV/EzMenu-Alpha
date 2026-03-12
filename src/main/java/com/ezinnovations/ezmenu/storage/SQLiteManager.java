package com.ezinnovations.ezmenu.storage;

import com.ezinnovations.ezmenu.EzMenu;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SQLiteManager {

    private final EzMenu plugin;
    private Connection connection;

    public SQLiteManager(EzMenu plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        File databaseFile = new File(plugin.getDataFolder(), "database.db");
        String jdbcUrl = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        this.connection = DriverManager.getConnection(jdbcUrl);
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}
