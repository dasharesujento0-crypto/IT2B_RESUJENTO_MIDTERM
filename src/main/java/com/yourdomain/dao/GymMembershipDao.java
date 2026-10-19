package com.yourdomain.dao;

import com.yourdomain.model.GymMember;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: the ONLY class that contains SQL statements.
 * The UI calls these methods and never touches JDBC.
 */
public class GymMembershipDao {

    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS members ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "full_name TEXT NOT NULL, "
                    + "tier TEXT NOT NULL CHECK (tier IN ('BASIC', 'PREMIUM', 'VIP')), "
                    + "monthly_fee REAL NOT NULL CHECK (monthly_fee >= 0), "
                    + "status TEXT NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE'))"
                    + ")";

    private static final String INSERT_SQL =
            "INSERT INTO members (full_name, tier, monthly_fee, status) VALUES (?, ?, ?, ?)";

    private static final String SELECT_ALL_SQL =
            "SELECT id, full_name, tier, monthly_fee, status FROM members ORDER BY id ASC";

    private final DatabaseConnection databaseConnection;

    public GymMembershipDao(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Creates the members table if it does not exist yet.
     * Existing records are never deleted.
     *
     * @throws IllegalStateException if the database cannot be initialized
     */
    public void initializeDatabase() {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Inserts one member using a parameterized PreparedStatement.
     *
     * @return true only if exactly one row was actually inserted; false otherwise
     */
    public boolean addMember(GymMember member) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {

            statement.setString(1, member.getFullName());
            statement.setString(2, member.getTier());
            statement.setDouble(3, member.getMonthlyFee());
            statement.setString(4, member.getStatus());

            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error while saving member: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads every member from the database, ordered by ID ascending.
     *
     * @throws IllegalStateException if the records cannot be read
     */
    public List<GymMember> getAllMembers() {
        List<GymMember> members = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                members.add(new GymMember(
                        resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("tier"),
                        resultSet.getDouble("monthly_fee"),
                        resultSet.getString("status")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        return members;
    }
}
