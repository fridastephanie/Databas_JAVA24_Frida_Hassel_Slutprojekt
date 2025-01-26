package se.gritacademy.fulkopingsbibliotek.model;

import java.util.Date;

public class Reservation {
    //Id för reservationen
    private int id;
    //Id för Media-objektet
    private int mediaId;
    //Id för användaren
    private int userId;
    //Startdatumet för reservationen
    private Date startDate;
    //Utgångsdatumet för reservationen
    private Date expiryDate;
    //Titeln på Media-objektet
    private String mediaTitle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
    }
}