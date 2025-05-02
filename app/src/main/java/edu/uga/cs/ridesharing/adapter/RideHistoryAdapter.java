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
/**
 * Adapter class for showing past accepted rides.
 * Each item shows ride date, time, route, and user's role (Driver/Rider).
 */
public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideHistoryViewHolder> {

    private List<AcceptedRide> rideHistory;
    /**
     * Constructor.
     * @param rideHistory List of completed (accepted) rides.
     */
    public RideHistoryAdapter(List<AcceptedRide> rideHistory) {
        this.rideHistory = rideHistory;
    }
    /**
     * Creates a new ViewHolder for a ride history item.
     * @param parent   Parent ViewGroup used to add new View.
     * @param viewType View type of new View.
     * @return New RideHistoryViewHolder.
     */
    @NonNull
    @Override
    public RideHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_history_item, parent, false);
        return new RideHistoryViewHolder(itemView);
    }
    /**
     * Binds an AcceptedRide's data to a ViewHolder.
     * @param holder   ViewHolder to bind data.
     * @param position Position in dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull RideHistoryViewHolder holder, int position) {
        AcceptedRide ride = rideHistory.get(position);

        holder.dateTextView.setText("Date: " + ride.getDate());
        holder.timeTextView.setText("Time: " + ride.getTime());
        holder.fromToTextView.setText("From: " + ride.getFrom() + " To: " + ride.getTo());

        String currentUID = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUID.equals(ride.getDriverUID())) {
            holder.roleTextView.setText("Role: Driver");
        } else if (currentUID.equals(ride.getRiderUID())) {
            holder.roleTextView.setText("Role: Rider");
        } else {
            holder.roleTextView.setText("Role: Unknown");
        }
    }
    /**
     * Returns total number of accepted rides in history.
     * @return Size of rideHistory list.
     */
    @Override
    public int getItemCount() {
        return rideHistory.size();
    }
    /**
     * ViewHolder to show a single ride history entry.
     */
    static class RideHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, timeTextView, fromToTextView, roleTextView;
        /**
         * Constructor binds UI components for ride history item.
         * @param itemView View representing a single ride item.
         */
        public RideHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            fromToTextView = itemView.findViewById(R.id.fromToTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
        }
    }
}
