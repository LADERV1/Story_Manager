package ma.example.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AuthorDatabase {
    private static final String TAG = "AuthorDatabase";

    private DatabaseHelper dbHelper;

    public AuthorDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Add a new author
    public long addAuthor(Author author) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_AUTHOR_NAME, author.getName());
            values.put(DatabaseHelper.COLUMN_AUTHOR_DOB, author.getDob());
            values.put(DatabaseHelper.COLUMN_AUTHOR_BIO, author.getBio());
            values.put(DatabaseHelper.COLUMN_AUTHOR_IMAGE_PATH, author.getImagePath());

            long id = db.insert(DatabaseHelper.TABLE_AUTHORS, null, values);
            db.close();

            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error adding author: " + e.getMessage(), e);
            return -1;
        }
    }

    // Get a single author by ID
    public Author getAuthor(int id) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(
                DatabaseHelper.TABLE_AUTHORS,
                new String[] {
                    DatabaseHelper.COLUMN_AUTHOR_ID,
                    DatabaseHelper.COLUMN_AUTHOR_NAME,
                    DatabaseHelper.COLUMN_AUTHOR_DOB,
                    DatabaseHelper.COLUMN_AUTHOR_BIO,
                    DatabaseHelper.COLUMN_AUTHOR_IMAGE_PATH
                },
                DatabaseHelper.COLUMN_AUTHOR_ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null
            );

            Author author = null;

            if (cursor != null && cursor.moveToFirst()) {
                author = new Author();
                author.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR_ID)));
                author.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR_NAME)));
                author.setDob(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR_DOB)));
                author.setBio(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR_BIO)));
                author.setImagePath(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR_IMAGE_PATH)));

                cursor.close();
            }

            db.close();
            return author;
        } catch (Exception e) {
            Log.e(TAG, "Error getting author: " + e.getMessage(), e);
            return null;
        }
    }

    // Get all authors
    public List<Author> getAllAuthors() {
        List<Author> authorList = new ArrayList<>();

        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_AUTHORS;

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Author author = new Author();
                    author.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR_ID)));
                    author.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR_NAME)));
                    author.setDob(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR_DOB)));
                    author.setBio(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR_BIO)));
                    author.setImagePath(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR_IMAGE_PATH)));

                    authorList.add(author);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all authors: " + e.getMessage(), e);
        }

        return authorList;
    }

    // Update an author
    public boolean updateAuthor(Author author) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_AUTHOR_NAME, author.getName());
            values.put(DatabaseHelper.COLUMN_AUTHOR_DOB, author.getDob());
            values.put(DatabaseHelper.COLUMN_AUTHOR_BIO, author.getBio());
            values.put(DatabaseHelper.COLUMN_AUTHOR_IMAGE_PATH, author.getImagePath());

            int rowsAffected = db.update(
                DatabaseHelper.TABLE_AUTHORS,
                values,
                DatabaseHelper.COLUMN_AUTHOR_ID + "=?",
                new String[] { String.valueOf(author.getId()) }
            );

            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating author: " + e.getMessage(), e);
            return false;
        }
    }

    // Delete an author
    public boolean deleteAuthor(int id) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            int rowsAffected = db.delete(
                DatabaseHelper.TABLE_AUTHORS,
                DatabaseHelper.COLUMN_AUTHOR_ID + "=?",
                new String[] { String.valueOf(id) }
            );

            db.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting author: " + e.getMessage(), e);
            return false;
        }
    }
}
