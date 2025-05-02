package edu.uga.cs.ridesharing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

import edu.uga.cs.ridesharing.R;
import edu.uga.cs.ridesharing.model.AcceptedRide;

/**
 * Adapter class used to display and interact with a list of accepted rides.
 * Shows ride info and allows user to confirm a ride.
 */
public class AcceptedRideAdapter extends RecyclerView.Adapter<AcceptedRideAdapter.AcceptedRideViewHolder> {

    private List<AcceptedRide> acceptedRides;
    private Context context;

    /**
     * Constructor.
     * @param context
     * @param acceptedRides
     */
    public AcceptedRideAdapter(Context context, List<AcceptedRide> acceptedRides) {
        this.context = context;
        this.acceptedRides = acceptedRides;
    }

    @NonNull
    @Override
    public AcceptedRideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.accepted_ride_item, parent, false);
        return new AcceptedRideViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AcceptedRideViewHolder holder, int position) {
        AcceptedRide ride = acceptedRides.get(position);
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.dateTextView.setText("Date: " + ride.getDate());
        holder.timeTextView.setText("Time: " + ride.getTime());
        holder.fromToTextView.setText("From: " + ride.getFrom() + " To: " + ride.getTo());
        // Set role
        if (currentUID.equals(ride.getDriverUID())) {
            holder.roleTextView.setText("Role: Driver");
        } else if (currentUID.equals(ride.getRiderUID())) {
            holder.roleTextView.setText("Role: Rider");
        } else {
            holder.roleTextView.setText("Role: Unknown");
        }

        holder.confirmButton.setOnClickListener(v -> confirmRide(ride));
    }

    @Override
    public int getItemCount() {
        return acceptedRides.size();
    }

    /**
     * ViewHolder for each accepted ride in the RecyclerView.
     */

    static class AcceptedRideViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, timeTextView, fromToTextView, roleTextView;
        Button confirmButton;

        public AcceptedRideViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            fromToTextView = itemView.findViewById(R.id.fromToTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView); // NEW: role view
            confirmButton = itemView.findViewById(R.id.confirmButton);
        }
    }

    /**
     * Confirms the ride for the current user and checks whether both parties have confirmed the ride.
     * If both users confirm it moves the ride to history and exchanges points.
     * @param ride
     */
    private void confirmRide(AcceptedRide ride) {
        DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("acceptedRides").child(ride.getAcceptedRideID());
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (currentUID.equals(ride.getDriverUID())) {
            rideRef.child("confirmedByDriver").setValue(true);
        } else if (currentUID.equals(ride.getRiderUID())) {
            rideRef.child("confirmedByRider").setValue(true);
        }

        // After setting confirmation, listen for changes
        rideRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                AcceptedRide updatedRide = snapshot.getValue(AcceptedRide.class);
                if (updatedRide != null) {
                    updatedRide.setAcceptedRideID(snapshot.getKey());

                    if (updatedRide.isConfirmedByDriver() && updatedRide.isConfirmedByRider()) {
                        adjustRidePointsAndMoveToHistory(updatedRide);
                        Toast.makeText(
                                context,
                                "Ride confirmed successfully! Points updated.",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                                context,
                                "Waiting for the other user to confirm.",
                                Toast.LENGTH_SHORT
                        ).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
            }
        });
    }
    /**
     * Adjusts points for both users and move the ride to history.
     * Delete accepted ride from the active list.
     * @param ride The ride that was fully confirmed.
     */
    private void adjustRidePointsAndMoveToHistory(AcceptedRide ride) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("rideHistory");
        DatabaseReference acceptedRideRef = FirebaseDatabase.getInstance().getReference("acceptedRides").child(ride.getAcceptedRideID());

        // add points
        usersRef.child(ride.getDriverUID()).child("points").get().addOnSuccessListener(snapshot -> {
            Integer points = snapshot.getValue(Integer.class);
            if (points == null) points = 0;
            usersRef.child(ride.getDriverUID()).child("points").setValue(points + 50);
        });

        // deduct points
        usersRef.child(ride.getRiderUID()).child("points").get().addOnSuccessListener(snapshot -> {
            Integer points = snapshot.getValue(Integer.class);
            if (points == null) points = 0;
            usersRef.child(ride.getRiderUID()).child("points").setValue(points - 50);
        });
        //move to history and remove from accepted ride
        historyRef.push().setValue(ride)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        acceptedRideRef.removeValue();
                    }
                });
    }
}
