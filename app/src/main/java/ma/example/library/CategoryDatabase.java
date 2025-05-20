package ma.example.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CategoryDatabase {
    private static final String TAG = "CategoryDatabase";
    
    private DatabaseHelper dbHelper;
    
    public CategoryDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    
    // Add a new category
    public long addCategory(Category category) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
            values.put(DatabaseHelper.COLUMN_CATEGORY_IMAGE_PATH, category.getImagePath());
            
            long id = db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
            db.close();
            
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error adding category: " + e.getMessage(), e);
            return -1;
        }
    }
    
    // Get a single category by ID
    public Category getCategory(int id) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            
            Cursor cursor = db.query(
                DatabaseHelper.TABLE_CATEGORIES,
                new String[] {
                    DatabaseHelper.COLUMN_CATEGORY_ID,
                    DatabaseHelper.COLUMN_CATEGORY_NAME,
                    DatabaseHelper.COLUMN_CATEGORY_IMAGE_PATH
                },
                DatabaseHelper.COLUMN_CATEGORY_ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null
            );
            
            Category category = null;
            
            if (cursor != null && cursor.moveToFirst()) {
                try {
                    int idIndex = DatabaseHelper.getColumnIndex(cursor, DatabaseHelper.COLUMN_CATEGORY_ID);
                    int nameIndex = DatabaseHelper.getColumnIndex(cursor, DatabaseHelper.COLUMN_CATEGORY_NAME);
                    int imagePathIndex = DatabaseHelper.getColumnIndex(cursor, DatabaseHelper.COLUMN_CATEGORY_IMAGE_PATH);
                    
                    category = new Category();
                    category.setId(cursor.getInt(idIndex));
                    category.setName(cursor.getString(nameIndex));
                    category.setImagePath(cursor.getString(imagePathIndex));
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing cursor data: " + e.getMessage(), e);
                }
                
                cursor.close();
            }
            
            db.close();
            return category;
        } catch (Exception e) {
            Log.e(TAG, "Error getting category: " + e.getMessage(), e);
            return null;
        }
    }
    
    // Get all categories
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES;
            
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            
            if (cursor.moveToFirst()) {
                do {
                    try {
                        int idIndex = DatabaseHelper.getColumnIndex(cursor, DatabaseHelper.COLUMN_CATEGORY_ID);
                        int nameIndex = DatabaseHelper.getColumnIndex(cursor, DatabaseHelper.COLUMN_CATEGORY_NAME);
                        int imagePathIndex = DatabaseHelper.getColumnIndex(cursor, DatabaseHelper.COLUMN_CATEGORY_IMAGE_PATH);
                        
                        Category category = new Category();
                        category.setId(cursor.getInt(idIndex));
                        category.setName(cursor.getString(nameIndex));
                        category.setImagePath(cursor.getString(imagePathIndex));
                        
                        categoryList.add(category);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing cursor data: " + e.getMessage(), e);
                    }
                } while (cursor.moveToNext());
            }
            
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all categories: " + e.getMessage(), e);
        }
        
        return categoryList;
    }
    
    // Update a category
    public boolean updateCategory(Category category) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
            values.put(DatabaseHelper.COLUMN_CATEGORY_IMAGE_PATH, category.getImagePath());
            
            int rowsAffected = db.update(
                DatabaseHelper.TABLE_CATEGORIES,
                values,
                DatabaseHelper.COLUMN_CATEGORY_ID + "=?",
                new String[] { String.valueOf(category.getId()) }
            );
            
            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating category: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Delete a category
    public boolean deleteCategory(int id) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            int rowsAffected = db.delete(
                DatabaseHelper.TABLE_CATEGORIES,
                DatabaseHelper.COLUMN_CATEGORY_ID + "=?",
                new String[] { String.valueOf(id) }
            );
            
            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting category: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Count stories in a category
    public int countStoriesInCategory(String category) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            
            String countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_HISTOIRES +
                " WHERE " + DatabaseHelper.COLUMN_HISTOIRE_PAYS + "=?";
            
            Cursor cursor = db.rawQuery(countQuery, new String[] { category });
            
            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
            
            db.close();
            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting stories in category: " + e.getMessage(), e);
            return 0;
        }
    }
}