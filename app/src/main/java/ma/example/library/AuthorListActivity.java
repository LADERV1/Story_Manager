package ma.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AuthorListActivity extends AppCompatActivity {

    private static final String TAG = "AuthorListActivity";

    private Toolbar toolbar;
    private RecyclerView authorRecyclerView;
    private FloatingActionButton fabAddAuthor;
    private LinearLayout emptyView;
    private AuthorAdapter authorAdapter;
    private List<Author> authorList;
    private AuthorDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_list);

        try {
            // Initialize database
            db = new AuthorDatabase(this);

            // Initialize views with the exact IDs from your layout
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            authorRecyclerView = findViewById(R.id.authorRecyclerView);
            fabAddAuthor = findViewById(R.id.fabAddAuthor);
            emptyView = findViewById(R.id.emptyView);

            // Set up RecyclerView
            authorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            
            // Initialize author list
            authorList = new ArrayList<>();
            loadAuthors();

            // Set up adapter
            authorAdapter = new AuthorAdapter(this, authorList);
            authorRecyclerView.setAdapter(authorAdapter);

            // Set up FAB click listener
            fabAddAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "FAB clicked, opening AddEditAuthorActivity");
                    Intent intent = new Intent(AuthorListActivity.this, AddEditAuthorActivity.class);
                    startActivity(intent);
                }
            });

            Log.d(TAG, "AuthorListActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing AuthorListActivity: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        loadAuthors();
        if (authorAdapter != null) {
            authorAdapter.notifyDataSetChanged();
        }
    }

    private void loadAuthors() {
        try {
            // Clear the current list
            authorList.clear();

            // Load authors from database
            List<Author> authors = db.getAllAuthors();
            authorList.addAll(authors);
            Log.d(TAG, "Loaded " + authorList.size() + " authors");

            // Show empty view if no authors
            updateEmptyViewVisibility();
        } catch (Exception e) {
            Log.e(TAG, "Error loading authors: " + e.getMessage(), e);
        }
    }

    private void updateEmptyViewVisibility() {
        if (authorList.isEmpty()) {
            authorRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            authorRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    // Method to handle author deletion (called from adapter)
    public void deleteAuthor(int id, int position) {
        try {
            // Delete from database
            boolean success = db.deleteAuthor(id);

            if (success) {
                authorList.remove(position);
                authorAdapter.notifyItemRemoved(position);
                updateEmptyViewVisibility();
                Log.d(TAG, "Author deleted successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting author: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
