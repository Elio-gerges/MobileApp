package com.mobileappcourse.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLite extends SQLiteOpenHelper {

    private static SQLite self = null;
    private static Context mContext = null;

    // DB information
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MovieAppDB";

    // Movies table information
    public static final String TABLE_Movie_NAME = "movies";
    public static final String Movie_COLUMN_ID = "id";

    public SQLite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static SQLite getInstance(Context context) {
        if (self == null || context != mContext || mContext == null) {
            self = new SQLite(context);
        }

        return self;
    }

    private final String CREATE_Movie_Table =
            "CREATE TABLE " + TABLE_Movie_NAME + " (" +
                    Movie_COLUMN_ID + " TEXT UNIQUE PRIMARY KEY);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Table Movie
        db.execSQL(CREATE_Movie_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Movie_NAME);
        onCreate(db);
    }
}
