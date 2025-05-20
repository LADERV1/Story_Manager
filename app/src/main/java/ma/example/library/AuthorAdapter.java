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

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder> {

    private static final String TAG = "AuthorAdapter";

    private Context context;
    private List<Author> authorList;
    private AuthorDatabase db;

    public AuthorAdapter(Context context, List<Author> authorList) {
        this.context = context;
        this.authorList = authorList;
        this.db = new AuthorDatabase(context);
        Log.d(TAG, "Adapter initialized with " + authorList.size() + " items");
    }

    @NonNull
    @Override
    public AuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.item_author, parent, false);
            return new AuthorViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating view: " + e.getMessage(), e);
            // Fallback to a simpler layout if needed
            View fallbackView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new AuthorViewHolder(fallbackView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorViewHolder holder, int position) {
        try {
            Author author = authorList.get(position);

            // Set the author name
            if (holder.authorNameTextView != null) {
                holder.authorNameTextView.setText(author.getName());
            }

            // Set the author date of birth if available
            if (holder.authorDateTextView != null && author.getDob() != null) {
                holder.authorDateTextView.setText("Born: " + author.getDob());
            }

            // Set the author bio if available
            if (holder.authorDescTextView != null && author.getBio() != null) {
                holder.authorDescTextView.setText(author.getBio());
            }

            // Set the author image if available
            if (holder.authorImageView != null && author.getImagePath() != null && !author.getImagePath().isEmpty()) {
                try {
                    File imgFile = new File(author.getImagePath());
                    if (imgFile.exists()) {
                        holder.authorImageView.setImageURI(Uri.fromFile(imgFile));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading author image: " + e.getMessage(), e);
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
                                Author clickedAuthor = authorList.get(adapterPosition);
                                Log.d(TAG, "Edit button clicked for author ID: " + clickedAuthor.getId());

                                // Navigate to edit screen
                                Intent intent = new Intent(context, AddEditAuthorActivity.class);
                                intent.putExtra("AUTHOR_ID", clickedAuthor.getId());
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
                                confirmDelete(authorList.get(adapterPosition).getId(), adapterPosition);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error handling delete click: " + e.getMessage(), e);
                            Toast.makeText(context, "Error deleting author", Toast.LENGTH_SHORT).show();
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
                    .setTitle("Delete Author")
                    .setMessage("Are you sure you want to delete this author?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        try {
                            // Delete the author
                            if (context instanceof AuthorListActivity) {
                                ((AuthorListActivity) context).deleteAuthor(id, position);
                                Log.d(TAG, "Author deleted, ID: " + id);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting author: " + e.getMessage(), e);
                            Toast.makeText(context, "Error deleting author", Toast.LENGTH_SHORT).show();
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
        return authorList.size();
    }

    public static class AuthorViewHolder extends RecyclerView.ViewHolder {
        ImageView authorImageView;
        TextView authorNameTextView;
        TextView authorDateTextView;
        TextView authorDescTextView;
        MaterialButton editButton;
        MaterialButton deleteButton;

        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                // Find views with the exact IDs from your layout
                authorImageView = itemView.findViewById(R.id.authorImageView);
                authorNameTextView = itemView.findViewById(R.id.authorNameTextView);
                authorDateTextView = itemView.findViewById(R.id.authorDateTextView);
                authorDescTextView = itemView.findViewById(R.id.authorDescTextView);
                editButton = itemView.findViewById(R.id.editButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);

                Log.d("AuthorViewHolder", "ViewHolder initialized successfully");
            } catch (Exception e) {
                Log.e("AuthorViewHolder", "Error finding views: " + e.getMessage(), e);
            }
        }
    }
}