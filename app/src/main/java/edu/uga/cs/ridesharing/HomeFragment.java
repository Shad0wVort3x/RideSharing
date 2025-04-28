package edu.uga.cs.ridesharing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class HomeFragment extends Fragment {

    private Button postRideOfferButton, postRideRequestButton, viewRideOffersButton, viewRideRequestsButton, viewAcceptedRidesButton, logoutButton, viewRideHistoryButton;
    private TextView pointsTextView;
    private TextView notificationBadge;
    private FirebaseAuth mAuth;
    private DatabaseReference notificationsRef;
    private ValueEventListener notificationListener;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Buttons
        postRideOfferButton = view.findViewById(R.id.postRideOfferButton);
        postRideRequestButton = view.findViewById(R.id.postRideRequestButton);
        viewRideOffersButton = view.findViewById(R.id.viewRideOffersButton);
        viewRideRequestsButton = view.findViewById(R.id.viewRideRequestsButton);
        viewAcceptedRidesButton = view.findViewById(R.id.viewAcceptedRidesButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        viewRideHistoryButton = view.findViewById(R.id.viewRideHistoryButton);

        // Points
        pointsTextView = view.findViewById(R.id.pointsTextView);

        // Notification stuff
        FrameLayout notificationIconLayout = view.findViewById(R.id.notificationIconLayout);
        notificationBadge = view.findViewById(R.id.notificationBadge);

        mAuth = FirebaseAuth.getInstance();

        // Setup buttons
        setupButtonListeners();

        // Load points
        loadUserPoints();

        // Setup notification badge
        notificationsRef = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(mAuth.getCurrentUser().getUid());

        notificationListener = notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int notificationCount = (int) snapshot.getChildrenCount();
                if (notificationCount > 0) {
                    notificationBadge.setVisibility(View.VISIBLE);
                    notificationBadge.setText(String.valueOf(notificationCount));
                } else {
                    notificationBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        notificationIconLayout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ViewNotificationsFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupButtonListeners() {
        postRideOfferButton.setOnClickListener(v -> navigateTo(new PostRideOfferFragment()));
        postRideRequestButton.setOnClickListener(v -> navigateTo(new PostRideRequestFragment()));
        viewRideOffersButton.setOnClickListener(v -> navigateTo(new ViewRideOffersFragment()));
        viewRideRequestsButton.setOnClickListener(v -> navigateTo(new ViewRideRequestsFragment()));
        viewAcceptedRidesButton.setOnClickListener(v -> navigateTo(new ViewAcceptedRidesFragment()));
        viewRideHistoryButton.setOnClickListener(v -> navigateTo(new ViewRideHistoryFragment()));
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
            navigateTo(new LoginFragment());
        });
    }

    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserPoints();
    }

    private void loadUserPoints() {
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.child(uid).child("points").get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Integer points = snapshot.getValue(Integer.class);
                        pointsTextView.setText("Ride Points: " + points);
                    } else {
                        pointsTextView.setText("Ride Points: 0");
                    }
                })
                .addOnFailureListener(e -> pointsTextView.setText("Ride Points: 0"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (notificationsRef != null && notificationListener != null) {
            notificationsRef.removeEventListener(notificationListener);
        }
    }
}
