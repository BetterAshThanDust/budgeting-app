package com.example.budget.helpers;

import com.example.budget.transactions.TransactionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class StatisticsHelper {
    private static StatisticsHelper INSTANCE;
    private double totalIncome, totalOutcome, saved;
    private double goal = 30000.00;
    private HashMap<String, Double> incomeMap, outcomeMap;

    private StatisticsHelper() {

    }

    public static StatisticsHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE =  new StatisticsHelper();
        }
        return INSTANCE;
    }

    public void setIncomeStats(ArrayList<TransactionModel> transactionList) {
        incomeMap = new HashMap<>();
        totalIncome = 0;
        for (TransactionModel transaction : transactionList) {
            totalIncome += transaction.getAmount();
            Double oldAmount = incomeMap.getOrDefault(transaction.getDateString(), 0.0);
            incomeMap.put(transaction.getDateString(), oldAmount + transaction.getAmount());
        }
        updateSaved();
    }

    public void setOutcomeStats(ArrayList<TransactionModel> transactionList) {
        outcomeMap = new HashMap<>();
        totalOutcome = 0;
        for (TransactionModel transaction : transactionList) {
            totalOutcome += transaction.getAmount();
            Double oldAmount = outcomeMap.getOrDefault(transaction.getDateString(), 0.0);
            outcomeMap.put(transaction.getDateString(), oldAmount + transaction.getAmount());
        }
        updateSaved();
    }

    private void updateSaved() {
        saved = totalIncome - totalOutcome;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double amount) {
        goal = amount;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalOutcome() {
        return totalOutcome;
    }

    public double getSaved() {
        return saved;
    }

    public String getGoalString() {
        return String.format(Locale.ENGLISH, "Saved so far: £%.2f out of £%.2f goal!", saved, goal);
    }

    public HashMap<String, Double> getIncomeMap() {
        return incomeMap;
    }

    public HashMap<String, Double> getOutcomeMap() {
        return outcomeMap;
    }
}
