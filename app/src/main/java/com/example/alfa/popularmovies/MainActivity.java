package com.example.alfa.popularmovies;

import android.content.Context;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.example.alfa.popularmovies.data.FavouritesDbHelper;
import com.example.alfa.popularmovies.data.MoviesContract;
import com.example.alfa.popularmovies.databinding.ActivityMainBinding;
import com.example.alfa.popularmovies.model.MoviesList;
import com.example.alfa.popularmovies.model.Result;
import com.example.alfa.popularmovies.model.Review;
import com.example.alfa.popularmovies.model.ReviewAndVideos;
import com.example.alfa.popularmovies.model.ReviewList;
import com.example.alfa.popularmovies.model.Video;
import com.example.alfa.popularmovies.model.VideoList;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

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
    private ArrayList<Review> mReviewsList;
    private ArrayList<Video> mVideoList;
    private static final int Movie_LOADER_ID = 1;
    Bundle mBundle;
    Result movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Retrofit retrofit;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUi();
        setupSharedPreferences();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                this, 2);

        mInterface = retrofit.create(MoviesInterface.class);


        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_KEY)) {
            mMoviesList = savedInstanceState.getParcelableArrayList(STATE_KEY);
            mMoviesAdapter.loadData(mMoviesList);


        } else {
            loadMovies();
        }
        //  loadWeatherData();
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
                    .subscribe(this::handleResponse, this::handleError);
        } else if (getType.equals(getString(R.string.pref_top))) {
            mInterface.getTopMovies().observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse, this::handleError);
        } else if (getType.equals(getString(R.string.pref_favourite))) {
            loadWeatherData();
        }
    }

    private void handleResponse(MoviesList list) {
        mMoviesList = list.getResults();
        mMoviesAdapter.loadData(mMoviesList);

    }


    private void handleResponses(ReviewAndVideos reviewAndVideos) {

        Intent intent = new Intent(MainActivity.this, MovieDetails.class);

        intent.putExtra("movie", movie);

        intent.putExtra("videos", reviewAndVideos.getVideoList());
        intent.putExtra("reviews", reviewAndVideos.getReviewList());
        startActivity(intent);
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

        loadReviewAndTrailer(movie.getId());

    }

    private void loadReviewAndTrailer(int id) {


        Observable.zip(mInterface.getReview(id), mInterface.getTrailer(id), new BiFunction<ReviewList, VideoList,ReviewAndVideos>() {
                    @Override
                    public ReviewAndVideos apply(ReviewList reviewList, VideoList videoList) throws Exception {
                        ReviewAndVideos reviewAndVideos=new ReviewAndVideos();
//                        mReviewsList = reviewList.getResults();
//                        mVideoList = videoList.getResults();
                        reviewAndVideos.setReviewList(reviewList);
                        reviewAndVideos.setVideoList(videoList);
                        return  reviewAndVideos;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponses, this::handleError);



//            @Override
//            public VideoList apply(ReviewList s, VideoList s2) throws Exception {
//                mReviewsList = s.getResults();
//                mVideoList = s2.getResults();
//                return mVideoList;
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponse3, this::handleError);
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

                    Cursor c = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, null, null, null, MoviesContract.MovieEntry.COLUMN_NAME_ID);
                    return c;
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
        Result result = new Result();
        mMoviesList=new ArrayList<Result>();

        while (cursor.moveToNext()) {
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

    private void loadWeatherData() {
        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           Loader loader = loaderManager.getLoader(Movie_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(Movie_LOADER_ID, mBundle, MainActivity.this);
        } else {
            loaderManager.restartLoader(Movie_LOADER_ID, mBundle, MainActivity.this);
        }

    }




}
