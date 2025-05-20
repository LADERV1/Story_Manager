package ma.example.library;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    
    // Database Info
    private static final String DATABASE_NAME = "library.db";
    private static final int DATABASE_VERSION = 2; // Increased version number for migration
    
    // Table Names
    public static final String TABLE_HISTOIRES = "histoires";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_AUTHORS = "authors";
    public static final String TABLE_COUNTRIES = "countries";
    
    // Common column names
    public static final String COLUMN_ID = "id";
    
    // Histoire Table Columns
    public static final String COLUMN_HISTOIRE_ID = "id";
    public static final String COLUMN_HISTOIRE_TITRE = "titre";
    public static final String COLUMN_HISTOIRE_CONTENU = "contenu";
    public static final String COLUMN_HISTOIRE_AUTEUR = "auteur";
    public static final String COLUMN_HISTOIRE_PAYS = "pays";
    public static final String COLUMN_HISTOIRE_DATE = "date";
    public static final String COLUMN_HISTOIRE_IMAGE_PATH = "image_path";
    public static final String COLUMN_HISTOIRE_CATEGORIE = "categorie"; // Added category column
    
    // Category Table Columns
    public static final String COLUMN_CATEGORY_ID = "id";
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_IMAGE_PATH = "image_path";
    
    // Author Table Columns
    public static final String COLUMN_AUTHOR_ID = "id";
    public static final String COLUMN_AUTHOR_NAME = "name";
    public static final String COLUMN_AUTHOR_DOB = "dob";
    public static final String COLUMN_AUTHOR_BIO = "bio";
    public static final String COLUMN_AUTHOR_IMAGE_PATH = "image_path";
    
    // Country Table Columns
    public static final String COLUMN_COUNTRY_ID = "id";
    public static final String COLUMN_COUNTRY_NAME = "name";
    public static final String COLUMN_COUNTRY_FLAG_PATH = "flag_path";
    
    // Create Table Statements
    private static final String CREATE_TABLE_HISTOIRES = "CREATE TABLE " + TABLE_HISTOIRES + "("
            + COLUMN_HISTOIRE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_HISTOIRE_TITRE + " TEXT,"
            + COLUMN_HISTOIRE_CONTENU + " TEXT,"
            + COLUMN_HISTOIRE_AUTEUR + " TEXT,"
            + COLUMN_HISTOIRE_PAYS + " TEXT,"
            + COLUMN_HISTOIRE_DATE + " TEXT,"
            + COLUMN_HISTOIRE_IMAGE_PATH + " TEXT,"
            + COLUMN_HISTOIRE_CATEGORIE + " TEXT"
            + ")";
    
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CATEGORY_NAME + " TEXT,"
            + COLUMN_CATEGORY_IMAGE_PATH + " TEXT"
            + ")";
    
    private static final String CREATE_TABLE_AUTHORS = "CREATE TABLE " + TABLE_AUTHORS + "("
            + COLUMN_AUTHOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_AUTHOR_NAME + " TEXT,"
            + COLUMN_AUTHOR_DOB + " TEXT,"
            + COLUMN_AUTHOR_BIO + " TEXT,"
            + COLUMN_AUTHOR_IMAGE_PATH + " TEXT"
            + ")";
    
    private static final String CREATE_TABLE_COUNTRIES = "CREATE TABLE " + TABLE_COUNTRIES + "("
            + COLUMN_COUNTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_COUNTRY_NAME + " TEXT,"
            + COLUMN_COUNTRY_FLAG_PATH + " TEXT"
            + ")";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_HISTOIRES);
            db.execSQL(CREATE_TABLE_CATEGORIES);
            db.execSQL(CREATE_TABLE_AUTHORS);
            db.execSQL(CREATE_TABLE_COUNTRIES);
            Log.d(TAG, "Database tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database tables: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            
            if (oldVersion < 2) {
                // Add the category column to the histoires table
                try {
                    db.execSQL("ALTER TABLE " + TABLE_HISTOIRES + 
                              " ADD COLUMN " + COLUMN_HISTOIRE_CATEGORIE + " TEXT");
                    Log.d(TAG, "Added categorie column to histoires table");
                } catch (Exception e) {
                    Log.e(TAG, "Error adding categorie column: " + e.getMessage(), e);
                }
            }
            
            // For future migrations, add more conditions here
            // if (oldVersion < 3) { ... }
            
            Log.d(TAG, "Database upgrade completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage(), e);
        }
    }
    
    // Helper method to safely get column index
    public static int getColumnIndex(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex == -1) {
            Log.e(TAG, "Column not found: " + columnName);
            // Return a default value or throw an exception
            return 0; // Default to first column as fallback
        }
        return columnIndex;
    }
}
