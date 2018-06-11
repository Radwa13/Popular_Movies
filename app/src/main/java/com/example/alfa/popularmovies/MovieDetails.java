package com.example.alfa.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alfa.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.example.alfa.popularmovies.databinding.ReviewItemBinding;
import com.example.alfa.popularmovies.databinding.TrailersItemsBinding;
import com.example.alfa.popularmovies.model.Result;
import com.example.alfa.popularmovies.model.ReviewAndVideos;
import com.example.alfa.popularmovies.model.ReviewList;
import com.example.alfa.popularmovies.model.Video;
import com.example.alfa.popularmovies.model.VideoList;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.alfa.popularmovies.NetworkUtils.BASE_URL;
import static com.example.alfa.popularmovies.NetworkUtils.BASE_URL_POSTER;
import static com.example.alfa.popularmovies.data.MoviesContract.MovieEntry;
import static com.example.alfa.popularmovies.data.MoviesContract.MovieEntry.COLUMN_NAME_ID;
import static com.example.alfa.popularmovies.data.MoviesContract.MovieEntry.CONTENT_URI;


public class MovieDetails extends AppCompatActivity implements TrailersAdapter.ListItemClickListner{
    private TextView titleTv, overviewTv, ratingTv, dateTv;
    private ImageView posterIv, favoriteIv;
    private boolean isExist;
    private ListView mReviewsListView;
    private RecyclerView mTrailersListView;
    private Reviews reviewsAdapter;
    private ReviewList reviewList;
    private VideoList videoList;
    private MoviesInterface mInterface;
    private final String REVIEW_KEY = "rKey";
    private final String VIDEO_KEY = "vKey";
    TextView trailers, reviews;
    TrailersAdapter trailersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initializeUI();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailersListView.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        Result movie = intent.getParcelableExtra("movie");


        if (savedInstanceState != null && savedInstanceState.containsKey(REVIEW_KEY) && savedInstanceState.containsKey(VIDEO_KEY)) {
            videoList = savedInstanceState.getParcelable(VIDEO_KEY);
            reviewList = savedInstanceState.getParcelable(REVIEW_KEY);
            reviewsAdapter = new Reviews(this, reviewList);


            trailersAdapter  = new TrailersAdapter(this, this,videoList.getResults());
            mTrailersListView.setAdapter(trailersAdapter);
            mReviewsListView.setAdapter(reviewsAdapter);


        } else {
            loadReviewAndTrailer(movie.getId());
        }
        isExist = isExist(movie);
        if (isExist) {
            favoriteIv.setBackgroundResource(R.drawable.star);

        }
        titleTv.setText(movie.getOriginalTitle());
        overviewTv.setText(movie.getOverview());
        ratingTv.setText(String.valueOf(movie.getVoteAverage()));
        dateTv.setText(movie.getReleaseDate());
        String getType = MainActivity.sharedPreferences.getString(getString(R.string.pref_key), getString(R.string.pref_popular));
        if (getType.equals(getString(R.string.pref_favourite))) {
            mReviewsListView.setVisibility(View.GONE);
            mTrailersListView.setVisibility(View.GONE);
            trailers.setVisibility(View.GONE);
            reviews.setVisibility(View.GONE);

            File f = new File(movie.getPosterPath());
            Bitmap b = null;
            try {
                b = BitmapFactory.decodeStream(new FileInputStream(f));
                posterIv.setImageBitmap(b);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Picasso.with(this)
                    .load(BASE_URL_POSTER + movie.getPosterPath())
                    .into(posterIv);
        }
        favoriteIv.setOnClickListener(v -> {
            if (!isExist(movie)) {
                addToFavourite(movie);
                favoriteIv.setBackgroundResource(R.drawable.star);

            } else {
                favoriteIv.setBackgroundResource(R.drawable.unstar);

                removeFromFavourites(movie.getId());
                Toast.makeText(MovieDetails.this, "movie removed from favourites", Toast.LENGTH_LONG).show();
            }

        });


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
        trailers = mBinding.textView4;
        reviews = mBinding.textView6;
    }


    private void loadReviewAndTrailer(int id) {
        mInterface = RetrofitClient.getClient().create(MoviesInterface.class);


        Observable.zip(mInterface.getReview(id), mInterface.getTrailer(id), (reviewList, videoList) -> {
            ReviewAndVideos reviewAndVideos = new ReviewAndVideos();
//                        mReviewsList = reviewList.getResults();
//                        mVideoList = videoList.getResults();
            reviewAndVideos.setReviewList(reviewList);
            reviewAndVideos.setVideoList(videoList);
            return reviewAndVideos;
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponses, this::handleError);
    }


    private void handleResponses(ReviewAndVideos reviewAndVideos) {
        reviewList = reviewAndVideos.getReviewList();
        videoList = reviewAndVideos.getVideoList();
        Reviews adapter = new Reviews(this, reviewList);
        mReviewsListView.setAdapter(adapter);
         trailersAdapter = new TrailersAdapter(this, this,videoList.getResults());
        mTrailersListView.setAdapter(trailersAdapter);

    }

    private void handleError(Throwable error) {

        Toast.makeText(this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath() + name;
    }

    @Override
    public void onClick(int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoList.getResults().get(position).getKey()));
       startActivity(intent);
    }


    private static class TrailersViewHolder {
        private View view;

        private TrailersItemsBinding binding;

        TrailersViewHolder(TrailersItemsBinding binding) {
            this.view = binding.getRoot();
            this.binding = binding;
        }
    }




    private static class ReviewsViewHolder {
        private View view;

        private ReviewItemBinding binding;

        ReviewsViewHolder(ReviewItemBinding binding) {
            this.view = binding.getRoot();
            this.binding = binding;
        }
    }


    public class Reviews extends BaseAdapter {
        private ReviewList mDataSource;

        private Reviews(Context context, ReviewList items) {
            mDataSource = items;
        }

        @Override
        public int getCount() {
            if (mDataSource != null) {
                return mDataSource.getResults().size();
            } else return 0;
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

            ReviewsViewHolder reviewsViewHolder;
            if (convertView == null) {
                ReviewItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.review_item, parent, false);

                reviewsViewHolder = new ReviewsViewHolder(itemBinding);
                reviewsViewHolder.view = itemBinding.getRoot();
                reviewsViewHolder.view.setTag(reviewsViewHolder);
            } else {
                reviewsViewHolder = (ReviewsViewHolder) convertView.getTag();
            }
            reviewsViewHolder.binding.name.setText(mDataSource.getResults().get(position).getAuthor());
            reviewsViewHolder.binding.review.setText(mDataSource.getResults().get(position).getContent());


            return reviewsViewHolder.view;
        }

        protected void loadData(ReviewList items) {
            mDataSource = items;
            notifyDataSetChanged();
        }
    }

    private boolean isExist(Result movie) {
        Cursor c = getContentResolver().query(CONTENT_URI, null, COLUMN_NAME_ID + " = " + movie.getId(), null, null);
        if (c != null) {
            if (c.getCount() == 0) {
                return false;
            }
        }
        if (c != null) {
            c.close();
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(REVIEW_KEY, reviewList);
        outState.putParcelable(VIDEO_KEY, videoList);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
