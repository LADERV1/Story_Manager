package ma.example.library;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditActivity extends AppCompatActivity {

    private static final String TAG = "AddEditActivity";

    private Toolbar toolbar;
    private TextInputEditText categoryNameEditText;
    private ImageView categoryImageView;
    private ImageView addImageIcon;
    private MaterialButton btnSelectFromGallery;
    private MaterialButton btnTakePhoto;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;

    private boolean isEditMode = false;
    private int categoryId = -1;
    private CategoryDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FIXED: Use the correct layout file
        setContentView(R.layout.activity_add_edit_category);

        // Initialize database
        db = new CategoryDatabase(this);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        categoryNameEditText = findViewById(R.id.categoryNameEditText);
        categoryImageView = findViewById(R.id.categoryImageView);
        addImageIcon = findViewById(R.id.addImageIcon);
        btnSelectFromGallery = findViewById(R.id.btnSelectFromGallery);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Check if we're in edit mode
        if (getIntent().hasExtra("CATEGORY_ID")) {
            isEditMode = true;
            categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);

            if (categoryId != -1) {
                // Load the category from database
                Category categoryToEdit = db.getCategory(categoryId);

                if (categoryToEdit != null) {
                    // Update toolbar title
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("Edit Category");
                    }
                    
                    // Populate fields with category data
                    categoryNameEditText.setText(categoryToEdit.getName());
                    
                    // Load image if available
                    String imagePath = categoryToEdit.getImagePath();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            java.io.File imgFile = new java.io.File(imagePath);
                            if (imgFile.exists()) {
                                android.net.Uri imageUri = android.net.Uri.fromFile(imgFile);
                                categoryImageView.setImageURI(imageUri);
                                addImageIcon.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error loading image: " + e.getMessage(), e);
                        }
                    }
                }
            }
        }

        // Set up button click listeners
        btnSelectFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void selectImageFromGallery() {
        // Implement gallery selection logic
        Toast.makeText(this, "Select from gallery functionality will be implemented", Toast.LENGTH_SHORT).show();
    }

    private void takePhoto() {
        // Implement photo capture logic
        Toast.makeText(this, "Take photo functionality will be implemented", Toast.LENGTH_SHORT).show();
    }

    private void saveCategory() {
        try {
            // Validate inputs
            String name = categoryNameEditText.getText().toString().trim();

            if (name.isEmpty()) {
                categoryNameEditText.setError("Category name cannot be empty");
                return;
            }

            boolean success;

            // Create or update category
            if (isEditMode && categoryId != -1) {
                // Update existing category
                Category categoryToEdit = db.getCategory(categoryId);
                if (categoryToEdit != null) {
                    categoryToEdit.setName(name);
                    // Update image path if needed
                    
                    // Update in database
                    success = db.updateCategory(categoryToEdit);

                    if (success) {
                        Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // Create new category
                Category newCategory = new Category();
                newCategory.setName(name);
                // Set image path if available
                
                // Save to database
                long id = db.addCategory(newCategory);

                if (id > 0) {
                    Toast.makeText(this, "Category saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save category", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving category: " + e.getMessage(), e);
            Toast.makeText(this, "Error saving category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}