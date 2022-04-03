package com.example.budget.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budget.R;
import com.example.budget.transactions.TransactionModel;

import java.util.ArrayList;

public class TransactionRecyclerViewAdapter extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder> {

    private ArrayList<TransactionModel> transactions = new ArrayList<>();

    public TransactionRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_transaction.setText(transactions.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(ArrayList<TransactionModel> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public ArrayList<TransactionModel> getTransactions() {
        return transactions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txt_transaction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_transaction = itemView.findViewById(R.id.txt_transaction);
        }
    }
}
