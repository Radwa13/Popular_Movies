package com.example.alfa.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class MoviesList {

    @SerializedName("results")
    @Expose
    private final ArrayList<Result> results = null;

    public ArrayList<Result> getResults() {
        return results;
    }

}