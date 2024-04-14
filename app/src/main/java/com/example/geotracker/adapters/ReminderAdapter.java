package com.example.geotracker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geotracker.databinding.ReminderItemBinding;
import com.example.geotracker.model.ReminderEntity;

import java.util.ArrayList;
import java.util.List;

// Adapter for the reminder recycler view
public class ReminderAdapter  extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>{
    private static final String LOG_TAG = ReminderAdapter.class.getSimpleName(); // Log tag

    private List<ReminderEntity> reminders; // List of reminders
    private OnItemClickListener listener; // Listener for onClick events

    // Constructor
    public ReminderAdapter() {
        this.reminders = new ArrayList<>();
    }

    // Set up view holder
    @NonNull
    @Override
    public ReminderAdapter.ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Set up data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ReminderItemBinding binding = ReminderItemBinding.inflate(layoutInflater, parent, false);

        // Return view holder
        return new ReminderViewHolder(binding);
    }

    // Bind view holder
    @Override
    public void onBindViewHolder(@NonNull ReminderAdapter.ReminderViewHolder holder, int position) {
        // Set up data binding
        ReminderEntity reminder = reminders.get(position);
        holder.binding.setReminder(reminder);
        holder.binding.executePendingBindings();

        // Set up onClick listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) { // If listener is not null and position is valid
                // Call listener
                listener.onItemClick(reminder);
            }
        });
    }

    // Get number of reminders
    @Override
    public int getItemCount() {
        return reminders.size();
    }

    // Set reminders
    public void setReminders(List<ReminderEntity> reminders) {
        this.reminders = reminders;
        notifyDataSetChanged();
    }

    // Get reminder at position
    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        ReminderItemBinding binding; // Data binding

        // Constructor
        public ReminderViewHolder(ReminderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // On click listener interface
    public interface OnItemClickListener {
        void onItemClick(ReminderEntity reminder);
    }

    // Set on click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
