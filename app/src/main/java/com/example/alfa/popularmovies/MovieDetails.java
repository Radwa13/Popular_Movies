package com.example.alfa.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alfa.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.example.alfa.popularmovies.model.Result;
import com.squareup.picasso.Picasso;

import static com.example.alfa.popularmovies.NetworkUtils.BASE_URL_POSTER;

public class MovieDetails extends AppCompatActivity {
   private TextView titleTv, overviewTv, ratingTv, dateTv;
    private ImageView posterIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initializeUI();
        Intent intent = getIntent();
        Result movie = intent.getParcelableExtra("movie");
        titleTv.setText(movie.getOriginalTitle());
        overviewTv.setText(movie.getOverview());
        ratingTv.setText(String.valueOf(movie.getVoteAverage()));
        dateTv.setText(movie.getReleaseDate());
        Picasso.with(this)
                .load(BASE_URL_POSTER + movie.getPosterPath())
                .into(posterIv);
    }

    private void initializeUI() {
        ActivityMovieDetailsBinding mBinding;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        titleTv = mBinding.titleTv;
        overviewTv = mBinding.overviewTv;
        ratingTv = mBinding.ratingTv;
        dateTv = mBinding.dateTv;
        posterIv=mBinding.posterIv;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
