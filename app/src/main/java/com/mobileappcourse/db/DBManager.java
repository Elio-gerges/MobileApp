package com.mobileappcourse.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.mobileappcourse.beans.Movie;
import com.mobileappcourse.db.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBManager {

    // Manage User database
    // Singleton

    private static SQLite db = null;
    private final Context context;

    private static DBManager self;

    private DBManager(Context context) {
        this.context = context;
    }

    public static DBManager getInstance(@Nullable Context context) {
        if(self == null) {
            self = new DBManager(context);
            db = SQLite.getInstance(context);
        }

        return self;
    }

    public void manageMovie(Movie movie) throws DBException, ParseException {

        // Insert user to db using user class and returns id
        if(getMovie(movie.getId()) == -1) {
            SQLiteDatabase sqldb = db.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(SQLite.Movie_COLUMN_ID, movie.getId());

            long newRowID = sqldb.insert(SQLite.TABLE_Movie_NAME, null, values);
        } else {
            // Drop News by title, author, and url
            SQLiteDatabase sqldb = db.getWritableDatabase();

            sqldb.delete(SQLite.TABLE_Movie_NAME,
                    SQLite.Movie_COLUMN_ID + "=?",
                    new String[]{movie.getId()}
            );
        }
    }

    public int getMovie(String id) {
        SQLiteDatabase sqldb = db.getReadableDatabase();

        String[] projection = {SQLite.Movie_COLUMN_ID};

        String selection = SQLite.Movie_COLUMN_ID + " like ?;";

        String[] selectionArgs = {id};

        Cursor cursor = sqldb.query(SQLite.TABLE_Movie_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        int found = -1;

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                found = cursor.getColumnIndex(SQLite.Movie_COLUMN_ID);
            }
        }

        return found;
    }

    public ArrayList<Movie> getMovies() {
        SQLiteDatabase sqldb = db.getReadableDatabase();
        Cursor cursor = sqldb.rawQuery("SELECT * FROM " + SQLite.TABLE_Movie_NAME + ";", null);

        ArrayList<Movie> movies = new ArrayList<Movie>();

        if(cursor.moveToFirst()) {
            do {
                // Passing values
                int colIndex = cursor.getColumnIndex(SQLite.Movie_COLUMN_ID);
                String id = cursor.getString(colIndex);
                Movie movie = new Movie();
                movie.setId(id);
                movies.add(movie);
            } while(cursor.moveToNext());
        }

        cursor.close();

        return movies;
    }

}
