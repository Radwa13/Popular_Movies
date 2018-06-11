package com.example.alfa.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alfa.popularmovies.databinding.ListItemsBinding;
import com.example.alfa.popularmovies.databinding.TrailersItemsBinding;
import com.example.alfa.popularmovies.model.Result;
import com.example.alfa.popularmovies.model.Video;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static com.example.alfa.popularmovies.NetworkUtils.BASE_URL_POSTER;
import static com.example.alfa.popularmovies.NetworkUtils.YOUTUBE_THUMBNAIL;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder>  {
    private Context mContext;
    private List<Video> mTrailersList;
    final private ListItemClickListner mClickHandler;

    public interface ListItemClickListner {
        void onClick(int position);
    }

    public TrailersAdapter(Context context, ListItemClickListner clickHandler,List<Video> trailersList) {
        mContext = context;
        mClickHandler = clickHandler;
        mTrailersList=trailersList;
    }

    @NonNull
    @Override
    public TrailersAdapter.TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int listLayoutId = R.layout.trailers_items;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(listLayoutId, parent, false);
        return new TrailersAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersAdapter.TrailerViewHolder holder, int position) {

            Picasso.with(mContext)
                    .load(YOUTUBE_THUMBNAIL + mTrailersList.get(position).getKey()+"/0.jpg")
                    .into(holder.mImageView);
            holder.mTextView.setText(mTrailersList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        if (mTrailersList == null) {
            return 0;
        } else {
            return mTrailersList.size();
        }
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mImageView;
        public final TextView mTextView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            TrailersItemsBinding listItemsBinding = DataBindingUtil.bind(itemView);
            mImageView = listItemsBinding.moviePosterIV;
            mTextView=listItemsBinding.trailer;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int postion = getAdapterPosition();
            mClickHandler.onClick(postion);
        }
    }

}

