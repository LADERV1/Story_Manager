package ma.example.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HistoireDatabase {
    private static final String TAG = "HistoireDatabase";
    
    private DatabaseHelper dbHelper;
    
    public HistoireDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    
    // Add a new histoire
    public long addHistoire(Histoire histoire) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_HISTOIRE_TITRE, histoire.getTitre());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_CONTENU, histoire.getContenu());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_AUTEUR, histoire.getAuteur());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_PAYS, histoire.getPays());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_DATE, histoire.getDate());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_IMAGE_PATH, histoire.getImagePath());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_CATEGORIE, histoire.getCategorie()); // Added category
            
            long id = db.insert(DatabaseHelper.TABLE_HISTOIRES, null, values);
            db.close();
            
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error adding histoire: " + e.getMessage(), e);
            return -1;
        }
    }
    
    // Get a single histoire by ID
    public Histoire getHistoire(int id) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            
            Cursor cursor = db.query(
                DatabaseHelper.TABLE_HISTOIRES,
                new String[] {
                    DatabaseHelper.COLUMN_HISTOIRE_ID,
                    DatabaseHelper.COLUMN_HISTOIRE_TITRE,
                    DatabaseHelper.COLUMN_HISTOIRE_CONTENU,
                    DatabaseHelper.COLUMN_HISTOIRE_AUTEUR,
                    DatabaseHelper.COLUMN_HISTOIRE_PAYS,
                    DatabaseHelper.COLUMN_HISTOIRE_DATE,
                    DatabaseHelper.COLUMN_HISTOIRE_IMAGE_PATH,
                    DatabaseHelper.COLUMN_HISTOIRE_CATEGORIE // Added category
                },
                DatabaseHelper.COLUMN_HISTOIRE_ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null
            );
            
            Histoire histoire = null;
            
            if (cursor != null && cursor.moveToFirst()) {
                histoire = new Histoire(
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_TITRE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_CONTENU)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_AUTEUR)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_PAYS)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_DATE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_IMAGE_PATH))
                );
                
                // Set category separately
                histoire.setCategorie(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_CATEGORIE)));
                
                cursor.close();
            }
            
            db.close();
            return histoire;
        } catch (Exception e) {
            Log.e(TAG, "Error getting histoire: " + e.getMessage(), e);
            return null;
        }
    }
    
    // Get all histoires
    public List<Histoire> getAllHistoires() {
        List<Histoire> histoireList = new ArrayList<>();
        
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_HISTOIRES;
            
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            
            if (cursor.moveToFirst()) {
                do {
                    Histoire histoire = new Histoire(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_TITRE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_CONTENU)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_AUTEUR)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_PAYS)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_DATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_IMAGE_PATH))
                    );
                    
                    // Set category separately
                    histoire.setCategorie(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_CATEGORIE)));
                    
                    histoireList.add(histoire);
                } while (cursor.moveToNext());
            }
            
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all histoires: " + e.getMessage(), e);
        }
        
        return histoireList;
    }
    
    // Update a histoire
    public boolean updateHistoire(Histoire histoire) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_HISTOIRE_TITRE, histoire.getTitre());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_CONTENU, histoire.getContenu());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_AUTEUR, histoire.getAuteur());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_PAYS, histoire.getPays());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_DATE, histoire.getDate());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_IMAGE_PATH, histoire.getImagePath());
            values.put(DatabaseHelper.COLUMN_HISTOIRE_CATEGORIE, histoire.getCategorie()); // Added category
            
            int rowsAffected = db.update(
                DatabaseHelper.TABLE_HISTOIRES,
                values,
                DatabaseHelper.COLUMN_HISTOIRE_ID + "=?",
                new String[] { String.valueOf(histoire.getId()) }
            );
            
            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating histoire: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Delete a histoire
    public boolean deleteHistoire(int id) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            int rowsAffected = db.delete(
                DatabaseHelper.TABLE_HISTOIRES,
                DatabaseHelper.COLUMN_HISTOIRE_ID + "=?",
                new String[] { String.valueOf(id) }
            );
            
            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting histoire: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Get histoires by category
    public List<Histoire> getHistoiresByCategory(String category) {
        List<Histoire> histoireList = new ArrayList<>();
        
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            
            Cursor cursor = db.query(
                DatabaseHelper.TABLE_HISTOIRES,
                new String[] {
                    DatabaseHelper.COLUMN_HISTOIRE_ID,
                    DatabaseHelper.COLUMN_HISTOIRE_TITRE,
                    DatabaseHelper.COLUMN_HISTOIRE_CONTENU,
                    DatabaseHelper.COLUMN_HISTOIRE_AUTEUR,
                    DatabaseHelper.COLUMN_HISTOIRE_PAYS,
                    DatabaseHelper.COLUMN_HISTOIRE_DATE,
                    DatabaseHelper.COLUMN_HISTOIRE_IMAGE_PATH,
                    DatabaseHelper.COLUMN_HISTOIRE_CATEGORIE
                },
                DatabaseHelper.COLUMN_HISTOIRE_CATEGORIE + "=?",
                new String[] { category },
                null, null, null, null
            );
            
            if (cursor.moveToFirst()) {
                do {
                    Histoire histoire = new Histoire(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_TITRE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_CONTENU)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_AUTEUR)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_PAYS)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_DATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_IMAGE_PATH))
                    );
                    
                    // Set category separately
                    histoire.setCategorie(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HISTOIRE_CATEGORIE)));
                    
                    histoireList.add(histoire);
                } while (cursor.moveToNext());
            }
            
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting histoires by category: " + e.getMessage(), e);
        }
        
        return histoireList;
    }
}
