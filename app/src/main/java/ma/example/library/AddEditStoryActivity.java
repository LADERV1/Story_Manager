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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditStoryActivity extends AppCompatActivity {

    private static final String TAG = "AddEditStoryActivity";
    private static final int REQUEST_IMAGE_GALLERY = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PERMISSION_CAMERA = 201;

    private Toolbar toolbar;
    private ImageView storyImageView;
    private ImageView addImageIcon;
    private MaterialButton btnSelectFromGallery;
    private MaterialButton btnTakePhoto;
    private TextInputEditText titleEditText;
    private TextInputEditText contentEditText;
    private AutoCompleteTextView authorDropdown;
    private AutoCompleteTextView countryDropdown;
    private AutoCompleteTextView categoryDropdown; // Added category dropdown
    private TextInputEditText authorEditText;
    private TextInputEditText countryEditText;
    private TextInputEditText categoryEditText; // Added category text field
    private TextInputEditText dateEditText;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;

    private Histoire histoireToEdit;
    private boolean isEditMode = false;
    private HistoireDatabase db;
    private AuthorDatabase authorDb;
    private CountryDatabase countryDb;
    private CategoryDatabase categoryDb; // Added category database
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FIXED: Using the correct layout file
        setContentView(R.layout.activity_edit_book);

        // Initialize databases
        db = new HistoireDatabase(this);
        authorDb = new AuthorDatabase(this);
        countryDb = new CountryDatabase(this);
        categoryDb = new CategoryDatabase(this); // Initialize category database

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Story");
        }

        storyImageView = findViewById(R.id.storyImageView);
        addImageIcon = findViewById(R.id.addImageIcon);
        btnSelectFromGallery = findViewById(R.id.btnSelectFromGallery);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        authorDropdown = findViewById(R.id.authorDropdown);
        countryDropdown = findViewById(R.id.countryDropdown);
        categoryDropdown = findViewById(R.id.categoryDropdown); // Find category dropdown
        authorEditText = findViewById(R.id.authorEditText);
        countryEditText = findViewById(R.id.countryEditText);
        categoryEditText = findViewById(R.id.categoryEditText); // Find category text field
        dateEditText = findViewById(R.id.dateEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Set up author dropdown
        setupAuthorDropdown();

        // Set up country dropdown
        setupCountryDropdown();

        // Set up category dropdown
        setupCategoryDropdown();

        // Check if we're in edit mode
        if (getIntent().hasExtra("HISTOIRE_ID")) {
            isEditMode = true;
            int histoireId = getIntent().getIntExtra("HISTOIRE_ID", -1);

            if (histoireId != -1) {
                // Load the histoire from database
                histoireToEdit = db.getHistoire(histoireId);

                if (histoireToEdit != null) {
                    // Update toolbar title
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("Edit Story");
                    }
                    
                    // Populate fields with story data
                    populateFields();
                }
            }
        }

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

        // Set up save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStory();
            }
        });

        // Set up cancel button click listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupAuthorDropdown() {
        try {
            List<Author> authors = authorDb.getAllAuthors();
            List<String> authorNames = new ArrayList<>();
            
            for (Author author : authors) {
                authorNames.add(author.getName());
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                authorNames
            );
            
            authorDropdown.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up author dropdown: " + e.getMessage(), e);
        }
    }

    private void setupCountryDropdown() {
        try {
            List<Country> countries = countryDb.getAllCountries();
            List<String> countryNames = new ArrayList<>();
            
            for (Country country : countries) {
                countryNames.add(country.getName());
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                countryNames
            );
            
            countryDropdown.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up country dropdown: " + e.getMessage(), e);
        }
    }

    private void setupCategoryDropdown() {
        try {
            // Get all categories from the database
            List<Category> categories = categoryDb.getAllCategories();
            List<String> categoryNames = new ArrayList<>();
            
            // Extract category names
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }
            
            // Create and set the adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
            );
            
            // Set the adapter to the dropdown
            categoryDropdown.setAdapter(adapter);
            
            Log.d(TAG, "Category dropdown setup with " + categoryNames.size() + " items");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up category dropdown: " + e.getMessage(), e);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                // Handle gallery image selection
                Uri selectedImage = data.getData();
                try {
                    if (selectedImage != null) {
                        // Display the selected image
                        storyImageView.setImageURI(selectedImage);
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
                        storyImageView.setImageBitmap(imageBitmap);
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

    private void populateFields() {
        try {
            if (histoireToEdit != null) {
                titleEditText.setText(histoireToEdit.getTitre());
                contentEditText.setText(histoireToEdit.getContenu());
                
                // Set author in dropdown or text field
                String author = histoireToEdit.getAuteur();
                if (author != null && !author.isEmpty()) {
                    authorDropdown.setText(author, false);
                    authorEditText.setText(author);
                }
                
                // Set country in dropdown or text field
                String country = histoireToEdit.getPays();
                if (country != null && !country.isEmpty()) {
                    countryDropdown.setText(country, false);
                    countryEditText.setText(country);
                }
                
                // Set category in dropdown or text field
                String category = histoireToEdit.getCategorie();
                if (category != null && !category.isEmpty()) {
                    categoryDropdown.setText(category, false);
                    categoryEditText.setText(category);
                }
                
                dateEditText.setText(histoireToEdit.getDate());
                
                // Load image if available
                String imagePath = histoireToEdit.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            Uri imageUri = Uri.fromFile(imgFile);
                            storyImageView.setImageURI(imageUri);
                            addImageIcon.setVisibility(View.GONE);
                            currentPhotoPath = imagePath;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading image: " + e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating fields: " + e.getMessage(), e);
        }
    }

    private void saveStory() {
        try {
            // Validate inputs
            String titre = titleEditText.getText().toString().trim();
            String contenu = contentEditText.getText().toString().trim();
            
            // Get author from dropdown or text field
            String auteur = authorDropdown.getText().toString().trim();
            if (auteur.isEmpty()) {
                auteur = authorEditText.getText().toString().trim();
            }
            
            // Get country from dropdown or text field
            String pays = countryDropdown.getText().toString().trim();
            if (pays.isEmpty()) {
                pays = countryEditText.getText().toString().trim();
            }
            
            // Get category from dropdown or text field
            String categorie = categoryDropdown.getText().toString().trim();
            if (categorie.isEmpty()) {
                categorie = categoryEditText.getText().toString().trim();
            }
            
            String date = dateEditText.getText().toString().trim();

            if (titre.isEmpty()) {
                titleEditText.setError("Title cannot be empty");
                return;
            }

            if (contenu.isEmpty()) {
                contentEditText.setError("Content cannot be empty");
                return;
            }

            boolean success;

            // Create or update story
            if (isEditMode && histoireToEdit != null) {
                // Update existing story
                histoireToEdit.setTitre(titre);
                histoireToEdit.setContenu(contenu);
                histoireToEdit.setAuteur(auteur);
                histoireToEdit.setPays(pays);
                histoireToEdit.setCategorie(categorie); // Set category
                histoireToEdit.setDate(date);
                
                // Update image path if needed
                if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                    histoireToEdit.setImagePath(currentPhotoPath);
                }

                // Update in database
                success = db.updateHistoire(histoireToEdit);

                if (success) {
                    Toast.makeText(this, "Story updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update story", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Create new story
                Histoire newHistoire = new Histoire();
                newHistoire.setTitre(titre);
                newHistoire.setContenu(contenu);
                newHistoire.setAuteur(auteur);
                newHistoire.setPays(pays);
                newHistoire.setCategorie(categorie); // Set category
                newHistoire.setDate(date);
                
                // Set image path if available
                if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                    newHistoire.setImagePath(currentPhotoPath);
                }

                // Save to database
                long id = db.addHistoire(newHistoire);

                if (id > 0) {
                    Toast.makeText(this, "Story saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save story", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving story: " + e.getMessage(), e);
            Toast.makeText(this, "Error saving story: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
