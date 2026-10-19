package com.yourdomain;

import com.yourdomain.dao.DatabaseConnection;
import com.yourdomain.dao.GymMembershipDao;
import com.yourdomain.ui.GymMembershipUI;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        GymMembershipDao membershipDao = new GymMembershipDao(databaseConnection);

        try {
            membershipDao.initializeDatabase();
        } catch (IllegalStateException e) {
            System.err.println("Could not initialize the database: " + e.getMessage());
            return;
        }

        System.out.println("Database file: " + databaseConnection.getDatabasePath());

        GymMembershipUI ui = new GymMembershipUI(membershipDao);
        ui.start();
    }
}