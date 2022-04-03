package com.example.budget.recyclerview;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budget.R;
import com.example.budget.helpers.StatisticsHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class OverallStatsRecyclerViewAdapter extends RecyclerView.Adapter<OverallStatsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> uniqueDatesSet = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_overall, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.stats_overall_date.setText(uniqueDatesSet.get(position));
        holder.stats_overall_amounts.setText(getStatsOverallAmountsString(uniqueDatesSet.get(position)));
    }

    private String getStatsOverallAmountsString(String s) {
        double income = StatisticsHelper.getInstance().getIncomeMap().getOrDefault(s, 0.0);
        double outcome = StatisticsHelper.getInstance().getOutcomeMap().getOrDefault(s, 0.0);
        return String.format(Locale.ENGLISH, "In: £%.2f%nOut: £%.2f%nSaved: £%.2f", income, outcome, income - outcome);
    }

    @Override
    public int getItemCount() {
        return uniqueDatesSet.size();
    }

    public void setUniqueDates() {
        for (Map.Entry<String, Double> entry : StatisticsHelper.getInstance().getIncomeMap().entrySet()) {
            if (!uniqueDatesSet.contains(entry.getKey()))
                uniqueDatesSet.add(entry.getKey());
        }

        for (Map.Entry<String, Double> entry : StatisticsHelper.getInstance().getOutcomeMap().entrySet()) {
            if (!uniqueDatesSet.contains(entry.getKey()))
                uniqueDatesSet.add(entry.getKey());
        }

        uniqueDatesSet.sort((o1, o2) -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMMMM yyyy");
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdf.parse(o1);
                d2 = sdf.parse(o2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return d2.compareTo(d1);
        });

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView stats_overall_date, stats_overall_amounts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stats_overall_amounts = itemView.findViewById(R.id.stats_overall_amounts);
            stats_overall_date = itemView.findViewById(R.id.stats_overall_date);
        }
    }
}
