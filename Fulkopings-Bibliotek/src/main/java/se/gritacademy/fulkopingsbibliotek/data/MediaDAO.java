package se.gritacademy.fulkopingsbibliotek.data;

import se.gritacademy.fulkopingsbibliotek.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaDAO {
    //Referens till databasanslutningen som används för att utföra databasoperationer
    private Connection connection;

    /**
     * Konstuktor för MediaDAO som atr emot en databasanslutning och sätter den som referens för att utföra databasoperationer
     *
     * @param connection - Databasanslutningen
     */
    public MediaDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Hämtar alla Media-objekt från databasen och returnerar en lista med Media-objekten, sorterade i bokstavsordning efter titeln,
     * samt skapar lokala Media-objekt baserat på mediatypen
     *
     * @return - En lista med alla mediaobjekt som finns i databasen
     * @throws SQLException
     */
    public List<Media> getAllMedia() throws SQLException {
        List<Media> mediaList = new ArrayList<>();
        String sql = "SELECT * FROM Media ORDER BY title ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String mediaType = rs.getString("mediaType");
                Media media = Media.createMedia(mediaType);

                media.setId(rs.getInt("id"));
                media.setTitle(rs.getString("title"));
                media.setAuthor(rs.getString("author"));
                media.setIsbn(rs.getString("isbn"));
                media.setGenre(rs.getString("genre"));
                media.setRented(rs.getBoolean("isRented"));
                media.setReserved(rs.getBoolean("isReserved"));
                media.setMediaType(MediaType.valueOf(mediaType.toUpperCase()));

                mediaList.add(media);
            }
        }
        return mediaList;
    }

    /**
     * Hämtar alla Media-objekt av en specifik mediatyp från databasen, som sorteras i bokstavsordning efter titeln
     *
     * @param mediaType - Mediatypen som ska hämtas (t.ex. "BOOK", "MOVIE", "MAGAZINE", "CD")
     * @return - En lista med Media-objekt av den angivna typen, inklusive informationen om Media-objektet
     * @throws SQLException
     */
    public List<Media> getMediaByType (String mediaType) throws SQLException {
        String sql = "SELECT * FROM Media WHERE mediaType LIKE ? ORDER BY title ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, mediaType);
            ResultSet rs = stmt.executeQuery();

            List<Media> mediaList = new ArrayList<>();
            while (rs.next()) {
                Media media = Media.createMedia(rs.getString("mediaType"));

                media.setId(rs.getInt("id"));
                media.setTitle(rs.getString("title"));
                media.setAuthor(rs.getString("author"));
                media.setIsbn(rs.getString("isbn"));
                media.setGenre(rs.getString("genre"));
                media.setRented(rs.getBoolean("isRented"));
                media.setReserved(rs.getBoolean("isReserved"));
                media.setMediaType(MediaType.valueOf(mediaType.toUpperCase()));
                mediaList.add(media);
            }
            return mediaList;
        }
    }

    /**
     * Söker efter Media-objekt i databasen baserat på en inmatad söksträng,
     * på alla mediatyper och i fälten titel, författare, ISBN och genre. Sökningen använder wildcard (%) och söker
     * därför även på delar av ord och är inte skiftslägeskänslig. Resultaten sorteras i bokstavsordning efter titeln
     *
     * @param searchWord - Användarens inmatade sökord från sökfältet
     * @return - En lista med Media-objekt som matchar sökvillkoren
     * @throws SQLException
     */
    public List<Media> searchInAllMedia (String searchWord) throws SQLException {
        String sql = "SELECT * FROM Media WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? OR genre LIKE ? ORDER BY title ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchLike = "%" + searchWord + "%";

            stmt.setString(1, searchLike);
            stmt.setString(2, searchLike);
            stmt.setString(3, searchLike);
            stmt.setString(4, searchLike);
            ResultSet rs = stmt.executeQuery();

            List<Media> mediaList = new ArrayList<>();
            while (rs.next()) {
                String mediaType = rs.getString("mediaType");
                Media media = Media.createMedia(mediaType);

                media.setId(rs.getInt("id"));
                media.setTitle(rs.getString("title"));
                media.setAuthor(rs.getString("author"));
                media.setIsbn(rs.getString("isbn"));
                media.setGenre(rs.getString("genre"));
                media.setRented(rs.getBoolean("isRented"));
                media.setReserved(rs.getBoolean("isReserved"));
                media.setMediaType(MediaType.valueOf(mediaType.toUpperCase()));
                mediaList.add(media);
            }
            return mediaList;
        }
    }

    /**
     * Söker bland Media-objektets titlar. Sökningen är inte skiftlägeskänslig och använder wildcard (%)
     * för att matcha titlar som innehåller söksträngen. Resultaten sorteras i bokstavsordning efter titel
     *
     * @param searchWord - Användarens inmatade sökord från sökfältet
     * @return - En lista med Media-objekt som matchar sökvillkoren
     * @throws SQLException
     */
    public List<Media> searchMediaByTitle (String searchWord) throws SQLException {
        String sql = "SELECT * FROM Media WHERE title LIKE ? ORDER BY title ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchWord + "%");
            ResultSet rs = stmt.executeQuery();

            List<Media> mediaList = new ArrayList<>();
            while (rs.next()) {
                String mediaType = rs.getString("mediaType");
                Media media = Media.createMedia(mediaType);

                media.setId(rs.getInt("id"));
                media.setTitle(rs.getString("title"));
                media.setAuthor(rs.getString("author"));
                media.setIsbn(rs.getString("isbn"));
                media.setGenre(rs.getString("genre"));
                media.setRented(rs.getBoolean("isRented"));
                media.setReserved(rs.getBoolean("isReserved"));
                media.setMediaType(MediaType.valueOf(mediaType.toUpperCase()));
                mediaList.add(media);
            }
            return mediaList;
        }
    }

    /**
     * Söker bland Media-objektets författare. Sökningen är inte skiftlägeskänslig och använder wildcard (%)
     * för att matcha författare som innehåller söksträngen. Resultaten sorteras i bokstavsordning efter titel
     *
     * @param searchWord - Användarens inmatade sökord från sökfältet
     * @return - En lista med Media-objekt som matchar sökvillkoren
     * @throws SQLException
     */
    public List<Media> searchMediaByAuthor(String searchWord) throws SQLException {
        String sql = "SELECT * FROM Media WHERE author LIKE ? ORDER BY title ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchWord + "%");
            ResultSet rs = stmt.executeQuery();

            List<Media> mediaList = new ArrayList<>();
            while (rs.next()) {
                String mediaType = rs.getString("mediaType");
                Media media = Media.createMedia(mediaType);

                media.setId(rs.getInt("id"));
                media.setTitle(rs.getString("title"));
                media.setAuthor(rs.getString("author"));
                media.setIsbn(rs.getString("isbn"));
                media.setGenre(rs.getString("genre"));
                media.setRented(rs.getBoolean("isRented"));
                media.setReserved(rs.getBoolean("isReserved"));
                media.setMediaType(MediaType.valueOf(mediaType.toUpperCase()));
                mediaList.add(media);
            }
            return mediaList;
        }
    }

    /**
     * Söker bland Media-objektets ISBN. Sökningen är inte skiftlägeskänslig och använder wildcard (%)
     * för att matcha ISBN som innehåller söksträngen. Resultaten sorteras i bokstavsordning efter titel
     *
     * @param searchWord - Användarens inmatade sökord från sökfältet
     * @return - En lista med Media-objekt som matchar sökvillkoren
     * @throws SQLException
     */
    public List<Media> searchMediaByISBN(String searchWord) throws SQLException {
        String sql = "SELECT * FROM Media WHERE isbn LIKE ? ORDER BY title ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchWord + "%");
            ResultSet rs = stmt.executeQuery();

            List<Media> mediaList = new ArrayList<>();
            while (rs.next()) {
                String mediaType = rs.getString("mediaType");
                Media media = Media.createMedia(mediaType);

                media.setId(rs.getInt("id"));
                media.setTitle(rs.getString("title"));
                media.setAuthor(rs.getString("author"));
                media.setIsbn(rs.getString("isbn"));
                media.setGenre(rs.getString("genre"));
                media.setRented(rs.getBoolean("isRented"));
                media.setReserved(rs.getBoolean("isReserved"));
                media.setMediaType(MediaType.valueOf(mediaType.toUpperCase()));
                mediaList.add(media);
            }
            return mediaList;
        }
    }

    /**
     * Söker bland Media-objektets genre. Sökningen är inte skiftlägeskänslig och använder wildcard (%)
     * för att matcha genre som innehåller söksträngen. Resultaten sorteras i bokstavsordning efter titel
     *
     * @param searchWord - Användarens inmatade sökord från sökfältet
     * @return - En lista med Media-objekt som matchar sökvillkoren
     * @throws SQLException
     */
    public List<Media> searchMediaByGenre(String searchWord) throws SQLException {
        String sql = "SELECT * FROM Media WHERE genre LIKE ? ORDER BY title ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchWord + "%");
            ResultSet rs = stmt.executeQuery();

            List<Media> mediaList = new ArrayList<>();
            while (rs.next()) {
                String mediaType = rs.getString("mediaType");
                Media media = Media.createMedia(mediaType);

                media.setId(rs.getInt("id"));
                media.setTitle(rs.getString("title"));
                media.setAuthor(rs.getString("author"));
                media.setIsbn(rs.getString("isbn"));
                media.setGenre(rs.getString("genre"));
                media.setRented(rs.getBoolean("isRented"));
                media.setReserved(rs.getBoolean("isReserved"));
                media.setMediaType(MediaType.valueOf(mediaType.toUpperCase()));
                mediaList.add(media);
            }
            return mediaList;
        }
    }

    /**
     * Kontrollerar om ett specifikt Media-objekt är reserverat
     *
     * @param mediaId - Id för Media-objektet som ska kontrolleras
     * @return - Om Media-objektet är reserverat returneras True, annars False om objektet inte är reserverat
     * @throws SQLException
     */
    public boolean isMediaReserved(int mediaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Reservation WHERE mediaId = ? AND startDate > NOW()";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Uppdaterar lånestatusen för ett specifikt Media-objekt i databasen
     *
     * @param mediaId - Id för Media-objektet vars lånestatus ska uppdateras
     * @param isRented - Ny lånestatus för objektet, True om objektet är utlånat eller False om objektet är tillgängligt
     * @throws SQLException
     */
    public void updateMediaRentedStatus(int mediaId, boolean isRented) throws SQLException {
        String sql = "UPDATE Media SET isRented = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isRented);
            stmt.setInt(2, mediaId);
            stmt.executeUpdate();
        }
    }

    /**
     * Uppdaterar reserveringsstatusen för ett specifikt Media-objekt i databasen
     *
     * @param mediaId - Id för Media-objektet vars reserveringsstatus ska uppdateras
     * @param isReserved - Ny reserveringsstatus för objektet, True om objektet är reserverat eller False om objektet är tillgängligt
     * @throws SQLException
     */
    public void updateMediaReservedStatus(int mediaId, boolean isReserved) throws SQLException {
        String sql = "UPDATE Media SET isReserved = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isReserved);
            stmt.setInt(2, mediaId);
            stmt.executeUpdate();
        }
    }
}