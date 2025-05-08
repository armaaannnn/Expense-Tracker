package com.example.expensetrackerarihant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.dateTextView.setText(transaction.getDate());
        holder.categoryTextView.setText(transaction.getCategory());
        holder.descriptionTextView.setText(transaction.getDescription());
        holder.amountTextView.setText("₹" + String.valueOf(transaction.getAmount()));

        if (transaction.getType().equals("Income")) {
            holder.amountTextView.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.amountTextView.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, categoryTextView, descriptionTextView, amountTextView;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
        }
    }
}
