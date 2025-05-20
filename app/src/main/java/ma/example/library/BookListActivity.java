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

public class BookListActivity extends AppCompatActivity {

    private static final String TAG = "BookListActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddStory;
    private HistoireAdapter histoireAdapter;
    private List<Histoire> histoireList;
    private HistoireDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        try {
            // Initialize database
            db = new HistoireDatabase(this);

            // Initialize views with the exact IDs from your layout
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            recyclerView = findViewById(R.id.recyclerView);
            fabAddStory = findViewById(R.id.fabAddStory);

            // Set up RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Initialize story list
            histoireList = new ArrayList<>();
            loadStories();

            // Set up adapter
            histoireAdapter = new HistoireAdapter(this, histoireList);
            recyclerView.setAdapter(histoireAdapter);

            // Set up FAB click listener
            fabAddStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BookListActivity.this, AddEditStoryActivity.class);
                    startActivity(intent);
                }
            });

            Log.d(TAG, "BookListActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing BookListActivity: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        loadStories();
        if (histoireAdapter != null) {
            histoireAdapter.notifyDataSetChanged();
        }
    }

    private void loadStories() {
        try {
            // Clear the current list
            histoireList.clear();

            // Load stories from database
            List<Histoire> stories = db.getAllHistoires();
            histoireList.addAll(stories);
            Log.d(TAG, "Loaded " + histoireList.size() + " stories");
        } catch (Exception e) {
            Log.e(TAG, "Error loading stories: " + e.getMessage(), e);
        }
    }

    // Method to handle story deletion (called from adapter)
    public void deleteStory(int id, int position) {
        try {
            // Delete from database
            boolean success = db.deleteHistoire(id);

            if (success) {
                histoireList.remove(position);
                histoireAdapter.notifyItemRemoved(position);
                Log.d(TAG, "Story deleted successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting story: " + e.getMessage(), e);
        }
    }
}