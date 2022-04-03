package com.example.budget;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.budget.helpers.DatabaseHelper;
import com.example.budget.helpers.StatisticsHelper;
import com.example.budget.recyclerview.ClickListener;
import com.example.budget.recyclerview.OverallStatsRecyclerViewAdapter;
import com.example.budget.recyclerview.RecyclerTouchListener;
import com.example.budget.recyclerview.TransactionRecyclerViewAdapter;
import com.example.budget.transactions.TransactionModel;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    // references to buttons and layout controls
    TextView goalTxt;
    LinearProgressIndicator progressBar;
    Spinner sp_category, sp_monthSelector, sp_yearSelector;
    EditText et_description, et_amount;
    Button btn_add;
    ImageButton btn_stats_open;
    RecyclerView rv_income, rv_outcome;

    DatabaseHelper databaseHelper;
    StatisticsHelper statisticsHelper = StatisticsHelper.getInstance();
    TransactionRecyclerViewAdapter incomeAdapter;
    TransactionRecyclerViewAdapter outcomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goalTxt = findViewById(R.id.txt_goal);
        progressBar = findViewById(R.id.progressBar);

        sp_category = findViewById(R.id.sp_category);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.setAdapter(categoryAdapter);

        sp_monthSelector = findViewById(R.id.sp_monthSelector);
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.month_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_monthSelector.setAdapter(monthAdapter);

        sp_yearSelector = findViewById(R.id.sp_yearSelector);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_yearSelector.setAdapter(yearAdapter);

        et_description = findViewById(R.id.et_description);
        et_amount = findViewById(R.id.et_amount);
        btn_add = findViewById(R.id.btn_add);
        btn_stats_open = findViewById(R.id.btn_stats_open);
        rv_income = findViewById(R.id.rv_income);
        rv_outcome = findViewById(R.id.rv_outcome);

        databaseHelper = new DatabaseHelper(MainActivity.this);

        ArrayList<TransactionModel> incomeTransactions = databaseHelper.getIncomeTransactions();
        ArrayList<TransactionModel> outcomeTransactions = databaseHelper.getOutcomeTransactions();

        statisticsHelper.setIncomeStats(incomeTransactions);
        statisticsHelper.setOutcomeStats(outcomeTransactions);
        goalTxt.setText(statisticsHelper.getGoalString());
        progressBar.setProgress((int) (Math.round(100 * statisticsHelper.getSaved() / statisticsHelper.getGoal())), true);

        incomeAdapter = new TransactionRecyclerViewAdapter();
        outcomeAdapter = new TransactionRecyclerViewAdapter();

        incomeAdapter.setTransactions(incomeTransactions);
        outcomeAdapter.setTransactions(outcomeTransactions);

        rv_income.setAdapter(incomeAdapter);
        rv_outcome.setAdapter(outcomeAdapter);
        rv_income.setLayoutManager(new LinearLayoutManager(this));
        rv_outcome.setLayoutManager(new LinearLayoutManager(this));

        final int[] isIncome = {0};
        final String[] year = {""};
        final int[] month = {0};

        goalTxt.setOnClickListener(v -> {
            showNewGoalPopup(this.getApplicationContext(), v);
        });
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isIncome[0] = (int) parent.getItemIdAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_monthSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month[0] = (int) (parent.getItemIdAtPosition(position) + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_yearSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year[0] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_add.setOnClickListener(v -> {
            TransactionModel transactionModel = null;
            String date = getDateString(year, month);

            try {
                transactionModel = new TransactionModel(0, isIncome[0] == 1, date, et_description.getText().toString(), Double.parseDouble(et_amount.getText().toString()));
                databaseHelper.addTransaction(transactionModel);
                updateData(isIncome[0]);
                et_description.getText().clear();
                et_amount.getText().clear();
                Toast.makeText(this, transactionModel.toString(), Toast.LENGTH_SHORT).show();
            } catch (Exception ignored) { }
        });
        btn_stats_open.setOnClickListener(v -> {
            showStatsPagePopup(this, v);
        });
        rv_income.addOnItemTouchListener(new RecyclerTouchListener(this, rv_income, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                TransactionModel transactionModel = incomeAdapter.getTransactions().get(position);
                showTransactionDeletePopup(MainActivity.this, view, transactionModel);
            }
        }));
        rv_outcome.addOnItemTouchListener(new RecyclerTouchListener(this, rv_outcome, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                TransactionModel transactionModel = outcomeAdapter.getTransactions().get(position);
                showTransactionDeletePopup(MainActivity.this, view, transactionModel);
            }
        }));
    }

    private void showStatsPagePopup(Context context, View v) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.stats_page, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, false);

        ImageButton btn_stats_close = popupWindow.getContentView().findViewById(R.id.btn_stats_close);
        RecyclerView rv_stats_overall = popupWindow.getContentView().findViewById(R.id.rv_stats_overall);

        btn_stats_close.setOnClickListener(v1 -> {
            popupWindow.dismiss();
        });

        OverallStatsRecyclerViewAdapter statsAdapter = new OverallStatsRecyclerViewAdapter();
        statsAdapter.setUniqueDates();
        rv_stats_overall.setAdapter(statsAdapter);
        rv_stats_overall.setLayoutManager(new LinearLayoutManager(this));

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    private void showTransactionDeletePopup(Context context, View v, TransactionModel transactionModel) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.transaction_delete_popup, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        TextView txt_transaction = popupWindow.getContentView().findViewById(R.id.txt_transaction);
        Button btn_delete_confirm = popupWindow.getContentView().findViewById(R.id.btn_delete_confirm);
        Button btn_delete_cancel = popupWindow.getContentView().findViewById(R.id.btn_delete_cancel);

        txt_transaction.setText(transactionModel.toString());
        btn_delete_confirm.setOnClickListener(v1 -> {
            databaseHelper.deleteTransaction(transactionModel);
            updateData(transactionModel.isIncome() ? 1 : 0);
            popupWindow.dismiss();
            Toast.makeText(MainActivity.this, "Deleted transaction: " + transactionModel.toString(), Toast.LENGTH_SHORT).show();
        });
        btn_delete_cancel.setOnClickListener(v1 -> {
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    private void showNewGoalPopup(Context context, View v) {
        AtomicReference<Double> newGoal = new AtomicReference<>((double) 0);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.goal_change_popup, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        EditText et_goal_change = popupWindow.getContentView().findViewById(R.id.et_goal_change);
        Button btn_goal_change = popupWindow.getContentView().findViewById(R.id.btn_goal_change);

        btn_goal_change.setOnClickListener(v1 -> {
            try {
                newGoal.set(Double.parseDouble(et_goal_change.getText().toString()));
            } catch (NumberFormatException e) {
                newGoal.set(statisticsHelper.getGoal());
            }
            statisticsHelper.setGoal(newGoal.get());
            goalTxt.setText(statisticsHelper.getGoalString());
            progressBar.setProgress((int) (Math.round(100 * statisticsHelper.getSaved() / statisticsHelper.getGoal())));
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    private void updateData(int i) {
        ArrayList<TransactionModel> updatedList;
        if (i == 1) {
            updatedList = databaseHelper.getIncomeTransactions();
            incomeAdapter.setTransactions(updatedList);
            statisticsHelper.setIncomeStats(updatedList);
        } else if (i == 0) {
            updatedList = databaseHelper.getOutcomeTransactions();
            outcomeAdapter.setTransactions(updatedList);
            statisticsHelper.setOutcomeStats(updatedList);
        }
        goalTxt.setText(statisticsHelper.getGoalString());
        progressBar.setProgress((int) (Math.round(100 * statisticsHelper.getSaved() / statisticsHelper.getGoal())));
    }

    private String getDateString(String[] year, int[] month) {
        StringBuilder date = new StringBuilder();
        date.append(year[0]);
        date.append("-");
        if (month[0] < 10) {
            date.append(0);
        }
        date.append(month[0]);
        date.append("-01");

        return date.toString();
    }


}