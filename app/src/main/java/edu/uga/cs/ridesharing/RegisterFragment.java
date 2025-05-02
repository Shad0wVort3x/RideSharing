package edu.uga.cs.ridesharing;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
/**
 * Fragment that handles user registration using Firebase Authentication.
 * On successful registration, it also initializes the user in the Firebase Realtime Database
 * with a starting balance of 100 ride points and redirects to the login screen.
 */
public class RegisterFragment extends Fragment {

    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private TextView goToLoginTextView;

    /**
     * Required empty public constructor.
     */
    public RegisterFragment() {

    }

    /**
     * Inflates the registration layout for the fragment.
     *
     * @param inflater           LayoutInflater used to inflate views.
     * @param container          The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState The saved state of the fragment (if any).
     * @return The root view of the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }
    /**
     * Called after the view has been created. Initializes UI components,
     * sets up listeners for register and login navigation actions.
     *
     * @param view               The fragment's root view.
     * @param savedInstanceState The saved state of the fragment (if any).
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        registerButton = view.findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(v -> {
            registerUser();
        });
        goToLoginTextView = view.findViewById(R.id.goToLoginTextView);

        goToLoginTextView.setOnClickListener(v -> {
            // Navigate back to LoginFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
    /**
     * Handles the registration process:
     * - Validates input fields.
     * - Creates a new user in Firebase Authentication.
     * - Adds user info to the Realtime Database.
     * - Navigates to the LoginFragment on success.
     */
    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Registration Successful! You can now log in.", Toast.LENGTH_SHORT).show();

                        String uid = mAuth.getCurrentUser().getUid();
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", email);
                        userMap.put("points", 100); // initial 100 ride points
                        usersRef.child(uid).setValue(userMap);

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new LoginFragment())
                                .commit();
                    } else {
                        Toast.makeText(getActivity(), "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
