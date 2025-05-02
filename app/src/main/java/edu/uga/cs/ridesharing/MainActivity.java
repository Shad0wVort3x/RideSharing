package edu.uga.cs.ridesharing;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
/**
 * MainActivity is entry point of the RideSharing application.
 * It sets up initial layout and launches the WelcomeFragment
 * if this is the first time the activity is being created.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     * Sets up edge-to-edge display, applies system window insets, and
     * launches the WelcomeFragment into the fragment container.
     *
     * @param savedInstanceState If the activity is being re-initialized
     *                           after previously being shut down, this contains
     *                           the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WelcomeFragment())
                    .commit();
        }
    }
}