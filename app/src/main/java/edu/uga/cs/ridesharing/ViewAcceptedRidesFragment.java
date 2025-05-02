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
import java.util.List;

import edu.uga.cs.ridesharing.adapter.AcceptedRideAdapter;
import edu.uga.cs.ridesharing.model.AcceptedRide;
/**
 * Fragment that displays a list of accepted rides for the currently logged-in user.
 * The rides are loaded from Firebase Realtime Database and shown in a sorted list by date and time.
 */
public class ViewAcceptedRidesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AcceptedRideAdapter acceptedRideAdapter;
    private List<AcceptedRide> acceptedRideList;
    private DatabaseReference acceptedRidesRef;
    private FirebaseAuth mAuth;
    /**
     * Required empty public constructor.
     */
    public ViewAcceptedRidesFragment() {

    }
    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_accepted_rides, container, false);
    }
    /**
     * Called immediately after onCreateView. Sets up UI components,
     * initializes Firebase references, and loads the accepted rides.
     *
     * @param view               The fragment's root view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
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

        recyclerView = view.findViewById(R.id.acceptedRidesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        acceptedRideList = new ArrayList<>();
        acceptedRideAdapter = new AcceptedRideAdapter(getContext(), acceptedRideList);
        recyclerView.setAdapter(acceptedRideAdapter);

        acceptedRidesRef = FirebaseDatabase.getInstance().getReference("acceptedRides");
        mAuth = FirebaseAuth.getInstance();

        loadAcceptedRides();
    }
    /**
     * Loads all accepted rides from the Firebase Realtime Database.
     * Filters for rides related to the current user and sorts them by date and time (earliest first).
     */
    private void loadAcceptedRides() {
        acceptedRidesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                acceptedRideList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AcceptedRide ride = dataSnapshot.getValue(AcceptedRide.class);
                    if (ride != null) {
                        String currentUID = mAuth.getCurrentUser().getUid();
                        if (currentUID.equals(ride.getDriverUID()) || currentUID.equals(ride.getRiderUID())) {
                            ride.setAcceptedRideID(dataSnapshot.getKey());
                            acceptedRideList.add(ride);
                        }
                    }
                }

                acceptedRideList.sort((r1, r2) -> {
                    try {
                        String dt1 = r1.getDate() + " " + r1.getTime();
                        String dt2 = r2.getDate() + " " + r2.getTime();
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
                        return sdf.parse(dt1).compareTo(sdf.parse(dt2));
                    } catch (Exception e) {
                        return 0;
                    }
                });

                acceptedRideAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load accepted rides", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
