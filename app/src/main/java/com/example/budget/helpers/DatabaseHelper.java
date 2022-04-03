package com.example.budget.helpers;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.budget.transactions.TransactionModel;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TRANSACTION_TABLE = "TRANSACTION_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_TRANSACTION_TYPE = "TRANSACTION_TYPE";
    public static final String COLUMN_TRANSACTION_DATE = "TRANSACTION_DATE";
    public static final String COLUMN_TRANSACTION_DESCRIPTION = "TRANSACTION_DESCRIPTION";
    public static final String COLUMN_TRANSACTION_AMOUNT = "TRANSACTION_AMOUNT";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "transactions.db", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TRANSACTION_TABLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRANSACTION_TYPE + " BOOL, " +
                COLUMN_TRANSACTION_DATE + " DATE, " +
                COLUMN_TRANSACTION_DESCRIPTION + " TEXT, " +
                COLUMN_TRANSACTION_AMOUNT + " FLOAT)";

        db.execSQL(createTableStatement);
    }

    public boolean addTransaction(TransactionModel transactionModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TRANSACTION_TYPE, transactionModel.isIncome());
        cv.put(COLUMN_TRANSACTION_DATE, transactionModel.getDate());
        cv.put(COLUMN_TRANSACTION_DESCRIPTION, transactionModel.getDescription());
        cv.put(COLUMN_TRANSACTION_AMOUNT, transactionModel.getAmount());

        long success = db.insert(TRANSACTION_TABLE, null, cv);

        return success != -1;
    }

    public boolean deleteTransaction(TransactionModel transactionModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        int ID = transactionModel.getID();
        int result = db.delete(TRANSACTION_TABLE, COLUMN_ID + "=?", new String[]{String.valueOf(ID)});

        return result > 0;
    }

    public ArrayList<TransactionModel> getIncomeTransactions() {
        return getTransactionList(true);
    }

    public ArrayList<TransactionModel> getOutcomeTransactions() {
        return getTransactionList(false);
    }

    private ArrayList<TransactionModel> getTransactionList(boolean isIncome) {
        ArrayList<TransactionModel> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + TRANSACTION_TABLE + " ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                int ID = cursor.getInt(0);
                boolean transactionType = cursor.getInt(1) == 1;
                String transactionDate = cursor.getString(2);
                String transactionDescription = cursor.getString(3);
                double transactionAmount = cursor.getDouble(4);

                if (transactionType == isIncome) {
                    returnList.add(new TransactionModel(ID, transactionType, transactionDate, transactionDescription, transactionAmount));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return returnList;
    }

}
