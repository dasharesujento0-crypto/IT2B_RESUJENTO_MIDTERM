package com.yourdomain.dao;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DATABASE_FILE_NAME = "gym_membership.db";

    private final String databasePath;
    private final String jdbcUrl;

    public DatabaseConnection() {
        Path projectRoot = findProjectRoot();
        this.databasePath = projectRoot.resolve(DATABASE_FILE_NAME).toAbsolutePath().toString();
        this.jdbcUrl = "jdbc:sqlite:" + databasePath;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
        return connection;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    private static Path findProjectRoot() {
        try {
            File location = new File(DatabaseConnection.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI());
            File dir = location.isDirectory() ? location : location.getParentFile();
            while (dir != null) {
                if (new File(dir, "pom.xml").isFile()) {
                    return dir.toPath();
                }
                dir = dir.getParentFile();
            }
        } catch (URISyntaxException | SecurityException | NullPointerException e) {
            return workingDirectory();
        }
        return workingDirectory();
    }

    private static Path workingDirectory() {
        return Paths.get(System.getProperty("user.dir"));
    }
}