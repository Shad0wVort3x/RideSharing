package edu.uga.cs.ridesharing;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
/**
 * Fragment that allows users to post a ride request by selecting a date, time,
 * origin, and destination. The request is then submitted to Firebase Realtime Database.
 */
public class PostRideRequestFragment extends Fragment {

    private EditText dateEditText, timeEditText, fromEditText, toEditText;
    private Button postRideRequestButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    /**
     * Required empty public constructor.
     */
    public PostRideRequestFragment() {

    }
    /**
     * Inflates the layout for this fragment.
     * @param inflater The LayoutInflater object that can be used to inflate views.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed.
     * @return The view for this fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_ride_request, container, false);
    }

    /**
     * Called after the view has been created. Initializes input fields, sets up
     * listeners for date and time pickers, and defines the post request logic.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        dateEditText = view.findViewById(R.id.dateEditText);
        timeEditText = view.findViewById(R.id.timeEditText);
        fromEditText = view.findViewById(R.id.fromEditText);
        toEditText = view.findViewById(R.id.toEditText);
        postRideRequestButton = view.findViewById(R.id.postRideRequestButton);
        dateEditText.setInputType(InputType.TYPE_NULL);
        dateEditText.setFocusable(false);

        timeEditText.setInputType(InputType.TYPE_NULL);
        timeEditText.setFocusable(false);
        dateEditText.setOnClickListener(v -> showDatePicker());
        timeEditText.setOnClickListener(v -> showTimePicker());
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        postRideRequestButton.setOnClickListener(v -> {
            postRideRequest();
        });
    }

    /**
     * Displays a DatePickerDialog for the user to select a date.
     * Updates the dateEditText with the selected value.
     */
    private void showDatePicker() {

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String formattedDate = String.format("%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                    dateEditText.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
    /**
     * Displays a TimePickerDialog for the user to select a time.
     * Updates the timeEditText with the selected value in 12-hour format.
     */
    private void showTimePicker() {

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, selectedHour, selectedMinute) -> {

                    String amPm = selectedHour >= 12 ? "PM" : "AM";
                    int hourIn12Format = selectedHour % 12;
                    if (hourIn12Format == 0) hourIn12Format = 12;

                    String formattedTime = String.format("%02d:%02d %s", hourIn12Format, selectedMinute, amPm);
                    timeEditText.setText(formattedTime);
                }, hour, minute, false);

        timePickerDialog.show();
    }

    /**
     * Validates input fields and submits the ride request to Firebase.
     * Shows appropriate Toast messages for success or failure.
     */
    private void postRideRequest() {
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String from = fromEditText.getText().toString().trim();
        String to = toEditText.getText().toString().trim();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(from) || TextUtils.isEmpty(to)) {
            Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> rideRequest = new HashMap<>();
        rideRequest.put("riderUID", mAuth.getCurrentUser().getUid());
        rideRequest.put("date", date);
        rideRequest.put("time", time);
        rideRequest.put("from", from);
        rideRequest.put("to", to);
        rideRequest.put("acceptedBy", null);

        databaseReference.child("rideRequests").push().setValue(rideRequest)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Ride Request Posted Successfully!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment())
                                .commit();
                    } else {
                        Toast.makeText(getActivity(), "Failed to post ride request: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
