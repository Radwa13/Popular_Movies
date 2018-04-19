package com.example.alfa.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.alfa.popularmovies.data.MoviesContract.AUTHORITY;
import static com.example.alfa.popularmovies.data.MoviesContract.MovieEntry.COLUMN_NAME_ID;
import static com.example.alfa.popularmovies.data.MoviesContract.MovieEntry.TABLE_NAME;
import static com.example.alfa.popularmovies.data.MoviesContract.PATH;


public class MoviesContentProvider extends ContentProvider {
    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;
    public static final UriMatcher sUriMatcher = buildUriMatcher();

    FavouritesDbHelper mFavouritesDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavouritesDbHelper = new FavouritesDbHelper(context);

        return true;
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher= new UriMatcher(UriMatcher.NO_MATCH);
            uriMatcher.addURI(AUTHORITY, PATH, MOVIES);
        uriMatcher.addURI(AUTHORITY, PATH + "/#", MOVIE_WITH_ID);
        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mFavouritesDbHelper.getReadableDatabase();
        int val = sUriMatcher.match(uri);
        Cursor returnCursor;
        switch (val) {
            case MOVIES:
                returnCursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
      final   SQLiteDatabase db = mFavouritesDbHelper.getWritableDatabase();
        int val = sUriMatcher.match(uri);
        Uri returnUri;
        switch (val) {
            case MOVIES:
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {

                    returnUri = ContentUris.withAppendedId(uri, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavouritesDbHelper.getWritableDatabase();
        int val = sUriMatcher.match(uri);
        int tasksDeleted ;
        switch (val) {
            case MOVIE_WITH_ID:

                String id = uri.getPathSegments().get(1);
                 tasksDeleted = db.delete(TABLE_NAME, COLUMN_NAME_ID+"= ?;", new String[]{id});

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if(tasksDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return tasksDeleted;

    }
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
