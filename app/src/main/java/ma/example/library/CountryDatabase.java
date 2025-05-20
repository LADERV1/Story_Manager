package ma.example.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CountryDatabase {
    private static final String TAG = "CountryDatabase";
    
    private DatabaseHelper dbHelper;
    
    public CountryDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    
    // Add a new country
    public long addCountry(Country country) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_COUNTRY_NAME, country.getName());
            values.put(DatabaseHelper.COLUMN_COUNTRY_FLAG_PATH, country.getFlagPath());
            
            long id = db.insert(DatabaseHelper.TABLE_COUNTRIES, null, values);
            db.close();
            
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error adding country: " + e.getMessage(), e);
            return -1;
        }
    }
    
    // Get a single country by ID
    public Country getCountry(int id) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            
            Cursor cursor = db.query(
                DatabaseHelper.TABLE_COUNTRIES,
                new String[] {
                    DatabaseHelper.COLUMN_COUNTRY_ID,
                    DatabaseHelper.COLUMN_COUNTRY_NAME,
                    DatabaseHelper.COLUMN_COUNTRY_FLAG_PATH
                },
                DatabaseHelper.COLUMN_COUNTRY_ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null
            );
            
            Country country = null;
            
            if (cursor != null && cursor.moveToFirst()) {
                country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_COUNTRY_ID)));
                country.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COUNTRY_NAME)));
                country.setFlagPath(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COUNTRY_FLAG_PATH)));
                
                cursor.close();
            }
            
            db.close();
            return country;
        } catch (Exception e) {
            Log.e(TAG, "Error getting country: " + e.getMessage(), e);
            return null;
        }
    }
    
    // Get all countries
    public List<Country> getAllCountries() {
        List<Country> countryList = new ArrayList<>();
        
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_COUNTRIES;
            
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            
            if (cursor.moveToFirst()) {
                do {
                    Country country = new Country();
                    country.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_COUNTRY_ID)));
                    country.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COUNTRY_NAME)));
                    country.setFlagPath(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COUNTRY_FLAG_PATH)));
                    
                    countryList.add(country);
                } while (cursor.moveToNext());
            }
            
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all countries: " + e.getMessage(), e);
        }
        
        return countryList;
    }
    
    // Update a country
    public boolean updateCountry(Country country) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_COUNTRY_NAME, country.getName());
            values.put(DatabaseHelper.COLUMN_COUNTRY_FLAG_PATH, country.getFlagPath());
            
            int rowsAffected = db.update(
                DatabaseHelper.TABLE_COUNTRIES,
                values,
                DatabaseHelper.COLUMN_COUNTRY_ID + "=?",
                new String[] { String.valueOf(country.getId()) }
            );
            
            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating country: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Delete a country
    public boolean deleteCountry(int id) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            int rowsAffected = db.delete(
                DatabaseHelper.TABLE_COUNTRIES,
                DatabaseHelper.COLUMN_COUNTRY_ID + "=?",
                new String[] { String.valueOf(id) }
            );
            
            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting country: " + e.getMessage(), e);
            return false;
        }
    }
}