package com.example.alfa.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {
    protected static final String AUTHORITY="com.example.alfa.popularmovies";
    protected static final Uri BASE_CONTENT_URI=Uri.parse("content://"+AUTHORITY);
    protected static final String PATH="favourites";

    public static final class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH).build();
        public static final String TABLE_NAME="favourites";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_NAME_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_NAME_POSTER_PATH = "posterPath";

    }
}
