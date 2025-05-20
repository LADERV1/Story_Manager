package ma.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CountryListActivity extends AppCompatActivity {

    private static final String TAG = "CountryListActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddCountry;
    private CountryAdapter countryAdapter;
    private List<Country> countryList;
    private CountryDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        try {
            // Initialize database
            db = new CountryDatabase(this);

            // Initialize views with the exact IDs from your layout
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            recyclerView = findViewById(R.id.recyclerView);
            fabAddCountry = findViewById(R.id.fabAddCountry);

            // Set up RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Initialize country list
            countryList = new ArrayList<>();
            loadCountries();

            // Set up adapter
            countryAdapter = new CountryAdapter(this, countryList);
            recyclerView.setAdapter(countryAdapter);

            // Set up FAB click listener to navigate to AddEditCountryActivity
            fabAddCountry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CountryListActivity.this, AddEditCountryActivity.class);
                    startActivity(intent);
                }
            });
            
            Log.d(TAG, "CountryListActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing CountryListActivity: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        loadCountries();
        if (countryAdapter != null) {
            countryAdapter.notifyDataSetChanged();
        }
    }

    private void loadCountries() {
        try {
            // Clear the current list
            countryList.clear();

            // Load countries from database
            List<Country> countries = db.getAllCountries();
            countryList.addAll(countries);
            Log.d(TAG, "Loaded " + countryList.size() + " countries");
        } catch (Exception e) {
            Log.e(TAG, "Error loading countries: " + e.getMessage(), e);
        }
    }

    // Method to handle country deletion (called from adapter)
    public void deleteCountry(int id, int position) {
        try {
            // Delete from database
            boolean success = db.deleteCountry(id);

            if (success) {
                countryList.remove(position);
                countryAdapter.notifyItemRemoved(position);
                Log.d(TAG, "Country deleted successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting country: " + e.getMessage(), e);
        }
    }
}