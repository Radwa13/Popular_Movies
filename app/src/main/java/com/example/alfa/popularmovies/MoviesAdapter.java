package com.example.alfa.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.alfa.popularmovies.databinding.ListItemsBinding;
import com.example.alfa.popularmovies.model.Result;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.alfa.popularmovies.NetworkUtils.BASE_URL_POSTER;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private Context mContext;
    private List<Result> mMoviesList;
    final private ListItemClickListner mClickHandler;

    public interface ListItemClickListner {
        void onClick(int position, List<Result> movies);
    }

    public MoviesAdapter(Context context, ListItemClickListner clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int listLayoutId = R.layout.list_items;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(listLayoutId, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
//

        String getType = MainActivity.sharedPreferences.getString(mContext.getString(R.string.pref_key), mContext.getString(R.string.pref_popular));
        if (getType.equals(mContext.getString(R.string.pref_favourite))) {

            File f = new File( mMoviesList.get(position).getPosterPath());
            Bitmap b = null;
            try {
                b = BitmapFactory.decodeStream(new FileInputStream(f));
                holder.mImageView.setImageBitmap(b);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Picasso.with(mContext)
                .load(BASE_URL_POSTER + mMoviesList.get(position).getPosterPath())
                .into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mMoviesList == null) {
            return 0;
        } else {
            return mMoviesList.size();
        }
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mImageView;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            ListItemsBinding listItemsBinding = DataBindingUtil.bind(itemView);
            mImageView = listItemsBinding.moviePosterIV;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int postion = getAdapterPosition();
            mClickHandler.onClick(postion, mMoviesList);
        }
    }

    public void loadData(List<Result> moviesList) {
        mMoviesList = moviesList;
        notifyDataSetChanged();
    }


}
