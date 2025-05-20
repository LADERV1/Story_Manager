package ma.example.library;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

import java.io.File;

public class StoryViewActivity extends AppCompatActivity {

    private static final String TAG = "StoryViewActivity";

    private Toolbar toolbar;
    private ImageView storyImageView;
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView authorTextView;
    private TextView countryTextView;
    private TextView categoryTextView;
    private TextView dateTextView;
    private MaterialButton backButton;

    private HistoireDatabase db;
    private int histoireId;
    private Histoire histoire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);

        try {
            // Initialize database
            db = new HistoireDatabase(this);

            // Initialize views with the exact IDs from your layout
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            storyImageView = findViewById(R.id.storyImageView);
            titleTextView = findViewById(R.id.titleTextView);
            contentTextView = findViewById(R.id.contentTextView);
            authorTextView = findViewById(R.id.authorTextView);
            countryTextView = findViewById(R.id.countryTextView);
            categoryTextView = findViewById(R.id.categoryTextView);
            dateTextView = findViewById(R.id.dateTextView);
            backButton = findViewById(R.id.backButton);

            // Get story ID from intent
            histoireId = getIntent().getIntExtra("HISTOIRE_ID", -1);
            Log.d(TAG, "Received histoire ID: " + histoireId);

            if (histoireId != -1) {
                // Load the story from database
                histoire = db.getHistoire(histoireId);

                if (histoire != null) {
                    // Display the story details
                    displayStoryDetails();
                } else {
                    Log.e(TAG, "Failed to load histoire with ID: " + histoireId);
                    Toast.makeText(this, "Failed to load story", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Log.e(TAG, "No histoire ID provided");
                Toast.makeText(this, "No story ID provided", Toast.LENGTH_SHORT).show();
                finish();
            }

            // Set up back button click listener
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Go back to previous screen
                }
            });
            
            Log.d(TAG, "StoryViewActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading story view", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayStoryDetails() {
        try {
            // Set the title in the toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(histoire.getTitre());
            }

            // Set the text in the TextViews
            titleTextView.setText(histoire.getTitre());
            contentTextView.setText(histoire.getContenu());
            authorTextView.setText(histoire.getAuteur());
            countryTextView.setText(histoire.getPays());
            dateTextView.setText(histoire.getDate());
            
            // Display category if available
            String category = histoire.getCategorie();
            if (category != null && !category.isEmpty()) {
                categoryTextView.setText(category);
            } else {
                // If no category, set a default message
                categoryTextView.setText("Not categorized");
            }

            // Display the story image if available
            String imagePath = histoire.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        Uri imageUri = Uri.fromFile(imgFile);
                        storyImageView.setImageURI(null); // Clear the image first
                        storyImageView.setImageURI(imageUri);
                        Log.d(TAG, "Successfully loaded image from: " + imagePath);
                    } else {
                        Log.d(TAG, "Image file doesn't exist: " + imagePath);
                        storyImageView.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading image: " + e.getMessage(), e);
                    storyImageView.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                Log.d(TAG, "No image path for story");
                storyImageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            Log.d(TAG, "Story details displayed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error displaying story details: " + e.getMessage(), e);
            Toast.makeText(this, "Error displaying story details", Toast.LENGTH_SHORT).show();
        }
    }
}
