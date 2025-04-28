package edu.uga.cs.ridesharing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.ridesharing.R;
import edu.uga.cs.ridesharing.model.RideRequest;

public class RideRequestAdapter extends RecyclerView.Adapter<RideRequestAdapter.RideRequestViewHolder> {

    private List<RideRequest> rideRequests;
    private OnRideRequestClickListener listener;

    public interface OnRideRequestClickListener {
        void onRideRequestClick(RideRequest rideRequest);
    }

    public RideRequestAdapter(List<RideRequest> rideRequests, OnRideRequestClickListener listener) {
        this.rideRequests = rideRequests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RideRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_request_item, parent, false);
        return new RideRequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RideRequestViewHolder holder, int position) {
        RideRequest rideRequest = rideRequests.get(position);
        holder.dateTextView.setText("Date: " + rideRequest.getDate());
        holder.timeTextView.setText("Time: " + rideRequest.getTime());
        holder.fromToTextView.setText("From: " + rideRequest.getFrom() + " To: " + rideRequest.getTo());

        holder.itemView.setOnClickListener(v -> listener.onRideRequestClick(rideRequest));
    }

    @Override
    public int getItemCount() {
        return rideRequests.size();
    }

    static class RideRequestViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, timeTextView, fromToTextView;

        public RideRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            fromToTextView = itemView.findViewById(R.id.fromToTextView);
        }
    }
}
