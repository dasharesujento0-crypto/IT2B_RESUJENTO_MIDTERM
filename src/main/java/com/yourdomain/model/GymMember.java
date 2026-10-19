package com.yourdomain.model;

public class GymMember {

    private final int id;
    private final String fullName;
    private final String tier;
    private final double monthlyFee;
    private final String status;

    public GymMember(String fullName, String tier, double monthlyFee, String status) {
        this(0, fullName, tier, monthlyFee, status);
    }

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