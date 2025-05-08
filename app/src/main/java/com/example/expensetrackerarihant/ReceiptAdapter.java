package com.example.expensetrackerarihant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {

    private List<Receipt> originalList;
    private List<Receipt> filteredList;

    public ReceiptAdapter(List<Receipt> receiptList) {
        this.originalList = new ArrayList<>(receiptList);
        this.filteredList = new ArrayList<>(receiptList);
    }

    @NonNull
    @Override
    public ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receipt, parent, false);
        return new ReceiptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptViewHolder holder, int position) {
        Receipt receipt = filteredList.get(position);
        holder.categoryText.setText(receipt.getCategory());
        holder.amountText.setText("₹ " + receipt.getAmount());
        holder.dateText.setText(receipt.getDate());
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ReceiptViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText, amountText, dateText;

        public ReceiptViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.receiptCategory);
            amountText = itemView.findViewById(R.id.receiptAmount);
            dateText = itemView.findViewById(R.id.receiptDate);
        }
    }

    // Updates both original and filtered lists with new Firestore data
    public void setOriginalList(List<Receipt> newList) {
        this.originalList = new ArrayList<>(newList);
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    // Filters receipts by text (category, date, or amount)
    public void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            String lowerText = text.toLowerCase();
            for (Receipt receipt : originalList) {
                if (receipt.getCategory().toLowerCase().contains(lowerText)
                        || receipt.getDate().toLowerCase().contains(lowerText)
                        || String.valueOf(receipt.getAmount()).contains(lowerText)) {
                    filteredList.add(receipt);
                }
            }
        }
        notifyDataSetChanged();
    }
}
