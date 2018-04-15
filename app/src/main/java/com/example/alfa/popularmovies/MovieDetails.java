package com.example.alfa.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alfa.popularmovies.data.FavouritesDbHelper;
import com.example.alfa.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.example.alfa.popularmovies.databinding.ReviewItemBinding;
import com.example.alfa.popularmovies.databinding.TrailersItemsBinding;
import com.example.alfa.popularmovies.model.Result;
import com.example.alfa.popularmovies.model.ReviewList;
import com.example.alfa.popularmovies.model.VideoList;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.alfa.popularmovies.NetworkUtils.BASE_URL_POSTER;
import static com.example.alfa.popularmovies.data.MoviesContract.MovieEntry;
import static com.example.alfa.popularmovies.data.MoviesContract.MovieEntry.COLUMN_NAME_ID;
import static com.example.alfa.popularmovies.data.MoviesContract.MovieEntry.CONTENT_URI;


public class MovieDetails extends AppCompatActivity {
    private TextView titleTv, overviewTv, ratingTv, dateTv;
    private ImageView posterIv, favoriteIv;
    private ListView mTrailersListView, mReviewsListView;
boolean isExist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initializeUI();
        Intent intent = getIntent();
        Result movie = intent.getParcelableExtra("movie");
        ReviewList reviewList = intent.getParcelableExtra("reviews");
        VideoList videoList = intent.getParcelableExtra("videos");
        isExist=isExist(movie);
        if(isExist){
            favoriteIv.setBackgroundResource(R.drawable.star);

        }
        titleTv.setText(movie.getOriginalTitle());
        overviewTv.setText(movie.getOverview());
        ratingTv.setText(String.valueOf(movie.getVoteAverage()));
        dateTv.setText(movie.getReleaseDate());
        String getType = MainActivity.sharedPreferences.getString(getString(R.string.pref_key), getString(R.string.pref_popular));
        if (getType.equals(getString(R.string.pref_favourite))) {

            File f = new File( movie.getPosterPath());
            Bitmap b = null;
            try {
                b = BitmapFactory.decodeStream(new FileInputStream(f));
                posterIv.setImageBitmap(b);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
        Picasso.with(this)
                .load(BASE_URL_POSTER + movie.getPosterPath())
                .into(posterIv);}
        favoriteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExist(movie)) {
                    addToFavourite(movie);
                    favoriteIv.setBackgroundResource(R.drawable.star);

                } else {
                    favoriteIv.setBackgroundResource(R.drawable.unstar);

                    removeFromFavourites(movie.getId());
                    Toast.makeText(MovieDetails.this, "movie removed from favourites", Toast.LENGTH_LONG).show();
                }

            }
        });
//        Reviews adapter = new Reviews(this, reviewList);
//        mReviewsListView.setAdapter(adapter);
//        Trailers adapters = new Trailers(this, videoList);
//        mTrailersListView.setAdapter(adapters);
    }

    private void initializeUI() {
        ActivityMovieDetailsBinding mBinding;

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        titleTv = mBinding.titleTv;
        overviewTv = mBinding.overviewTv;
        ratingTv = mBinding.ratingTv;
        dateTv = mBinding.dateTv;
        posterIv = mBinding.posterIv;
        favoriteIv = mBinding.favouriteIv;
        mReviewsListView = mBinding.rev;
        mTrailersListView = mBinding.vid;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToFavourite(Result movie) {
        Bitmap poster = ((BitmapDrawable) posterIv.getDrawable()).getBitmap();


        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_ID, movie.getId());
        cv.put(MovieEntry.COLUMN_NAME_TITLE, movie.getOriginalTitle());
        cv.put(MovieEntry.COLUMN_NAME_POSTER_PATH, saveToInternalStorage(poster, movie.getPosterPath()));
        cv.put(MovieEntry.COLUMN_NAME_OVERVIEW, movie.getOverview());
        cv.put(MovieEntry.COLUMN_NAME_VOTE_AVERAGE, movie.getVoteAverage());
        cv.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.getId());

        Uri uri = getContentResolver().insert(CONTENT_URI, cv);

        if (uri != null) {
            Toast.makeText(this, "movie added to favourites", Toast.LENGTH_LONG).show();
        }
    }

    private void removeFromFavourites(int id) {

        String stringId = Integer.toString(id);
        Uri uri = CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        getContentResolver().delete(uri, COLUMN_NAME_ID, new String[]{String.valueOf(id)});


    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath() + name;
    }

    public class Trailers extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private VideoList mDataSource;

        public Trailers(Context context, VideoList items) {
            mContext = context;
            mDataSource = items;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mDataSource.getResults().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = mInflater.inflate(R.layout.trailers_items, parent, false);
            TrailersItemsBinding mBinding;

            mBinding = DataBindingUtil.setContentView(MovieDetails.this, R.layout.trailers_items);
            TextView tv = mBinding.trailer;
            tv.setText("Trailer " + position);
            return rowView;
        }
    }

    public class Reviews extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private ReviewList mDataSource;

        public Reviews(Context context, ReviewList items) {
            mContext = context;
            mDataSource = items;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mDataSource.getResults().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = mInflater.inflate(R.layout.review_item, parent, false);
            ReviewItemBinding mBinding;
            mBinding = DataBindingUtil.setContentView(MovieDetails.this, R.layout.review_item);
            TextView nameTv = mBinding.name;
            TextView reviewTv = mBinding.review;
            nameTv.setText(mDataSource.getResults().get(position).getAuthor());
            reviewTv.setText(mDataSource.getResults().get(position).getContent());
            return rowView;
        }
    }

    private boolean isExist(Result movie) {
        Cursor c = getContentResolver().query(CONTENT_URI, null, COLUMN_NAME_ID + " = " + movie.getId(), null, null);
        if (c.getCount() == 0) {
            return false;
        } else return true;
    }
}
