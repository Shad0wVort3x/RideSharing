package edu.uga.cs.ridesharing;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.ridesharing.adapter.ManageRideAdapter;
import edu.uga.cs.ridesharing.model.RideOffer;
import edu.uga.cs.ridesharing.model.RideRequest;

public class ManageMyRidesFragment extends Fragment {

    private RecyclerView offersRecyclerView, requestsRecyclerView;
    private ManageRideAdapter offerAdapter, requestAdapter;
    private List<RideOffer> rideOffers;
    private List<RideRequest> rideRequests;

    private DatabaseReference offersRef, requestsRef;
    private FirebaseAuth mAuth;

    public ManageMyRidesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_my_rides, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup the Toolbar
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        // RecyclerViews setup
        offersRecyclerView = view.findViewById(R.id.myRideOffersRecyclerView);
        requestsRecyclerView = view.findViewById(R.id.myRideRequestsRecyclerView);

        offersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        rideOffers = new ArrayList<>();
        rideRequests = new ArrayList<>();

        offerAdapter = new ManageRideAdapter(rideOffers, null, this);
        requestAdapter = new ManageRideAdapter(null, rideRequests, this);

        offersRecyclerView.setAdapter(offerAdapter);
        requestsRecyclerView.setAdapter(requestAdapter);

        mAuth = FirebaseAuth.getInstance();
        offersRef = FirebaseDatabase.getInstance().getReference("rideOffers");
        requestsRef = FirebaseDatabase.getInstance().getReference("rideRequests");

        loadMyRides();
    }


    private void loadMyRides() {
        String uid = mAuth.getCurrentUser().getUid();

        // Load my unaccepted Ride Offers
        offersRef.orderByChild("driverUID").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rideOffers.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            RideOffer offer = data.getValue(RideOffer.class);
                            if (offer != null && offer.getAcceptedBy() == null) {
                                offer.setRideID(data.getKey());
                                rideOffers.add(offer);
                            }
                        }
                        offerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

        // Load my unaccepted Ride Requests
        requestsRef.orderByChild("riderUID").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rideRequests.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            RideRequest request = data.getValue(RideRequest.class);
                            if (request != null && request.getAcceptedBy() == null) {
                                request.setRequestID(data.getKey());
                                rideRequests.add(request);
                            }
                        }
                        requestAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    public void editRideOffer(RideOffer rideOffer) {
        showEditDialog(rideOffer, null);
    }

    public void editRideRequest(RideRequest rideRequest) {
        showEditDialog(null, rideRequest);
    }

    private void showEditDialog(RideOffer offer, RideRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Ride");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_ride, null);
        EditText editDate = dialogView.findViewById(R.id.editDate);
        EditText editTime = dialogView.findViewById(R.id.editTime);
        EditText editFrom = dialogView.findViewById(R.id.editFrom);
        EditText editTo = dialogView.findViewById(R.id.editTo);

        if (offer != null) {
            editDate.setText(offer.getDate());
            editTime.setText(offer.getTime());
            editFrom.setText(offer.getFrom());
            editTo.setText(offer.getTo());
        } else if (request != null) {
            editDate.setText(request.getDate());
            editTime.setText(request.getTime());
            editFrom.setText(request.getFrom());
            editTo.setText(request.getTo());
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newDate = editDate.getText().toString();
            String newTime = editTime.getText().toString();
            String newFrom = editFrom.getText().toString();
            String newTo = editTo.getText().toString();

            if (offer != null) {
                offersRef.child(offer.getRideID()).child("date").setValue(newDate);
                offersRef.child(offer.getRideID()).child("time").setValue(newTime);
                offersRef.child(offer.getRideID()).child("from").setValue(newFrom);
                offersRef.child(offer.getRideID()).child("to").setValue(newTo);
                Toast.makeText(getContext(), "Ride offer updated", Toast.LENGTH_SHORT).show();
            } else if (request != null) {
                requestsRef.child(request.getRequestID()).child("date").setValue(newDate);
                requestsRef.child(request.getRequestID()).child("time").setValue(newTime);
                requestsRef.child(request.getRequestID()).child("from").setValue(newFrom);
                requestsRef.child(request.getRequestID()).child("to").setValue(newTo);
                Toast.makeText(getContext(), "Ride request updated", Toast.LENGTH_SHORT).show();
            }

            loadMyRides(); // Refresh
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void deleteRideOffer(RideOffer offer) {
        offersRef.child(offer.getRideID()).removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Ride offer deleted", Toast.LENGTH_SHORT).show();
                    loadMyRides();
                });
    }

    public void deleteRideRequest(RideRequest request) {
        requestsRef.child(request.getRequestID()).removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Ride request deleted", Toast.LENGTH_SHORT).show();
                    loadMyRides();
                });
    }
}
