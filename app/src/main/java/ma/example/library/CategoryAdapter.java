package ma.example.library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private static final String TAG = "CategoryAdapter";

    private Context context;
    private List<Category> categoryList;
    private CategoryDatabase db;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.db = new CategoryDatabase(context);
        Log.d(TAG, "Adapter initialized with " + categoryList.size() + " items");
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating view: " + e.getMessage(), e);
            // Fallback to a simpler layout if needed
            View fallbackView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new CategoryViewHolder(fallbackView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        try {
            Category category = categoryList.get(position);

            // Set the category name
            if (holder.categoryNameTextView != null) {
                holder.categoryNameTextView.setText(category.getName());
            }

            // Set the category image if available
            if (holder.categoryImageView != null) {
                String imagePath = category.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            Uri imageUri = Uri.fromFile(imgFile);
                            holder.categoryImageView.setImageURI(null); // Clear the image first to prevent caching issues
                            holder.categoryImageView.setImageURI(imageUri);
                            Log.d(TAG, "Successfully loaded image for category: " + category.getName());
                        } else {
                            Log.d(TAG, "Image file doesn't exist: " + imagePath);
                            // Set default image if file doesn't exist
                            holder.categoryImageView.setImageResource(android.R.drawable.ic_menu_gallery);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading image: " + e.getMessage(), e);
                        holder.categoryImageView.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } else {
                    Log.d(TAG, "No image path for category: " + category.getName());
                    // Set default image if no path
                    holder.categoryImageView.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }

            // Set up edit button click listener
            if (holder.editButton != null) {
                holder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                Category clickedCategory = categoryList.get(adapterPosition);
                                Log.d(TAG, "Edit button clicked for category ID: " + clickedCategory.getId());

                                // Navigate to edit screen
                                Intent intent = new Intent(context, AddEditCategoryActivity.class);
                                intent.putExtra("CATEGORY_ID", clickedCategory.getId());
                                context.startActivity(intent);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error handling edit click: " + e.getMessage(), e);
                            Toast.makeText(context, "Error opening edit screen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            // Set up delete button click listener
            if (holder.deleteButton != null) {
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                Log.d(TAG, "Delete button clicked for position: " + adapterPosition);
                                confirmDelete(categoryList.get(adapterPosition).getId(), adapterPosition);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error handling delete click: " + e.getMessage(), e);
                            Toast.makeText(context, "Error deleting category", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder: " + e.getMessage(), e);
        }
    }

    private void confirmDelete(int id, int position) {
        try {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to delete this category?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        try {
                            // Delete the category
                            if (context instanceof CategoryListActivity) {
                                ((CategoryListActivity) context).deleteCategory(id, position);
                                Log.d(TAG, "Category deleted, ID: " + id);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting category: " + e.getMessage(), e);
                            Toast.makeText(context, "Error deleting category", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing delete confirmation: " + e.getMessage(), e);
            Toast.makeText(context, "Error showing delete dialog", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImageView;
        TextView categoryNameTextView;
        MaterialButton editButton;
        MaterialButton deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                // Find views with the exact IDs from your layout
                categoryImageView = itemView.findViewById(R.id.categoryImageView);
                categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
                editButton = itemView.findViewById(R.id.editButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);

                Log.d("CategoryViewHolder", "ViewHolder initialized successfully");
            } catch (Exception e) {
                Log.e("CategoryViewHolder", "Error finding views: " + e.getMessage(), e);
            }
        }
    }
}
