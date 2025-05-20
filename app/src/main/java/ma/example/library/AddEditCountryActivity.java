package ma.example.library;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditCountryActivity extends AppCompatActivity {

    private static final String TAG = "AddEditCountryActivity";
    private static final int REQUEST_IMAGE_GALLERY = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PERMISSION_CAMERA = 201;

    // Declare views
    private Toolbar toolbar;
    private TextInputEditText countryNameEditText;
    private ImageView countryFlagImageView;
    private ImageView addImageIcon;
    private MaterialButton btnSelectFromGallery;
    private MaterialButton btnTakePhoto;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;

    private boolean isEditMode = false;
    private int countryId = -1;
    private CountryDatabase db;
    private String currentPhotoPath;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_country);

        try {
            // Initialize database
            db = new CountryDatabase(this);

            // Initialize views with the exact IDs from your layout
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Country");
            }

            countryNameEditText = findViewById(R.id.countryNameEditText);
            countryFlagImageView = findViewById(R.id.countryFlagImageView);
            addImageIcon = findViewById(R.id.addImageIcon);
            btnSelectFromGallery = findViewById(R.id.btnSelectFromGallery);
            btnTakePhoto = findViewById(R.id.btnTakePhoto);
            saveButton = findViewById(R.id.saveButton);
            cancelButton = findViewById(R.id.cancelButton);

            // Check if we're in edit mode
            if (getIntent().hasExtra("COUNTRY_ID")) {
                isEditMode = true;
                countryId = getIntent().getIntExtra("COUNTRY_ID", -1);

                if (countryId != -1) {
                    // Load the country from database
                    Country countryToEdit = db.getCountry(countryId);

                    if (countryToEdit != null) {
                        // Update toolbar title
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Edit Country");
                        }

                        // Populate fields with country data
                        countryNameEditText.setText(countryToEdit.getName());

                        // Load flag image if available
                        String flagPath = countryToEdit.getFlagPath();
                        if (flagPath != null && !flagPath.isEmpty()) {
                            try {
                                File imgFile = new File(flagPath);
                                if (imgFile.exists()) {
                                    Uri imageUri = Uri.fromFile(imgFile);
                                    countryFlagImageView.setImageURI(imageUri);
                                    addImageIcon.setVisibility(View.GONE);
                                    currentPhotoPath = flagPath;
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error loading flag image: " + e.getMessage(), e);
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
                    saveCountry();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            Log.d(TAG, "AddEditCountryActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing AddEditCountryActivity: " + e.getMessage(), e);
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Create the directory if it doesn't exist
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "Created image file at: " + currentPhotoPath);
        return image;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                // Handle gallery image selection
                Uri selectedImage = data.getData();
                try {
                    if (selectedImage != null) {
                        // Display the selected image
                        countryFlagImageView.setImageURI(selectedImage);
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
                        countryFlagImageView.setImageBitmap(imageBitmap);
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

            // Compress the bitmap and save it
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Error saving image: " + e.getMessage(), e);
            return null;
        }
    }

    private void saveCountry() {
        try {
            // Validate inputs
            String name = countryNameEditText.getText().toString().trim();

            if (name.isEmpty()) {
                countryNameEditText.setError("Country name cannot be empty");
                return;
            }

            boolean success;

            // Create or update country
            if (isEditMode && countryId != -1) {
                // Update existing country
                Country countryToEdit = db.getCountry(countryId);
                if (countryToEdit != null) {
                    countryToEdit.setName(name);
                    // Update flag path if needed
                    if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                        countryToEdit.setFlagPath(currentPhotoPath);
                    }

                    // Update in database
                    success = db.updateCountry(countryToEdit);

                    if (success) {
                        Toast.makeText(this, "Country updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to update country", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // Create new country
                Country newCountry = new Country();
                newCountry.setName(name);
                // Set flag path if available
                if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                    newCountry.setFlagPath(currentPhotoPath);
                }

                // Save to database
                long id = db.addCountry(newCountry);

                if (id > 0) {
                    Toast.makeText(this, "Country saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save country", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving country: " + e.getMessage(), e);
            Toast.makeText(this, "Error saving country: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
