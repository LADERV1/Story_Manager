package ma.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity {

    private static final String TAG = "CategoryListActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddCategory;
    private EditText searchEditText;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private List<Category> filteredCategoryList;
    private CategoryDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        try {
            // Initialize database
            db = new CategoryDatabase(this);

            // Initialize views with the exact IDs from your layout
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            recyclerView = findViewById(R.id.recyclerView);
            fabAddCategory = findViewById(R.id.fabAddCategory);
            searchEditText = findViewById(R.id.searchEditText);

            // Set up RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Initialize category lists
            categoryList = new ArrayList<>();
            filteredCategoryList = new ArrayList<>();
            loadCategories();

            // Set up adapter with filtered list
            categoryAdapter = new CategoryAdapter(this, filteredCategoryList);
            recyclerView.setAdapter(categoryAdapter);

            // Set up search functionality
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Not needed
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Filter the list as text changes
                    filterCategories(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Not needed
                }
            });

            // Handle search action on keyboard
            searchEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    filterCategories(searchEditText.getText().toString());
                    return true;
                }
                return false;
            });

            // Set up FAB click listener
            fabAddCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CategoryListActivity.this, AddEditCategoryActivity.class);
                    startActivity(intent);
                }
            });
            
            Log.d(TAG, "CategoryListActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing CategoryListActivity: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        loadCategories();
        // Apply current filter
        filterCategories(searchEditText.getText().toString());
    }

    private void loadCategories() {
        try {
            // Clear the current list
            categoryList.clear();

            // Load categories from database
            List<Category> categories = db.getAllCategories();
            
            // Debug: Log image paths
            for (Category category : categories) {
                Log.d(TAG, "Category: " + category.getName() + 
                      ", Image path: " + (category.getImagePath() != null ? category.getImagePath() : "null"));
            }
            
            categoryList.addAll(categories);
            Log.d(TAG, "Loaded " + categoryList.size() + " categories");
            
            // Initialize filtered list with all categories
            filteredCategoryList.clear();
            filteredCategoryList.addAll(categoryList);
            
            // Notify adapter of data change
            if (categoryAdapter != null) {
                categoryAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading categories: " + e.getMessage(), e);
        }
    }

    private void filterCategories(String query) {
        try {
            Log.d(TAG, "Filtering categories with query: " + query);
            filteredCategoryList.clear();
            
            if (query == null || query.isEmpty()) {
                // If query is empty, show all categories
                filteredCategoryList.addAll(categoryList);
                Log.d(TAG, "Empty query, showing all " + categoryList.size() + " categories");
            } else {
                // Filter categories by name (case-insensitive)
                String lowerCaseQuery = query.toLowerCase();
                for (Category category : categoryList) {
                    if (category.getName().toLowerCase().contains(lowerCaseQuery)) {
                        filteredCategoryList.add(category);
                    }
                }
                Log.d(TAG, "Found " + filteredCategoryList.size() + " categories matching query: " + query);
            }
            
            // Update the adapter
            if (categoryAdapter != null) {
                categoryAdapter.notifyDataSetChanged();
                Log.d(TAG, "Adapter notified of data change");
            } else {
                Log.e(TAG, "Adapter is null, cannot update");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error filtering categories: " + e.getMessage(), e);
        }
    }

    // Method to handle category deletion (called from adapter)
    public void deleteCategory(int id, int position) {
        try {
            // Find the category in the original list
            Category categoryToDelete = null;
            for (Category category : categoryList) {
                if (category.getId() == id) {
                    categoryToDelete = category;
                    break;
                }
            }
            
            // Delete from database
            boolean success = db.deleteCategory(id);

            if (success) {
                // Remove from both lists
                if (categoryToDelete != null) {
                    categoryList.remove(categoryToDelete);
                }
                filteredCategoryList.remove(position);
                categoryAdapter.notifyItemRemoved(position);
                Log.d(TAG, "Category deleted successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting category: " + e.getMessage(), e);
        }
    }
}
