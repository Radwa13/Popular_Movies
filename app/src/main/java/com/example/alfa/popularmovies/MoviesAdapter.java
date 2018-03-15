package com.example.alfa.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.alfa.popularmovies.databinding.ListItemsBinding;
import com.example.alfa.popularmovies.model.Result;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        Picasso.with(mContext)
                .load(BASE_URL_POSTER + mMoviesList.get(position).getPosterPath())
                .into(holder.mImageView);

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
            mClickHandler.onClick(postion,mMoviesList);
        }
    }

    public void loadData(List<Result> moviesList) {
        mMoviesList = moviesList;
        notifyDataSetChanged();
    }


}
