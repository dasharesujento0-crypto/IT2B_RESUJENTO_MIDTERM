package com.yourdomain.ui;

import com.yourdomain.dao.GymMembershipDao;
import com.yourdomain.model.GymMember;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class GymMembershipUI {

    private static final int MAX_NAME_LENGTH = 100;

    private final GymMembershipDao membershipDao;
    private final Scanner scanner = new Scanner(System.in);

    public GymMembershipUI(GymMembershipDao membershipDao) {
        this.membershipDao = membershipDao;
    }

    public void start() {
        boolean running = true;

        try {
            while (running) {
                printMenu();
                String choice = readLine("Enter your choice: ").trim();

                switch (choice) {
                    case "1":
                        addNewMember();
                        break;
                    case "2":
                        viewAllMembers();
                        break;
                    case "3":
                        System.out.println();
                        System.out.println("Thank you for using the Gym Membership Management System. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Input closed. Exiting system.");
        } finally {
            scanner.close();
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("GYM MEMBERSHIP MANAGEMENT");
        System.out.println();
        System.out.println("[1] Add New Member");
        System.out.println("[2] View All Members");
        System.out.println("[3] Exit System");
        System.out.println();
    }

    private void addNewMember() {
        System.out.println();
        System.out.println("--- Add New Member ---");

        String fullName = readFullName();
        String tier = readTier();
        double monthlyFee = readMonthlyFee();
        String status = readStatus();

        GymMember member = new GymMember(fullName, tier, monthlyFee, status);

        if (membershipDao.addMember(member)) {
            System.out.println("Member added successfully!");
        } else {
            System.out.println("Failed to add member. Nothing was saved.");
        }
    }

    private String readFullName() {
        while (true) {
            String input = readLine("Full Name: ").trim();
            if (input.isEmpty()) {
                System.out.println("Full name cannot be blank. Please try again.");
            } else if (input.length() > MAX_NAME_LENGTH) {
                System.out.println("Full name is too long (maximum " + MAX_NAME_LENGTH + " characters).");
            } else {
                return input;
            }
        }
    }

    private String readTier() {
        while (true) {
            String input = readLine("Tier (BASIC, PREMIUM, or VIP): ").trim().toUpperCase(Locale.ROOT);
            if (input.equals("BASIC") || input.equals("PREMIUM") || input.equals("VIP")) {
                return input;
            }
            System.out.println("Invalid tier. Please enter BASIC, PREMIUM, or VIP.");
        }
    }

    private double readMonthlyFee() {
        while (true) {
            String input = readLine("Monthly Fee: ").trim();
            if (!input.matches("-?\\d+(\\.\\d+)?")) {
                System.out.println("Invalid number. Please enter a valid amount, e.g. 500 or 1200.50.");
                continue;
            }
            try {
                double fee = Double.parseDouble(input);
                if (Double.isNaN(fee) || Double.isInfinite(fee)) {
                    System.out.println("Invalid number. Please enter a valid amount, e.g. 500 or 1200.50.");
                } else if (fee < 0) {
                    System.out.println("Monthly fee cannot be negative.");
                } else {
                    return fee;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid amount, e.g. 500 or 1200.50.");
            }
        }
    }

    private String readStatus() {
        while (true) {
            String input = readLine("Status (ACTIVE or INACTIVE): ").trim().toUpperCase(Locale.ROOT);
            if (input.equals("ACTIVE") || input.equals("INACTIVE")) {
                return input;
            }
            System.out.println("Invalid status. Please enter ACTIVE or INACTIVE.");
        }
    }

    private void viewAllMembers() {
        List<GymMember> members;
        try {
            members = membershipDao.getAllMembers();
        } catch (IllegalStateException e) {
            System.out.println("Could not load members: " + e.getMessage());
            return;
        }

        System.out.println();
        if (members.isEmpty()) {
            System.out.println("No members found. Add a member first using option [1].");
            return;
        }

        printTable(members);
        System.out.println("Total records: " + members.size());
    }

    private void printTable(List<GymMember> members) {
        String[] headers = {"ID", "FULL NAME", "TIER", "MONTHLY FEE", "STATUS"};
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }

        String[][] rows = new String[members.size()][];
        for (int r = 0; r < members.size(); r++) {
            GymMember m = members.get(r);
            rows[r] = new String[]{
                    String.valueOf(m.getId()),
                    m.getFullName(),
                    m.getTier(),
                    String.format(Locale.US, "%.2f", m.getMonthlyFee()),
                    m.getStatus()
            };
            for (int c = 0; c < rows[r].length; c++) {
                widths[c] = Math.max(widths[c], rows[r][c].length());
            }
        }

        String separator = buildSeparator(widths);
        System.out.println(separator);
        System.out.println(buildRow(headers, widths));
        System.out.println(separator);
        for (String[] row : rows) {
            System.out.println(buildRow(row, widths));
        }
        System.out.println(separator);
    }

    private String buildSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int width : widths) {
            sb.append("-".repeat(width + 2)).append("+");
        }
        return sb.toString();
    }

    private String buildRow(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < cells.length; i++) {
            boolean rightAlign = (i == 0 || i == 3);
            String format = rightAlign ? " %" + widths[i] + "s |" : " %-" + widths[i] + "s |";
            sb.append(String.format(format, cells[i]));
        }
        return sb.toString();
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}