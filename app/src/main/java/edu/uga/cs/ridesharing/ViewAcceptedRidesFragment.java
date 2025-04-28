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

public class ViewAcceptedRidesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AcceptedRideAdapter acceptedRideAdapter;
    private List<AcceptedRide> acceptedRideList;
    private DatabaseReference acceptedRidesRef;
    private FirebaseAuth mAuth;

    public ViewAcceptedRidesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_accepted_rides, container, false);
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

        recyclerView = view.findViewById(R.id.acceptedRidesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        acceptedRideList = new ArrayList<>();
        acceptedRideAdapter = new AcceptedRideAdapter(getContext(), acceptedRideList);
        recyclerView.setAdapter(acceptedRideAdapter);

        acceptedRidesRef = FirebaseDatabase.getInstance().getReference("acceptedRides");
        mAuth = FirebaseAuth.getInstance();

        loadAcceptedRides();
    }

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
                acceptedRideAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load accepted rides", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
