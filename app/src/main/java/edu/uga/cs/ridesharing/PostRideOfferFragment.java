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

public class PostRideOfferFragment extends Fragment {

    private EditText dateEditText, timeEditText, fromEditText, toEditText;
    private Button postRideOfferButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public PostRideOfferFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_ride_offer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateEditText = view.findViewById(R.id.dateEditText);
        timeEditText = view.findViewById(R.id.timeEditText);
        fromEditText = view.findViewById(R.id.fromEditText);
        toEditText = view.findViewById(R.id.toEditText);
        postRideOfferButton = view.findViewById(R.id.postRideOfferButton);
        dateEditText.setInputType(InputType.TYPE_NULL);
        dateEditText.setFocusable(false);
        timeEditText.setInputType(InputType.TYPE_NULL);
        timeEditText.setFocusable(false);

        dateEditText.setOnClickListener(v -> showDatePicker());
        timeEditText.setOnClickListener(v -> showTimePicker());
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        postRideOfferButton.setOnClickListener(v -> {
            postRideOffer();
        });
    }
    private void showDatePicker() {
        // Get today's date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Month is 0-based so add 1
                    String formattedDate = String.format("%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                    dateEditText.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
    private void showTimePicker() {
        // Get current time
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, selectedHour, selectedMinute) -> {
                    // Optional: Format to am/pm
                    String amPm = selectedHour >= 12 ? "PM" : "AM";
                    int hourIn12Format = selectedHour % 12;
                    if (hourIn12Format == 0) hourIn12Format = 12;

                    String formattedTime = String.format("%02d:%02d %s", hourIn12Format, selectedMinute, amPm);
                    timeEditText.setText(formattedTime);
                }, hour, minute, false); // false for 12-hour clock

        timePickerDialog.show();
    }


    private void postRideOffer() {
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String from = fromEditText.getText().toString().trim();
        String to = toEditText.getText().toString().trim();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(from) || TextUtils.isEmpty(to)) {
            Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new ride offer map
        Map<String, Object> rideOffer = new HashMap<>();
        rideOffer.put("driverUID", mAuth.getCurrentUser().getUid());
        rideOffer.put("date", date);
        rideOffer.put("time", time);
        rideOffer.put("from", from);
        rideOffer.put("to", to);
        rideOffer.put("acceptedBy", null);

        // Push it to "rideOffers" in Firebase
        databaseReference.child("rideOffers").push().setValue(rideOffer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Ride Offer Posted Successfully!", Toast.LENGTH_SHORT).show();
                        // Optional: Go back to HomeFragment after posting
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment())
                                .commit();
                    } else {
                        Toast.makeText(getActivity(), "Failed to post ride offer: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
