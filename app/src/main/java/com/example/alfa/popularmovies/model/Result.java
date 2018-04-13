package com.example.alfa.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Result implements Parcelable {


    @SerializedName("original_title")
    @Expose
    private  String originalTitle;

    @SerializedName("poster_path")
    @Expose
    private  String posterPath;

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @SerializedName("overview")
    @Expose

    private  String overview;
    @SerializedName("vote_average")
    @Expose
    private  Double voteAverage;

    @SerializedName("release_date")
    @Expose
    private  String releaseDate;
    @SerializedName("id")
    @Expose
    private Integer id;


    public Result()  {
        super();
    }
    public Result(Parcel in) {
        id = in.readInt();
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


    public Integer getId() {
        return id;
    }

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
        parcel.writeInt(id);
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeDouble(voteAverage);
        parcel.writeString(releaseDate);
    }
}
