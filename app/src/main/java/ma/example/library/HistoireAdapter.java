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

public class HistoireAdapter extends RecyclerView.Adapter<HistoireAdapter.HistoireViewHolder> {

    private static final String TAG = "HistoireAdapter";

    private Context context;
    private List<Histoire> histoireList;

    public HistoireAdapter(Context context, List<Histoire> histoireList) {
        this.context = context;
        this.histoireList = histoireList;
        Log.d(TAG, "Adapter initialized with " + histoireList.size() + " items");
    }

    @NonNull
    @Override
    public HistoireViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
            return new HistoireViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating view: " + e.getMessage(), e);
            // Fallback to a simpler layout if needed
            View fallbackView = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
            return new HistoireViewHolder(fallbackView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HistoireViewHolder holder, int position) {
        try {
            Histoire histoire = histoireList.get(position);

            // Set the title
            if (holder.titleTextView != null) {
                holder.titleTextView.setText(histoire.getTitre());
            }

            // Set the content preview
            if (holder.contentTextView != null) {
                holder.contentTextView.setText(histoire.getContenu());
            }

            // Set the author
            if (holder.authorTextView != null) {
                holder.authorTextView.setText("Author: " + histoire.getAuteur());
            }

            // Set the category (now using the actual category field)
            if (holder.categoryTextView != null) {
                String category = histoire.getCategorie();
                if (category != null && !category.isEmpty()) {
                    holder.categoryTextView.setText("Category: " + category);
                } else {
                    // Fallback to country if category is not set
                    holder.categoryTextView.setText("Country: " + histoire.getPays());
                }
            }

            // Load image if available
            if (holder.storyImageView != null) {
                String imagePath = histoire.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            Uri imageUri = Uri.fromFile(imgFile);
                            holder.storyImageView.setImageURI(null); // Clear image first
                            holder.storyImageView.setImageURI(imageUri);
                            Log.d(TAG, "Successfully loaded image for story: " + histoire.getTitre());
                        } else {
                            Log.d(TAG, "Image file doesn't exist: " + imagePath);
                            // Set default image
                            holder.storyImageView.setImageResource(android.R.drawable.ic_menu_gallery);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading image: " + e.getMessage(), e);
                        holder.storyImageView.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } else {
                    Log.d(TAG, "No image path for story: " + histoire.getTitre());
                    // Set default image
                    holder.storyImageView.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }

            // Set up view button click listener
            if (holder.viewButton != null) {
                holder.viewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                Histoire clickedHistoire = histoireList.get(adapterPosition);
                                Log.d(TAG, "View button clicked for histoire ID: " + clickedHistoire.getId());

                                // Navigate to view screen
                                Intent intent = new Intent(context, StoryViewActivity.class);
                                intent.putExtra("HISTOIRE_ID", clickedHistoire.getId());
                                context.startActivity(intent);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error handling view click: " + e.getMessage(), e);
                            Toast.makeText(context, "Error opening view screen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            // Set up edit button click listener
            if (holder.editButton != null) {
                holder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                Histoire clickedHistoire = histoireList.get(adapterPosition);
                                Log.d(TAG, "Edit button clicked for histoire ID: " + clickedHistoire.getId());

                                // Navigate to edit screen
                                Intent intent = new Intent(context, AddEditStoryActivity.class);
                                intent.putExtra("HISTOIRE_ID", clickedHistoire.getId());
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
                                confirmDelete(histoireList.get(adapterPosition).getId(), adapterPosition);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error handling delete click: " + e.getMessage(), e);
                            Toast.makeText(context, "Error deleting story", Toast.LENGTH_SHORT).show();
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
                    .setTitle("Delete Story")
                    .setMessage("Are you sure you want to delete this story?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        try {
                            // Delete the story
                            if (context instanceof BookListActivity) {
                                ((BookListActivity) context).deleteStory(id, position);
                                Log.d(TAG, "Story deleted, ID: " + id);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting story: " + e.getMessage(), e);
                            Toast.makeText(context, "Error deleting story", Toast.LENGTH_SHORT).show();
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
        return histoireList.size();
    }

    public static class HistoireViewHolder extends RecyclerView.ViewHolder {
        ImageView storyImageView;
        TextView titleTextView;
        TextView contentTextView;
        TextView authorTextView;
        TextView categoryTextView;
        MaterialButton viewButton;
        MaterialButton editButton;
        MaterialButton deleteButton;

        public HistoireViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                // Find views with the exact IDs from your layout
                storyImageView = itemView.findViewById(R.id.storyThumbnailImageView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                contentTextView = itemView.findViewById(R.id.contentTextView);
                authorTextView = itemView.findViewById(R.id.authorTextView);
                categoryTextView = itemView.findViewById(R.id.categoryTextView);
                viewButton = itemView.findViewById(R.id.viewButton);
                editButton = itemView.findViewById(R.id.editButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);

                Log.d("HistoireViewHolder", "ViewHolder initialized successfully");
            } catch (Exception e) {
                Log.e("HistoireViewHolder", "Error finding views: " + e.getMessage(), e);
            }
        }
    }
}
