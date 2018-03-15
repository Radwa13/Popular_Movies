package com.example.alfa.popularmovies;

import com.example.alfa.popularmovies.model.MoviesList;

import retrofit2.Call;
import retrofit2.http.GET;

@SuppressWarnings("DefaultFileTemplate")
interface MoviesInterface
{
    @GET("/3/movie/popular?page=1&language=en-US&api_key="+BuildConfig.API_KEY)
    Call<MoviesList> getPopularMovies();

    @GET("3/movie/top_rated?api_key="+BuildConfig.API_KEY)
    Call<MoviesList> getTopMovies();

}
