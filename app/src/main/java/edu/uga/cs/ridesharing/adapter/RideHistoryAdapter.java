package edu.uga.cs.ridesharing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.ridesharing.R;
import edu.uga.cs.ridesharing.model.AcceptedRide;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideHistoryViewHolder> {

    private List<AcceptedRide> rideHistory;

    public RideHistoryAdapter(List<AcceptedRide> rideHistory) {
        this.rideHistory = rideHistory;
    }

    @NonNull
    @Override
    public RideHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_history_item, parent, false);
        return new RideHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RideHistoryViewHolder holder, int position) {
        AcceptedRide ride = rideHistory.get(position);

        holder.dateTextView.setText("Date: " + ride.getDate());
        holder.timeTextView.setText("Time: " + ride.getTime());
        holder.fromToTextView.setText("From: " + ride.getFrom() + " To: " + ride.getTo());

        // Show whether you were the Driver or Rider
        String currentUID = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUID.equals(ride.getDriverUID())) {
            holder.roleTextView.setText("Role: Driver");
        } else if (currentUID.equals(ride.getRiderUID())) {
            holder.roleTextView.setText("Role: Rider");
        } else {
            holder.roleTextView.setText("Role: Unknown");
        }
    }

    @Override
    public int getItemCount() {
        return rideHistory.size();
    }

    static class RideHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, timeTextView, fromToTextView, roleTextView;

        public RideHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            fromToTextView = itemView.findViewById(R.id.fromToTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
        }
    }
}
