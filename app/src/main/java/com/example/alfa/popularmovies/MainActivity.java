package com.example.alfa.popularmovies;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.alfa.popularmovies.databinding.ActivityMainBinding;
import com.example.alfa.popularmovies.model.Result;
import com.example.alfa.popularmovies.model.MoviesList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.alfa.popularmovies.NetworkUtils.BASE_URL;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListner, SharedPreferences.OnSharedPreferenceChangeListener {
    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private MoviesInterface mInterface;
    private SharedPreferences sharedPreferences;
    private final String STATE_KEY = "key";
    private ArrayList<Result> moviesList;

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
                .build();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                this, 2);

        mInterface = retrofit.create(MoviesInterface.class);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_KEY)) {
            moviesList = savedInstanceState.getParcelableArrayList(STATE_KEY);
            mMoviesAdapter.loadData(moviesList);


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
        Call<MoviesList> call2 = null;
        String getType = sharedPreferences.getString(getString(R.string.pref_key), getString(R.string.pref_popular));
        if (getType.equals(getString(R.string.pref_popular)))
            call2 = mInterface.getPopularMovies();
        else if (getType.equals(getString(R.string.pref_top)))
            call2 = mInterface.getTopMovies();

        if (call2 != null) {
            final ProgressDialog progressDialog;
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.progress_message));
            progressDialog.setMax(3);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            call2.enqueue(new Callback<MoviesList>() {
                @Override
                public void onResponse(@NonNull Call<MoviesList> call, @NonNull Response<MoviesList> response) {
                    //noinspection ConstantConditions
                    moviesList = response.body().getResults();
                    mMoviesAdapter.loadData(moviesList);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<MoviesList> call, @NonNull Throwable t) {
                    progressDialog.dismiss();


                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("There was a problem in connection")
                            .setCancelable(false)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    loadMovies();

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    builder.show();
                }

            });
        }

    }

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
        Intent intent = new Intent(MainActivity.this, MovieDetails.class);
        Result movie = movies.get(position);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }


    private void setupSharedPreferences() {
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
        outState.putParcelableArrayList(STATE_KEY, moviesList);
    }

}
