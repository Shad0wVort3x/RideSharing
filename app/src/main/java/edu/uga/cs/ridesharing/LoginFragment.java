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
/**
 * LoginFragment handles the user login interface and logic.
 * It allows users to input their email and password and sign into the application.
 * It also provides navigation to the registration screen.
 */
public class LoginFragment extends Fragment {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private TextView goToRegisterTextView;
    /**
     * Required empty public constructor.
     */
    public LoginFragment() {

    }
    /**
     * Inflates the layout for this fragment.
     * @param inflater The LayoutInflater object
     * @param container The container view the fragment UI should be attached to
     * @param savedInstanceState Saved state if available
     * @return The root view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    /**
     * Initializes UI components, Firebase auth instance, and sets up button listeners.
     * @param view The fragment's root view
     * @param savedInstanceState Saved state if available
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            loginUser();
        });
        goToRegisterTextView = view.findViewById(R.id.goToRegisterTextView);

        goToRegisterTextView.setOnClickListener(v -> {

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
    /**
     * Attempts to log the user in using Firebase Authentication with the provided credentials.
     * Shows error messages if fields are empty or if authentication fails.
     * Navigates to HomeFragment upon successful login.
     */
    private void loginUser() {
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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment())
                                .commit();
                    } else {
                        Toast.makeText(getActivity(), "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
