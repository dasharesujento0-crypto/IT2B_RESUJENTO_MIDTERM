package com.yourdomain.model;

/**
 * Plain data object for one gym member. No SQL or JDBC code here.
 */
public class GymMember {

    private final int id;            // 0 means "not saved yet" (SQLite assigns the real ID)
    private final String fullName;
    private final String tier;       // BASIC, PREMIUM, or VIP
    private final double monthlyFee;
    private final String status;     // ACTIVE or INACTIVE

    /** Used for a new member that has not been saved yet (no ID). */
    public GymMember(String fullName, String tier, double monthlyFee, String status) {
        this(0, fullName, tier, monthlyFee, status);
    }

    /** Used for a member loaded from the database (has an ID). */
    public GymMember(int id, String fullName, String tier, double monthlyFee, String status) {
        this.id = id;
        this.fullName = fullName;
        this.tier = tier;
        this.monthlyFee = monthlyFee;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTier() {
        return tier;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    public String getStatus() {
        return status;
    }
}
