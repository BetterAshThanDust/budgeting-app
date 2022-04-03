package com.example.budget.transactions;

import android.content.res.Resources;

import com.example.budget.R;

import java.util.Locale;

public class TransactionModel {
    private int ID;
    private boolean isIncome;
    // TODO: Need to change this to Date format eventually.
    private String date;
    private String description;
    private double amount;

    public TransactionModel(int ID, boolean isIncome, String date, String description, double amount) {
        this.ID = ID;
        this.isIncome = isIncome;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public int getID() {
        return ID;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public String getDate() {
        return date;
    }

    public String getDateString() {
        String month = Months.values()[Integer.parseInt(date.substring(5, 7)) % 12].toString();
        return month +  " " + date.substring(0, 4);
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s%n%s, £%.2f", this.getDateString(), description, amount);
    }

    private enum Months {
        DECEMBER,
        JANUARY,
        FEBRUARY,
        MARCH,
        APRIL,
        MAY,
        JUNE,
        JULY,
        AUGUST,
        SEPTEMBER,
        OCTOBER,
        NOVEMBER
    }
}
