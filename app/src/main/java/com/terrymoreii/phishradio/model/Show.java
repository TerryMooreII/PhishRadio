package com.terrymoreii.phishradio.model;

/**
 * Created by tmoore on 7/17/14.
 */
public class Show {

    public int id;
    public String date;
    public String venueName;
    public String location;
    public boolean soundboard;
    public boolean remastered;
    public int duration;

    public Show (int id, String date, String venueName, String location){
        this.id = id;
        this.date = date;
        this.venueName = venueName;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean getSoundboard() {
        return soundboard;
    }

    public void setSoundboard(boolean soundboard) {
        this.soundboard = soundboard;
    }

    public boolean getRemastered() {
        return remastered;
    }

    public void setRemastered(boolean remastered) {
        this.remastered = remastered;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
