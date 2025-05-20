package ma.example.library;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditCategoryActivity extends AppCompatActivity {

    private static final String TAG = "AddEditCategoryActivity";
    private static final int REQUEST_IMAGE_GALLERY = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PERMISSION_CAMERA = 201;

    private TextInputLayout categoryNameLayout;
    private TextInputEditText categoryNameEditText;
    private ImageView categoryImageView;
    private ImageView addImageIcon;
    private MaterialButton btnSelectFromGallery;
    private MaterialButton btnTakePhoto;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private Toolbar toolbar;

    private Category categoryToEdit;
    private boolean isEditMode = false;
    private CategoryDatabase db;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        try {
            // Initialize database
            db = new CategoryDatabase(this);

            // Initialize views
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Category");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            categoryNameLayout = findViewById(R.id.categoryNameLayout);
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
                int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);

                if (categoryId != -1) {
                    // Load the category from database
                    categoryToEdit = db.getCategory(categoryId);

                    if (categoryToEdit != null) {
                        // Update toolbar title
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Edit Category");
                        }
                        
                        // Populate fields with category data
                        populateFields();
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

            Log.d(TAG, "AddEditCategoryActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing AddEditCategoryActivity: " + e.getMessage(), e);
        }
    }

    private void populateFields() {
        try {
            if (categoryToEdit != null) {
                categoryNameEditText.setText(categoryToEdit.getName());
                
                // Load image if available
                if (categoryToEdit.getImagePath() != null && !categoryToEdit.getImagePath().isEmpty()) {
                    File imgFile = new File(categoryToEdit.getImagePath());
                    if (imgFile.exists()) {
                        Uri imageUri = Uri.fromFile(imgFile);
                        categoryImageView.setImageURI(imageUri);
                        addImageIcon.setVisibility(View.GONE);
                        currentPhotoPath = categoryToEdit.getImagePath();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating fields: " + e.getMessage(), e);
        }
    }

    private void selectImageFromGallery() {
        try {
            // Use ACTION_GET_CONTENT which doesn't require storage permissions on newer Android versions
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            
            // Create a chooser to let the user select an app
            Intent chooserIntent = Intent.createChooser(intent, "Select Image");
            startActivityForResult(chooserIntent, REQUEST_IMAGE_GALLERY);
        } catch (Exception e) {
            Log.e(TAG, "Error opening gallery: " + e.getMessage(), e);
            Toast.makeText(this, "Error opening gallery: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            
            // Request camera permission
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.CAMERA}, 
                    REQUEST_PERMISSION_CAMERA);
            return;
        }
        
        // Camera permission granted, take photo
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        // For simplicity, we'll use the basic camera intent that returns a thumbnail
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            // Check if permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, take photo
                dispatchTakePictureIntent();
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                // Handle gallery image selection
                Uri selectedImage = data.getData();
                try {
                    if (selectedImage != null) {
                        // Display the selected image
                        categoryImageView.setImageURI(null); // Clear the image first
                        categoryImageView.setImageURI(selectedImage);
                        addImageIcon.setVisibility(View.GONE);
                        
                        // Save the image to app's private storage
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        currentPhotoPath = saveImageToInternalStorage(bitmap);
                        Log.d(TAG, "Selected image saved to: " + currentPhotoPath);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling gallery result: " + e.getMessage(), e);
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                // Handle camera capture - get the thumbnail
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        // Display the thumbnail
                        categoryImageView.setImageBitmap(null); // Clear the image first
                        categoryImageView.setImageBitmap(imageBitmap);
                        addImageIcon.setVisibility(View.GONE);
                        
                        // Save the thumbnail to app's private storage
                        currentPhotoPath = saveImageToInternalStorage(imageBitmap);
                        Log.d(TAG, "Captured image saved to: " + currentPhotoPath);
                    }
                }
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            // Create a file in app's private storage
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "IMG_" + timeStamp + ".jpg";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            
            // Create the directory if it doesn't exist
            if (storageDir != null && !storageDir.exists()) {
                storageDir.mkdirs();
            }
            
            File imageFile = new File(storageDir, imageFileName);
            FileOutputStream fos = new FileOutputStream(imageFile);
            
            // Compress the bitmap and save it with higher quality
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            fos.close();
            
            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Error saving image: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Ensures the image is properly saved and displayed
     */
    private void updateImageView() {
        try {
            if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                File imgFile = new File(currentPhotoPath);
                if (imgFile.exists()) {
                    // Clear the image first to prevent caching issues
                    categoryImageView.setImageURI(null);
                    Uri imageUri = Uri.fromFile(imgFile);
                    categoryImageView.setImageURI(imageUri);
                    addImageIcon.setVisibility(View.GONE);
                    Log.d(TAG, "Successfully updated image view with: " + currentPhotoPath);
                } else {
                    Log.d(TAG, "Image file doesn't exist: " + currentPhotoPath);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating image view: " + e.getMessage(), e);
        }
    }

    private void saveCategory() {
        try {
            // Validate inputs
            String name = categoryNameEditText.getText().toString().trim();

            if (name.isEmpty()) {
                categoryNameLayout.setError("Category name cannot be empty");
                return;
            } else {
                categoryNameLayout.setError(null);
            }

            boolean success;

            // Create or update category
            if (isEditMode && categoryToEdit != null) {
                // Update existing category
                categoryToEdit.setName(name);
                if (currentPhotoPath != null) {
                    categoryToEdit.setImagePath(currentPhotoPath);
                }

                // Update in database
                success = db.updateCategory(categoryToEdit);

                if (success) {
                    Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Create new category
                Category newCategory = new Category();
                newCategory.setName(name);
                if (currentPhotoPath != null) {
                    newCategory.setImagePath(currentPhotoPath);
                }

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
