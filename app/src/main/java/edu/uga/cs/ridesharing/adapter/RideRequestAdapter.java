package edu.uga.cs.ridesharing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import edu.uga.cs.ridesharing.R;
import edu.uga.cs.ridesharing.model.RideRequest;
/**
 * Adapter class to show list of ride requests.
 * Each item shows ride details and lets user accept the request.
 */
public class RideRequestAdapter extends RecyclerView.Adapter<RideRequestAdapter.RideRequestViewHolder> {

    private List<RideRequest> rideRequests;
    private OnRideRequestClickListener listener;
    /**
     * Interface for handling ride request click actions.
     */
    public interface OnRideRequestClickListener {
        void onRideRequestClick(RideRequest rideRequest);
    }
    /**
     * Constructor.
     * @param rideRequests List of ride requests.
     * @param listener     Callback for click events per request.
     */
    public RideRequestAdapter(List<RideRequest> rideRequests, OnRideRequestClickListener listener) {
        this.rideRequests = rideRequests;
        this.listener = listener;
    }
    /**
     * Inflates a ride request item view.
     */
    @NonNull
    @Override
    public RideRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_request_item, parent, false);
        return new RideRequestViewHolder(itemView);
    }
    /**
     * Binds a ride request's data to UI and sets up click listeners.
     */
    @Override
    public void onBindViewHolder(@NonNull RideRequestViewHolder holder, int position) {
        RideRequest rideRequest = rideRequests.get(position);
        holder.dateTextView.setText("Date: " + rideRequest.getDate());
        holder.timeTextView.setText("Time: " + rideRequest.getTime());
        holder.fromToTextView.setText("From: " + rideRequest.getFrom() + " To: " + rideRequest.getTo());

        holder.itemView.setOnClickListener(v -> listener.onRideRequestClick(rideRequest));
        holder.acceptButton.setOnClickListener(v -> listener.onRideRequestClick(rideRequest));
    }
    /**
     * Returns total number of ride request items.
     */
    @Override
    public int getItemCount() {
        return rideRequests.size();
    }

    /**
     * ViewHolder for a single ride request item.
     */
    static class RideRequestViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, timeTextView, fromToTextView;
        Button acceptButton;
        /**
         * Binds UI elements to view holder.
         */
        public RideRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            fromToTextView = itemView.findViewById(R.id.fromToTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
        }
    }
}
