package com.terrymoreii.phishradio.model;

import java.io.Serializable;

/**
 * Created by tmoore on 7/20/14.
 */
public class PlayList implements Serializable{

    private ShowDetails showDetails;
    private int currentPosition;

    public ShowDetails getShowDetails() {
        return showDetails;
    }

    public void setShowDetails(ShowDetails showDetails) {
        this.showDetails = showDetails;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
