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
/**
 * HomeFragment serves as main dashboard for user after login.
 * Displays ride-related options such as posting/viewing ride offers and requests,
 * viewing accepted rides, ride history, managing user rides, logging out, and showing notifications.
 */
public class HomeFragment extends Fragment {

    private Button postRideOfferButton, postRideRequestButton, viewRideOffersButton, viewRideRequestsButton, viewAcceptedRidesButton, logoutButton, viewRideHistoryButton, manageMyRidesButton;
    private TextView pointsTextView;
    private TextView notificationBadge;
    private FirebaseAuth mAuth;
    private DatabaseReference notificationsRef;
    private ValueEventListener notificationListener;
    /**
     * Required empty public constructor.
     */
    public HomeFragment() {}
    /**
     * Inflates layout for HomeFragment.
     * @param inflater LayoutInflater
     * @param container ViewGroup container
     * @param savedInstanceState Previous state if any
     * @return Inflated View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    /**
     * Initializes UI components, sets button click listeners,
     * fetches user points, and attaches a listener to notifications.
     * @param view The fragment's root view
     * @param savedInstanceState Saved state if any
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postRideOfferButton = view.findViewById(R.id.postRideOfferButton);
        postRideRequestButton = view.findViewById(R.id.postRideRequestButton);
        viewRideOffersButton = view.findViewById(R.id.viewRideOffersButton);
        viewRideRequestsButton = view.findViewById(R.id.viewRideRequestsButton);
        viewAcceptedRidesButton = view.findViewById(R.id.viewAcceptedRidesButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        viewRideHistoryButton = view.findViewById(R.id.viewRideHistoryButton);
        manageMyRidesButton = view.findViewById(R.id.manageMyRidesButton);
        pointsTextView = view.findViewById(R.id.pointsTextView);

        FrameLayout notificationIconLayout = view.findViewById(R.id.notificationIconLayout);
        notificationBadge = view.findViewById(R.id.notificationBadge);

        mAuth = FirebaseAuth.getInstance();


        setupButtonListeners();


        loadUserPoints();

        // setup notification badge
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
    /**
     * Sets click listeners for each button on the home screen.
     */
    private void setupButtonListeners() {
        postRideOfferButton.setOnClickListener(v -> navigateTo(new PostRideOfferFragment()));
        postRideRequestButton.setOnClickListener(v -> navigateTo(new PostRideRequestFragment()));
        viewRideOffersButton.setOnClickListener(v -> navigateTo(new ViewRideOffersFragment()));
        viewRideRequestsButton.setOnClickListener(v -> navigateTo(new ViewRideRequestsFragment()));
        viewAcceptedRidesButton.setOnClickListener(v -> navigateTo(new ViewAcceptedRidesFragment()));
        viewRideHistoryButton.setOnClickListener(v -> navigateTo(new ViewRideHistoryFragment()));
        manageMyRidesButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ManageMyRidesFragment())
                    .addToBackStack(null)
                    .commit();
        });
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
            navigateTo(new LoginFragment());
        });
    }
    /**
     * Navigates to provided fragment and adds the transaction to the back stack.
     * @param fragment Fragment to navigate to
     */
    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    /**
     * Called when fragment becomes visible again.
     * Reloads user points from Firebase.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadUserPoints();
    }

    /**
     * Loads and displays current user's ride points from Firebase.
     */
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
    /**
     * Detaches the notification listener to avoid memory leaks when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (notificationsRef != null && notificationListener != null) {
            notificationsRef.removeEventListener(notificationListener);
        }
    }
}
