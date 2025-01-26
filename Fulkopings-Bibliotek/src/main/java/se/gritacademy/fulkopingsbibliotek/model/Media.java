package se.gritacademy.fulkopingsbibliotek.model;

public abstract class Media {
    //Media-objektets Id
    private int id;
    //Media-objektets titel
    private String title;
    //Media-objektets författare
    private String author;
    //Media-objektets ISBN
    private String isbn;
    //Media-objektets genre
    private String genre;
    //Media-objektets lånestatus
    private boolean isRented;
    //Media-objektets reserveringsstatus
    private boolean isReserved;
    //Media-objektets mediatyp
    private MediaType mediaType;
    //Användar Id för användare som lånar Media-objektet
    private int loanByUserId;
    //Användar Id för användaren som reserverar Media-objektet
    private int reservedByUserId;

    /**
     * Skapar ett nytt Media-objekt baserat på den angivna mediatypen
     *
     * @param type - Mediatyp som används för att avgöra vilken typ av Media-objekt som ska skapas
     * @return - En instans av en specifik typ av Media baserat på den angivna mediatypen
     */
    public static Media createMedia(String type) {
        switch (type.toUpperCase()) {
            case "BOOK":
                return new Book();
            case "CD":
                return new CD();
            case "MOVIE":
                return new Movie();
            case "MAGAZINE":
                return new Magazine();
            default:
                throw new IllegalArgumentException("Okänd mediatyp: " + type);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public int getReservedByUserId() {
        return reservedByUserId;
    }

    public void setReservedByUserId(int reservedByUserId) {
        this.reservedByUserId = reservedByUserId;
    }

    public int getLoanByUserId() {
        return loanByUserId;
    }

    public void setLoanByUserId(int loanByUserId) {
        this.loanByUserId = loanByUserId;
    }
}