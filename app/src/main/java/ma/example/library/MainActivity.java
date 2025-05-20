package ma.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    
    private MaterialButton viewStoriesButton;
    private MaterialButton addStoryButton;
    private MaterialButton manageAuthorsButton;
    private MaterialButton manageCategoriesButton;
    private MaterialButton manageCountriesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize buttons
        viewStoriesButton = findViewById(R.id.viewStoriesButton);
        addStoryButton = findViewById(R.id.addStoryButton);
        manageAuthorsButton = findViewById(R.id.manageAuthorsButton);
        manageCategoriesButton = findViewById(R.id.manageCategoriesButton);
        manageCountriesButton = findViewById(R.id.manageCountriesButton);

        // Set up click listeners
        viewStoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookListActivity.class);
                startActivity(intent);
            }
        });

        addStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditStoryActivity.class);
                startActivity(intent);
            }
        });

        manageAuthorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AuthorListActivity.class);
                startActivity(intent);
            }
        });

        manageCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryListActivity.class);
                startActivity(intent);
            }
        });

        manageCountriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CountryListActivity.class);
                startActivity(intent);
            }
        });
    }
}