package edu.uga.cs.ridesharing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import edu.uga.cs.ridesharing.ManageMyRidesFragment;
import edu.uga.cs.ridesharing.R;
import edu.uga.cs.ridesharing.model.RideOffer;
import edu.uga.cs.ridesharing.model.RideRequest;

/**
 * Adapter to show and manage the user unaccepted ride offer/requests. Allows user
 * to edit and delete  rides.
 */
public class ManageRideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_OFFER = 0;
    private static final int TYPE_REQUEST = 1;

    private List<RideOffer> rideOffers;
    private List<RideRequest> rideRequests;
    private ManageMyRidesFragment fragment;

    /**
     * Constructor
     * @param rideOffers
     * @param rideRequests
     * @param fragment
     */
    public ManageRideAdapter(List<RideOffer> rideOffers, List<RideRequest> rideRequests, ManageMyRidesFragment fragment) {
        this.rideOffers = rideOffers;
        this.rideRequests = rideRequests;
        this.fragment = fragment;
    }

    @Override
    public int getItemViewType(int position) {
        if (rideOffers != null && position < rideOffers.size()) {
            return TYPE_OFFER;
        } else {
            return TYPE_REQUEST;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_ride_item, parent, false);

        if (viewType == TYPE_OFFER) {
            return new RideOfferViewHolder(view);
        } else {
            return new RideRequestViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_OFFER) {
            RideOfferViewHolder offerHolder = (RideOfferViewHolder) holder;
            RideOffer offer = rideOffers.get(position);
            offerHolder.bind(offer);
        } else {
            int reqPosition = position - (rideOffers != null ? rideOffers.size() : 0);
            RideRequestViewHolder requestHolder = (RideRequestViewHolder) holder;
            RideRequest request = rideRequests.get(reqPosition);
            requestHolder.bind(request);
        }
    }

    @Override
    public int getItemCount() {
        int offersCount = (rideOffers != null) ? rideOffers.size() : 0;
        int requestsCount = (rideRequests != null) ? rideRequests.size() : 0;
        return offersCount + requestsCount;
    }

    class RideOfferViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, timeTextView, fromToTextView;
        Button editButton, deleteButton;
        /**
         * ViewHolder to display ride offer with edit and delete.
         */
        public RideOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            fromToTextView = itemView.findViewById(R.id.fromToTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
        /**
         * Bind the ride offer data to the views and sets up button listeners.
         *
         * @param offer RideOffer object to bind.
         */
        public void bind(RideOffer offer) {
            dateTextView.setText("Date: " + offer.getDate());
            timeTextView.setText("Time: " + offer.getTime());
            fromToTextView.setText("From: " + offer.getFrom() + " To: " + offer.getTo());

            editButton.setOnClickListener(v -> fragment.editRideOffer(offer));
            deleteButton.setOnClickListener(v -> fragment.deleteRideOffer(offer));
        }
    }
    /**
     * ViewHolder to display a ride request with edit and delete.
     */
    class RideRequestViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, timeTextView, fromToTextView;
        Button editButton, deleteButton;

        public RideRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            fromToTextView = itemView.findViewById(R.id.fromToTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        /**
         * Binds the RideRequest data to the views and sets up button listeners.
         *
         * @param request RideRequest object to bind.
         */
        public void bind(RideRequest request) {
            dateTextView.setText("Date: " + request.getDate());
            timeTextView.setText("Time: " + request.getTime());
            fromToTextView.setText("From: " + request.getFrom() + " To: " + request.getTo());

            editButton.setOnClickListener(v -> fragment.editRideRequest(request));
            deleteButton.setOnClickListener(v -> fragment.deleteRideRequest(request));
        }
    }
}
