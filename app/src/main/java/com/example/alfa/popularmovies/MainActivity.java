package com.example.alfa.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.alfa.popularmovies.data.FavouritesDbHelper;
import com.example.alfa.popularmovies.data.MoviesContract;
import com.example.alfa.popularmovies.databinding.ActivityMainBinding;
import com.example.alfa.popularmovies.model.MoviesList;
import com.example.alfa.popularmovies.model.Result;
import com.example.alfa.popularmovies.model.Review;
import com.example.alfa.popularmovies.model.ReviewAndVideos;
import com.example.alfa.popularmovies.model.Video;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.alfa.popularmovies.NetworkUtils.BASE_URL;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListner, SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private MoviesInterface mInterface;
    public static SharedPreferences sharedPreferences;
    private final String STATE_KEY = "key";
    private ArrayList<Result> mMoviesList;
    private static final int Movie_LOADER_ID = 1;
    private Bundle mBundle;
    private Result movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUi();
        setupSharedPreferences();

        AdroitGridLayout gridLayout=new AdroitGridLayout(this);
        mInterface = RetrofitClient.getClient().create(MoviesInterface.class);


        mRecyclerView.setLayoutManager(gridLayout);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_KEY)) {
            mMoviesList = savedInstanceState.getParcelableArrayList(STATE_KEY);
            mMoviesAdapter.loadData(mMoviesList);


        } else {
            loadMovies();
        }
    }


    private void initializeUi() {
        ActivityMainBinding mBinding;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mRecyclerView = mBinding.popularRv;

    }


    // private class MoviesAdaptr

    private void loadMovies() {
        //   Call<mMoviesList> call2 = null;
        String getType = sharedPreferences.getString(getString(R.string.pref_key), getString(R.string.pref_popular));
        if (getType.equals(getString(R.string.pref_popular))) {
            mInterface.getPopularMovies().observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .retry(3)
                    .subscribe(this::handleResponse, this::handleError);
        } else if (getType.equals(getString(R.string.pref_top))) {
            mInterface.getTopMovies().observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .retry(3)
                    .subscribe(this::handleResponse, this::handleError);

        } else if (getType.equals(getString(R.string.pref_favourite))) {
            loadFavouriteMovies();
        }
    }

    private void handleResponse(MoviesList list) {
        mMoviesList = list.getResults();
        mMoviesAdapter.loadData(mMoviesList);

    }



    private void handleError(Throwable error) {

        Toast.makeText(this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }



                /*

         retrofit.create(StoreCouponsApi.class).getCoupons("topcoupons")
        .subscribeOn(Schedulers.io())
        .retry(4)
        .timer(200, java.util.concurrent.TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::handleResults, this::handleError );

         */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(int position, List<Result> movies) {
        movie = movies.get(position);


        Intent intent = new Intent(MainActivity.this, MovieDetails.class);

        intent.putExtra("movie", movie);
        startActivity(intent);
    }



    private  void setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadMovies();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_KEY, mMoviesList);
    }

    ///getFavourites movies

    private void getFavouritesMovies() {
        SQLiteDatabase db;
        FavouritesDbHelper favouritesDbHelper = new FavouritesDbHelper(this);
        db = favouritesDbHelper.getReadableDatabase();
        Cursor cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME
                , null, null, null, null, null, MoviesContract.MovieEntry.COLUMN_NAME_TITLE);

        while (cursor.moveToNext()) {
            Result result = new Result();
            result.setId(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_ID)));
            result.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_TITLE)));
            result.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_OVERVIEW)));
            result.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_RELEASE_DATE)));
            Parcel parcel = Parcel.obtain();
            parcel.writeValue(result);
            parcel.recycle();
            mMoviesList.add(result);

        }
        cursor.close();
        db.close();
        mMoviesAdapter.loadData(mMoviesList);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        return new AsyncTaskLoader<Cursor>(MainActivity.this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    mBundle = bundle;

                    return getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, null, null, null, MoviesContract.MovieEntry.COLUMN_NAME_ID);
                }
                /*

                }
                 */ catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable Cursor data) {
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mMoviesList= new ArrayList<>();

        while (cursor.moveToNext()) {
            Result result = new Result();

            result.setId(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_ID)));
            result.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_TITLE)));
            result.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_POSTER_PATH)));
            result.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_OVERVIEW)));
            result.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));
            result.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_RELEASE_DATE)));
            Parcel parcel = Parcel.obtain();
            parcel.writeValue(result);
            parcel.recycle();
            mMoviesList.add(result);
        }
        mMoviesAdapter.loadData(mMoviesList);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void loadFavouriteMovies() {
        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           Loader loader = loaderManager.getLoader(Movie_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(Movie_LOADER_ID, mBundle, MainActivity.this);
        } else {
            loaderManager.restartLoader(Movie_LOADER_ID, mBundle, MainActivity.this);
        }

    }




}
