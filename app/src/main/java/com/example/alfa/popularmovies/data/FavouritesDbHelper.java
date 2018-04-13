package com.example.alfa.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import  com.example.alfa.popularmovies.data.MoviesContract.MovieEntry;

import static com.example.alfa.popularmovies.data.MoviesContract.MovieEntry.TABLE_NAME;

/**
 * Created by Alfa on 3/25/2018.
 */

public class FavouritesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public FavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITES_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                MovieEntry.COLUMN_NAME_ID + " INTEGER NOT NULL," +
                MovieEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT NOT NULL," +
                MovieEntry.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL,"
                +MovieEntry.COLUMN_NAME_VOTE_AVERAGE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_NAME_RELEASE_DATE + "  TEXT NOT NULL" + "); ";
        db.execSQL(SQL_CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }
}
