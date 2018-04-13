package com.example.alfa.popularmovies;

import com.example.alfa.popularmovies.model.MoviesList;
import com.example.alfa.popularmovies.model.ReviewList;
import com.example.alfa.popularmovies.model.VideoList;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

@SuppressWarnings("DefaultFileTemplate")
interface MoviesInterface {
    @GET("/3/movie/popular?page=1&language=en-US&api_key=" + BuildConfig.API_KEY)
    Observable<MoviesList> getPopularMovies();


    @GET("3/movie/top_rated?api_key=" + BuildConfig.API_KEY)
    Observable<MoviesList> getTopMovies();

    @GET("3/movie/{id}/videos?api_key=" + BuildConfig.API_KEY)
    Observable<VideoList> getTrailer(@Path("id") int id);

    @GET("3/movie/{id}/reviews?api_key=" + BuildConfig.API_KEY)
    Observable<ReviewList> getReview(@Path("id") int id);
}


