package se.gritacademy.fulkopingsbibliotek.model;

import java.util.Date;

public class Loan {
    //Id för lånet
    private int id;
    //Id för Media-Objektet
    private int mediaId;
    //Id för användaren
    private int userId;
    //Media-objektets titel
    private String mediaTitle;
    //Lånets startdatum
    private Date startDate;
    //Lånets slutdatum
    private Date endDate;
    //Returneringsdatumet
    private Date returnedDate;

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

    public String getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(Date returnedDate) {
        this.returnedDate = returnedDate;
    }
}
