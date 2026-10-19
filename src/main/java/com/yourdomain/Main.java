package com.yourdomain;

import com.yourdomain.dao.DatabaseConnection;
import com.yourdomain.dao.GymMembershipDao;
import com.yourdomain.ui.GymMembershipUI;

/**
 * Single entry point of the application.
 * It only wires the layers together: DatabaseConnection -> DAO -> UI.
 * No SQL or JDBC code belongs here.
 */
public class Main {
    public static void main(String[] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        GymMembershipDao membershipDao = new GymMembershipDao(databaseConnection);

        try {
            membershipDao.initializeDatabase();   // creates the table only if it does not exist
        } catch (IllegalStateException e) {
            System.err.println("Could not initialize the database: " + e.getMessage());
            return;
        }

        System.out.println("Database file: " + databaseConnection.getDatabasePath());

        GymMembershipUI ui = new GymMembershipUI(membershipDao);
        ui.start();
    }
}
