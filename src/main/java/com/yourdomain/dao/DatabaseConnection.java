package com.yourdomain.dao;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Opens connections to the SQLite database.
 *
 * DATABASE LOCATION (one predictable place):
 *   <project root>/gym_membership.db
 * where "project root" is the folder that contains pom.xml.
 *
 * The project root is found from where the compiled classes live
 * (target/classes), NOT from IntelliJ's "working directory", so the app
 * opens the SAME file no matter how or from where it is launched.
 * If pom.xml cannot be found (e.g. running from a packaged JAR), it falls
 * back to the current working directory.
 *
 * The app prints the full path at startup (see Main).
 */
public class DatabaseConnection {

    private static final String DATABASE_FILE_NAME = "gym_membership.db";

    private final String databasePath;
    private final String jdbcUrl;

    public DatabaseConnection() {
        Path projectRoot = findProjectRoot();
        this.databasePath = projectRoot.resolve(DATABASE_FILE_NAME).toAbsolutePath().toString();
        this.jdbcUrl = "jdbc:sqlite:" + databasePath;
    }

    /**
     * Opens a NEW connection with foreign-key enforcement turned ON.
     * SQLite resets this setting for every connection, so it is applied here
     * each time. The caller must close the connection (try-with-resources).
     */
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

    /** Full path of the SQLite file being used (shown at startup). */
    public String getDatabasePath() {
        return databasePath;
    }

    /** Walks up from the compiled classes' folder until it finds pom.xml. */
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
            // fall through to the fallback below
        }
        return Paths.get(System.getProperty("user.dir"));
    }
}
