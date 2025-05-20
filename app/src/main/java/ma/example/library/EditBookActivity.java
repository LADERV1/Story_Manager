package ma.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditBookActivity extends AppCompatActivity {

    private static final String TAG = "EditBookActivity";

    private TextInputEditText titleEditText;
    private TextInputEditText contentEditText;
    private TextInputEditText countryEditText;
    private TextInputEditText authorEditText;
    private TextInputEditText dateEditText;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private Toolbar toolbar;

    private int histoireId;
    private Histoire histoire;
    private HistoireDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        try {
            // Initialize database
            db = new HistoireDatabase(this);

            // Initialize views
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            titleEditText = findViewById(R.id.titleEditText);
            contentEditText = findViewById(R.id.contentEditText);
            countryEditText = findViewById(R.id.countryEditText);
            authorEditText = findViewById(R.id.authorEditText);
            dateEditText = findViewById(R.id.dateEditText);
            saveButton = findViewById(R.id.saveButton);
            cancelButton = findViewById(R.id.cancelButton);

            // Get story data from intent
            Intent intent = getIntent();
            if (intent != null) {
                histoireId = intent.getIntExtra("HISTOIRE_ID", -1);

                if (histoireId != -1) {
                    // Load the histoire from database
                    histoire = db.getHistoire(histoireId);

                    if (histoire != null) {
                        // Set all fields with the histoire data
                        populateFields();
                    }
                }
            }

            // Set up click listeners
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveChanges();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            
            Log.d(TAG, "EditBookActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing EditBookActivity: " + e.getMessage(), e);
        }
    }

    private void populateFields() {
        try {
            if (histoire != null) {
                titleEditText.setText(histoire.getTitre());
                contentEditText.setText(histoire.getContenu());
                countryEditText.setText(histoire.getPays());
                authorEditText.setText(histoire.getAuteur());
                dateEditText.setText(histoire.getDate());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating fields: " + e.getMessage(), e);
        }
    }

    private void saveChanges() {
        try {
            String title = titleEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();
            String country = countryEditText.getText().toString().trim();
            String author = authorEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();

            // Validate inputs
            if (title.isEmpty()) {
                titleEditText.setError("Title cannot be empty");
                return;
            }

            if (content.isEmpty()) {
                contentEditText.setError("Content cannot be empty");
                return;
            }

            if (histoire != null) {
                // Update all fields of the histoire object
                histoire.setTitre(title);
                histoire.setContenu(content);
                histoire.setPays(country);
                histoire.setAuteur(author);
                histoire.setDate(date);

                // Update in database
                boolean success = db.updateHistoire(histoire);

                if (success) {
                    Toast.makeText(this, "Story updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update story", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving changes: " + e.getMessage(), e);
            Toast.makeText(this, "Error saving changes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}