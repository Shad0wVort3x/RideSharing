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

public class ViewRideRequestsFragment extends Fragment implements RideRequestAdapter.OnRideRequestClickListener {

    private RecyclerView recyclerView;
    private RideRequestAdapter rideRequestAdapter;
    private List<RideRequest> rideRequestList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public ViewRideRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_ride_requests, container, false);
    }

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
                // Sort manually by date and time
                Collections.sort(rideRequestList, (o1, o2) -> {
                    int dateCompare = o1.getDate().compareTo(o2.getDate());
                    if (dateCompare == 0) {
                        return o1.getTime().compareTo(o2.getTime());
                    } else {
                        return dateCompare;
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
                                        // Send notification to rider AFTER ride accepted is saved
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
