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
import edu.uga.cs.ridesharing.model.RideOffer;

public class RideOfferAdapter extends RecyclerView.Adapter<RideOfferAdapter.RideOfferViewHolder> {

    private List<RideOffer> rideOffers;
    private OnRideOfferClickListener listener;

    public interface OnRideOfferClickListener {
        void onRideOfferClick(RideOffer rideOffer);
    }

    public RideOfferAdapter(List<RideOffer> rideOffers, OnRideOfferClickListener listener) {
        this.rideOffers = rideOffers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RideOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_offer_item, parent, false);
        return new RideOfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RideOfferViewHolder holder, int position) {
        RideOffer rideOffer = rideOffers.get(position);
        holder.dateTextView.setText("Date: " + rideOffer.getDate());
        holder.timeTextView.setText("Time: " + rideOffer.getTime());
        holder.fromToTextView.setText("From: " + rideOffer.getFrom() + " To: " + rideOffer.getTo());

        holder.acceptButton.setOnClickListener(v -> listener.onRideOfferClick(rideOffer));
    }

    @Override
    public int getItemCount() {
        return rideOffers.size();
    }

    static class RideOfferViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, timeTextView, fromToTextView;
        Button acceptButton;

        public RideOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            fromToTextView = itemView.findViewById(R.id.fromToTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
        }
    }
}
