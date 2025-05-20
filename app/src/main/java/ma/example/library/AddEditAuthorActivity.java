package ma.example.library;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditAuthorActivity extends AppCompatActivity {

    private static final String TAG = "AddEditAuthorActivity";
    private static final int REQUEST_IMAGE_GALLERY = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PERMISSION_CAMERA = 201;

    private Toolbar toolbar;
    private TextInputLayout authorNameLayout;
    private TextInputEditText authorNameEditText;
    private TextInputLayout authorDobLayout;
    private TextInputEditText authorDobEditText;
    private TextInputLayout authorBioLayout;
    private TextInputEditText authorBioEditText;
    private ImageView authorImageView;
    private ImageView addImageIcon;
    private MaterialButton btnSelectFromGallery;
    private MaterialButton btnTakePhoto;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;

    private boolean isEditMode = false;
    private int authorId = -1;
    private Author authorToEdit;
    private AuthorDatabase db;
    private String currentPhotoPath;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_author);

        try {
            // Initialize database
            db = new AuthorDatabase(this);

            // Initialize views with the exact IDs from your layout
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Add Author");
            }

            authorNameLayout = findViewById(R.id.authorNameLayout);
            authorNameEditText = findViewById(R.id.authorNameEditText);
            authorDobLayout = findViewById(R.id.authorDobLayout);
            authorDobEditText = findViewById(R.id.authorDobEditText);
            authorBioLayout = findViewById(R.id.authorBioLayout);
            authorBioEditText = findViewById(R.id.authorBioEditText);
            authorImageView = findViewById(R.id.authorImageView);
            addImageIcon = findViewById(R.id.addImageIcon);
            btnSelectFromGallery = findViewById(R.id.btnSelectFromGallery);
            btnTakePhoto = findViewById(R.id.btnTakePhoto);
            saveButton = findViewById(R.id.saveButton);
            cancelButton = findViewById(R.id.cancelButton);

            // Check if we're in edit mode
            if (getIntent().hasExtra("AUTHOR_ID")) {
                isEditMode = true;
                authorId = getIntent().getIntExtra("AUTHOR_ID", -1);

                if (authorId != -1) {
                    // Load the author from database
                    authorToEdit = db.getAuthor(authorId);

                    if (authorToEdit != null) {
                        // Update toolbar title
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Edit Author");
                        }

                        // Populate fields with author data
                        populateFields();
                    }
                }
            }

            // Set up date picker for DOB field
            authorDobEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog();
                }
            });

            // Set up image selection buttons
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

            // Set up save button
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAuthor();
                }
            });

            // Set up cancel button
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            Log.d(TAG, "AddEditAuthorActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing AddEditAuthorActivity: " + e.getMessage(), e);
        }
    }

    private void populateFields() {
        try {
            if (authorToEdit != null) {
                authorNameEditText.setText(authorToEdit.getName());
                authorDobEditText.setText(authorToEdit.getDob());
                authorBioEditText.setText(authorToEdit.getBio());

                // Load image if available
                if (authorToEdit.getImagePath() != null && !authorToEdit.getImagePath().isEmpty()) {
                    File imgFile = new File(authorToEdit.getImagePath());
                    if (imgFile.exists()) {
                        Uri imageUri = Uri.fromFile(imgFile);
                        authorImageView.setImageURI(imageUri);
                        addImageIcon.setVisibility(View.GONE);
                        currentPhotoPath = authorToEdit.getImagePath();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating fields: " + e.getMessage(), e);
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateField();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateField() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        authorDobEditText.setText(sdf.format(calendar.getTime()));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                // Handle gallery image selection
                Uri selectedImage = data.getData();
                try {
                    if (selectedImage != null) {
                        // Display the selected image
                        authorImageView.setImageURI(selectedImage);
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
                        authorImageView.setImageBitmap(imageBitmap);
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

    private void saveAuthor() {
        try {
            // Validate inputs
            String name = authorNameEditText.getText().toString().trim();
            String dob = authorDobEditText.getText().toString().trim();
            String bio = authorBioEditText.getText().toString().trim();

            if (name.isEmpty()) {
                authorNameLayout.setError("Author name cannot be empty");
                return;
            } else {
                authorNameLayout.setError(null);
            }

            boolean success;

            // Create or update author
            if (isEditMode && authorToEdit != null) {
                // Update existing author
                authorToEdit.setName(name);
                authorToEdit.setDob(dob);
                authorToEdit.setBio(bio);
                if (currentPhotoPath != null) {
                    authorToEdit.setImagePath(currentPhotoPath);
                }

                // Update in database
                success = db.updateAuthor(authorToEdit);

                if (success) {
                    Toast.makeText(this, "Author updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update author", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Create new author
                Author newAuthor = new Author();
                newAuthor.setName(name);
                newAuthor.setDob(dob);
                newAuthor.setBio(bio);
                if (currentPhotoPath != null) {
                    newAuthor.setImagePath(currentPhotoPath);
                }

                // Save to database
                long id = db.addAuthor(newAuthor);

                if (id > 0) {
                    Toast.makeText(this, "Author saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save author", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving author: " + e.getMessage(), e);
            Toast.makeText(this, "Error saving author: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        return true;
    }
}
