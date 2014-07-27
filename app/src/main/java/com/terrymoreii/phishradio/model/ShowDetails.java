package com.terrymoreii.phishradio.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tmoore on 7/19/14.
 */
public class ShowDetails implements Serializable {

    private int showId;
    private String date;
    private int duration;
    private boolean incomplete;
    private boolean soundBoard;
    private boolean remastered;
    private int tourId;
    private Venue venue;
    private List tracks;


    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isIncomplete() {
        return incomplete;
    }

    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }

    public boolean isSoundBoard() {
        return soundBoard;
    }

    public void setSoundBoard(boolean soundBoard) {
        this.soundBoard = soundBoard;
    }

    public boolean isRemastered() {
        return remastered;
    }

    public void setRemastered(boolean remastered) {
        this.remastered = remastered;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public List getTracks() {
        return tracks;
    }

    public void setTracks(List tracks) {
        this.tracks = tracks;
    }


//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//
//    }
//    public static final Parcelable.Creator<ShowDetails> CREATOR = new Creator<ShowDetails>() {
//        public ShowDetails createFromParcel(Parcel source) {
//            ShowDetails showDetails = new ShowDetails();
//
//            showDetails.showId = source.readInt();
//            showDetails.date = source.readString()
//            showDetails.duration = source.readInt();
////            private boolean = source.readB
////            private boolean soundBoard;
////            private boolean remastered;
////            private int tourId;
//            private Venue venue;
//            private List tracks;
//            mBook.bookName = source.readString();
//            mBook.author = source.readString();
//            mBook.publishTime = source.readInt();
//            return mBook;
//        }
    }
