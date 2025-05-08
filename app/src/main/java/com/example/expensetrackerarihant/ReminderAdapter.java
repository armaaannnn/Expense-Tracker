package com.example.expensetrackerarihant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;

    public ReminderAdapter(List<Reminder> reminderList) {
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.dateText.setText(reminder.getDate());
        holder.timeText.setText(reminder.getTime());
        holder.categoryText.setText(reminder.getCategory());
        holder.descriptionText.setText(reminder.getDescription());
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, timeText, categoryText, descriptionText;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.itemDate);
            timeText = itemView.findViewById(R.id.itemTime);
            categoryText = itemView.findViewById(R.id.itemCategory);
            descriptionText = itemView.findViewById(R.id.itemDescription);
        }
    }
}
