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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uga.cs.ridesharing.adapter.RideOfferAdapter;
import edu.uga.cs.ridesharing.model.RideOffer;
/**
 * Fragment that displays a list of available ride offers.
 * Allows the current user to view and accept ride offers.
 * Accepted offers are moved to the "acceptedRides" node in Firebase.
 */
public class ViewRideOffersFragment extends Fragment implements RideOfferAdapter.OnRideOfferClickListener {

    private RecyclerView recyclerView;
    private RideOfferAdapter rideOfferAdapter;
    private List<RideOffer> rideOfferList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    /**
     * Default constructor.
     */
    public ViewRideOffersFragment() {

    }
    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater LayoutInflater object to inflate views.
     * @param container ViewGroup container.
     * @param savedInstanceState Previously saved state, if any.
     * @return The inflated layout view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_ride_offers, container, false);
    }
    /**
     * Initializes UI components and sets up Firebase data loading and toolbar.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState Previously saved state, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Ride Offers");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        recyclerView = view.findViewById(R.id.rideOffersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rideOfferList = new ArrayList<>();
        rideOfferAdapter = new RideOfferAdapter(rideOfferList, this);
        recyclerView.setAdapter(rideOfferAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("rideOffers");
        mAuth = FirebaseAuth.getInstance();

        loadRideOffers();

    }
    /**
     * Loads ride offers from Firebase, filters out accepted ones,
     * sorts them by date and time, and updates the RecyclerView.
     */
    private void loadRideOffers() {
        databaseReference.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rideOfferList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RideOffer rideOffer = dataSnapshot.getValue(RideOffer.class);
                    rideOffer.setRideID(dataSnapshot.getKey());
                    if (rideOffer.getAcceptedBy() == null) {
                        rideOfferList.add(rideOffer);
                    }
                }

                rideOfferList.sort((o1, o2) -> {
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
                rideOfferAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load ride offers", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Callback method from the RideOfferAdapter when a user clicks to accept a ride.
     *
     * @param rideOffer The ride offer that was clicked.
     */
    @Override
    public void onRideOfferClick(RideOffer rideOffer) {
        acceptRideOffer(rideOffer);
    }
    /**
     * Accepts the selected ride offer:
     * - Marks it as accepted in Firebase.
     * - Saves it to "acceptedRides".
     * - Sends a notification to the driver.
     * - Navigates back to the home screen.
     *
     * @param rideOffer The ride offer being accepted.
     */
    private void acceptRideOffer(RideOffer rideOffer) {
        String riderUID = mAuth.getCurrentUser().getUid();
        DatabaseReference rideOffersRef = databaseReference.child(rideOffer.getRideID());
        DatabaseReference acceptedRidesRef = FirebaseDatabase.getInstance().getReference("acceptedRides");
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

        rideOffersRef.child("acceptedBy").setValue(riderUID)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> acceptedRide = new HashMap<>();
                        acceptedRide.put("driverUID", rideOffer.getDriverUID());
                        acceptedRide.put("riderUID", riderUID);
                        acceptedRide.put("date", rideOffer.getDate());
                        acceptedRide.put("time", rideOffer.getTime());
                        acceptedRide.put("from", rideOffer.getFrom());
                        acceptedRide.put("to", rideOffer.getTo());
                        acceptedRide.put("pointsCost", 50);
                        acceptedRide.put("confirmedByDriver", false);
                        acceptedRide.put("confirmedByRider", false);

                        acceptedRidesRef.push().setValue(acceptedRide)
                                .addOnCompleteListener(acceptTask -> {
                                    if (acceptTask.isSuccessful()) {

                                        notificationsRef.child(rideOffer.getDriverUID())
                                                .push()
                                                .setValue("Your ride offer from " + rideOffer.getFrom() + " to " + rideOffer.getTo() + " was accepted!");

                                        Toast.makeText(getActivity(), "Ride offer accepted!", Toast.LENGTH_SHORT).show();
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, new HomeFragment())
                                                .commit();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to save accepted ride.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Failed to accept ride offer.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
