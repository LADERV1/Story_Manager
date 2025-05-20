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

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private static final String TAG = "CountryAdapter";

    private Context context;
    private List<Country> countryList;
    private CountryDatabase db;

    public CountryAdapter(Context context, List<Country> countryList) {
        this.context = context;
        this.countryList = countryList;
        this.db = new CountryDatabase(context);
        Log.d(TAG, "Adapter initialized with " + countryList.size() + " items");
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.item_country, parent, false);
            return new CountryViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating view: " + e.getMessage(), e);
            // Fallback to a simpler layout if needed
            View fallbackView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new CountryViewHolder(fallbackView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        try {
            Country country = countryList.get(position);

            // Set the country name
            if (holder.countryNameTextView != null) {
                holder.countryNameTextView.setText(country.getName());
            }

            // Set the country flag if available
            if (holder.countryFlagImageView != null && country.getFlagPath() != null && !country.getFlagPath().isEmpty()) {
                try {
                    File imgFile = new File(country.getFlagPath());
                    if (imgFile.exists()) {
                        holder.countryFlagImageView.setImageURI(Uri.fromFile(imgFile));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading flag image: " + e.getMessage(), e);
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
                                Country clickedCountry = countryList.get(adapterPosition);
                                Log.d(TAG, "Edit button clicked for country ID: " + clickedCountry.getId());

                                // Navigate to edit screen
                                Intent intent = new Intent(context, AddEditCountryActivity.class);
                                intent.putExtra("COUNTRY_ID", clickedCountry.getId());
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
                                confirmDelete(countryList.get(adapterPosition).getId(), adapterPosition);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error handling delete click: " + e.getMessage(), e);
                            Toast.makeText(context, "Error deleting country", Toast.LENGTH_SHORT).show();
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
                    .setTitle("Delete Country")
                    .setMessage("Are you sure you want to delete this country?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        try {
                            // Delete the country
                            if (context instanceof CountryListActivity) {
                                ((CountryListActivity) context).deleteCountry(id, position);
                                Log.d(TAG, "Country deleted, ID: " + id);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting country: " + e.getMessage(), e);
                            Toast.makeText(context, "Error deleting country", Toast.LENGTH_SHORT).show();
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
        return countryList.size();
    }

    public static class CountryViewHolder extends RecyclerView.ViewHolder {
        ImageView countryFlagImageView;
        TextView countryNameTextView;
        MaterialButton editButton;
        MaterialButton deleteButton;

        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                // Find views with the exact IDs from your layout
                countryFlagImageView = itemView.findViewById(R.id.countryFlagImageView);
                countryNameTextView = itemView.findViewById(R.id.countryNameTextView);
                editButton = itemView.findViewById(R.id.editButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);

                Log.d("CountryViewHolder", "ViewHolder initialized successfully");
            } catch (Exception e) {
                Log.e("CountryViewHolder", "Error finding views: " + e.getMessage(), e);
            }
        }
    }
}