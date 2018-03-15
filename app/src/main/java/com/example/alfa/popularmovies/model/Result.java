package com.example.alfa.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Result implements Parcelable {


    @SerializedName("original_title")
    @Expose
    private final String originalTitle ;

    @SerializedName("poster_path")
    @Expose
    private final String posterPath ;

    @SerializedName("overview")
    @Expose
    private final String overview ;
    @SerializedName("vote_average")
    @Expose
    private final Double voteAverage ;

    @SerializedName("release_date")
    @Expose
    private final String releaseDate ;


    private Result(Parcel in) {
        originalTitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
    }

    @SuppressWarnings("unused")
    //must be included for parcel even no direct use
    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    public Double getVoteAverage() {
        return voteAverage;
    }


    public String getPosterPath() {
        return posterPath;
    }


    public String getOriginalTitle() {
        return originalTitle;
    }


    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeDouble(voteAverage);
        parcel.writeString(releaseDate);
    }
}
