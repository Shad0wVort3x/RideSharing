package edu.uga.cs.ridesharing;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uga.cs.ridesharing.adapter.RideRequestAdapter;
import edu.uga.cs.ridesharing.model.RideRequest;
/**
 * Fragment for displaying the ride history of the current user.
 * This includes all completed rides where the user acted as a driver or rider.
 */
public class ViewRideRequestsFragment extends Fragment implements RideRequestAdapter.OnRideRequestClickListener {

    private RecyclerView recyclerView;
    private RideRequestAdapter rideRequestAdapter;
    private List<RideRequest> rideRequestList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    /**
     * Default constructor.
     */
    public ViewRideRequestsFragment() {

    }
    /**
     * Inflates the fragment layout for ride history.
     *
     * @param inflater           LayoutInflater used to inflate views.
     * @param container          Optional parent view to attach the fragment UI to.
     * @param savedInstanceState Previously saved state, if any.
     * @return The inflated layout view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_ride_requests, container, false);
    }
    /**
     * Initializes UI components after view creation, sets up the RecyclerView,
     * toolbar, and starts loading ride history from Firebase.
     *
     * @param view               Root view of the fragment.
     * @param savedInstanceState Previously saved state, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        recyclerView = view.findViewById(R.id.rideRequestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rideRequestList = new ArrayList<>();
        rideRequestAdapter = new RideRequestAdapter(rideRequestList, this);
        recyclerView.setAdapter(rideRequestAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("rideRequests");
        mAuth = FirebaseAuth.getInstance();

        loadRideRequests();
    }
    /**
     * Retrieves ride history from Firebase where the current user was either a driver or a rider.
     * Updates the RecyclerView with the results.
     */
    private void loadRideRequests() {
        databaseReference.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rideRequestList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RideRequest rideRequest = dataSnapshot.getValue(RideRequest.class);
                    if (rideRequest != null) {
                        rideRequest.setRequestID(dataSnapshot.getKey());
                        if (rideRequest.getAcceptedBy() == null || "null".equals(rideRequest.getAcceptedBy())) {
                            rideRequestList.add(rideRequest);
                        }
                    }
                }

                Collections.sort(rideRequestList, (o1, o2) -> {
                    try {
                        String dt1 = o1.getDate() + " " + o1.getTime();
                        String dt2 = o2.getDate() + " " + o2.getTime();

                        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
                        java.util.Date dateTime1 = format.parse(dt1);
                        java.util.Date dateTime2 = format.parse(dt2);

                        return dateTime1.compareTo(dateTime2);
                    } catch (Exception e) {
                        return 0;
                    }
                });

                rideRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load ride requests", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onRideRequestClick(RideRequest rideRequest) {
        acceptRideRequest(rideRequest);
    }
    /**
     * Accepts the selected ride offer:
     * - Marks it as accepted in Firebase.
     * - Saves it to "acceptedRides".
     * - Sends a notification to the driver.
     * - Navigates back to the home screen.
     *
     * @param rideRequest The ride offer being accepted.
     */
    private void acceptRideRequest(RideRequest rideRequest) {
        String driverUID = mAuth.getCurrentUser().getUid();
        DatabaseReference rideRequestsRef = databaseReference.child(rideRequest.getRequestID());
        DatabaseReference acceptedRidesRef = FirebaseDatabase.getInstance().getReference("acceptedRides");
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

        rideRequestsRef.child("acceptedBy").setValue(driverUID)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> acceptedRide = new HashMap<>();
                        acceptedRide.put("driverUID", driverUID);
                        acceptedRide.put("riderUID", rideRequest.getRiderUID());
                        acceptedRide.put("date", rideRequest.getDate());
                        acceptedRide.put("time", rideRequest.getTime());
                        acceptedRide.put("from", rideRequest.getFrom());
                        acceptedRide.put("to", rideRequest.getTo());
                        acceptedRide.put("pointsCost", 50);
                        acceptedRide.put("confirmedByDriver", false);
                        acceptedRide.put("confirmedByRider", false);

                        acceptedRidesRef.push().setValue(acceptedRide)
                                .addOnCompleteListener(acceptTask -> {
                                    if (acceptTask.isSuccessful()) {

                                        notificationsRef.child(rideRequest.getRiderUID())
                                                .push()
                                                .setValue("Your ride request from " + rideRequest.getFrom() + " to " + rideRequest.getTo() + " was accepted!");

                                        Toast.makeText(getActivity(), "Ride request accepted!", Toast.LENGTH_SHORT).show();
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, new HomeFragment())
                                                .commit();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to save accepted ride.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Failed to accept ride request.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
