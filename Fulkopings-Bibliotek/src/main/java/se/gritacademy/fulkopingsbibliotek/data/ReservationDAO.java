package se.gritacademy.fulkopingsbibliotek.data;

import se.gritacademy.fulkopingsbibliotek.model.Reservation;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    //Referens till databasanslutningen som används för att utföra databasoperationer
    private Connection connection;

    /**
     * Konstuktor för ReservationDAO som atr emot en databasanslutning och sätter den som referens för att utföra databasoperationer
     *
     * @param connection - Databasanslutningen
     */
    public ReservationDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Reserverar ett specifikt Media-objekt till en användare under en angiven tidsperiod
     *
     * @param mediaId - Id för Media-objektet som ska reserveras
     * @param userId - Id för användaren som reserverar Media-objeketet
     * @param startDate - Startdatum för reservationen
     * @param expiryDate - Slutdatum för reservationen
     * @throws SQLException
     */
    public void reserveMedia(int mediaId, int userId, LocalDate startDate, LocalDate expiryDate) throws SQLException {
        String reserveSql = "INSERT INTO Reservation (mediaId, userId, startDate, expiryDate) VALUES (?, ?, ?, ?)";
        String updateMediaSql = "UPDATE Media SET isReserved = TRUE WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(reserveSql);
            PreparedStatement updateMediaStatement = connection.prepareStatement(updateMediaSql)){
            stmt.setInt(1, mediaId);
            stmt.setInt(2, userId);
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setDate(4, Date.valueOf(expiryDate));
            stmt.executeUpdate();

            updateMediaStatement.setInt(1, mediaId);
            updateMediaStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    /**
     * Hämtar användarens Id som har reserverat ett specifikt Media-objekt under den aktuella tidsperioden
     *
     * @param mediaId - Id för det Media-objekt som reservationen gäller
     * @return - Id för användaren som har reserverat Media-objektet, eller -1 om ingen reservation hittas
     * @throws SQLException
     */
    public int getReservedByUserId(int mediaId) throws SQLException {
        String sql = "SELECT userId FROM Reservation WHERE mediaId = ? AND CURDATE() BETWEEN startDate AND expiryDate";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("userId");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    /**
     * Hämtar alla aktiva reservationer för en specifik användare, sorterat efter reservationens utgångsdatum
     *
     * @param userId - Id för användaren vars reservationer ska hämtas
     * @return - En lista med alla Media-objekten som användaren har reserverat
     * @throws SQLException
     */
    public List<Reservation> getReservationsByUserId(int userId) throws SQLException {
        String sql = "SELECT Reservation.mediaId, Reservation.userId, Reservation.expiryDate, Media.title " +
                "FROM Reservation " +
                "JOIN Media ON Reservation.mediaId = Media.id " +
                "WHERE Reservation.userId = ? AND Reservation.expiryDate >= CURDATE() " +
                "ORDER BY Reservation.expiryDate ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Reservation> reservations = new ArrayList<>();
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setMediaId(rs.getInt("mediaId"));
                reservation.setUserId(rs.getInt("userId"));
                reservation.setExpiryDate(rs.getDate("expiryDate"));
                reservation.setMediaTitle(rs.getString("title"));
                reservations.add(reservation);
            }
            return reservations;
        }
    }

    /**
     * Hämtar användarens Id som står först i kön för att reservera ett speicfikt Media-objekt
     *
     * @param mediaId - Id för Media-objektet som ska reserveras
     * @return - Användarens Id som är först i kön för att reservera objektet, eller -1 om ingen finns i kön
     * @throws SQLException
     */
    public int getUserInQueue(int mediaId) throws SQLException {
        String sql = "SELECT userId FROM Reservation WHERE mediaId = ? ORDER BY startDate ASC LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("userId");
            }
        }
        return -1;
    }

    /**
     * Uppdaterar start- och utgångsdatum för en reservation kopplad till ett specifikt Media-objekt
     *
     * @param mediaId - Id för Media-objekt vars reservation ska uppdateras
     * @param newStartDate - Det nya startdatumet för reservationen
     * @throws SQLException
     */
    public void updateReservationDates(int mediaId, LocalDate newStartDate) throws SQLException {
        String sql = "UPDATE Reservation SET startDate = ?, expiryDate = ? WHERE mediaId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(newStartDate));
            stmt.setDate(2, Date.valueOf(newStartDate.plusDays(30)));
            stmt.setInt(3, mediaId);
            stmt.executeUpdate();
        }
    }

    /**
     * Tar bort en specifik reservation för ett Media-objekt från databasen
     *
     * @param mediaId - Id för Media-objektet vars reservation ska raderas
     * @param userId - Id för användaren vars reservation ska raderas
     * @throws SQLException
     */
    public void deleteReservation(int mediaId, int userId) throws SQLException {
        String sql = "DELETE FROM Reservation WHERE mediaId = ? AND userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Tar bort alla utgångna reservationer från databasen och uppdaterar Media-objektens reserveringsstatus
     *
     * @throws SQLException
     */
    public void deleteExpiredReservations() throws SQLException {
        String deleteReservationsSql = "DELETE FROM Reservation WHERE expiryDate < ?";
        String updateMediaSql = "UPDATE Media SET isReserved = FALSE WHERE id IN (" +
                                "SELECT mediaId FROM Reservation WHERE expiryDate < ?)";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteReservationsSql);
             PreparedStatement updateStmt = connection.prepareStatement(updateMediaSql)) {

            Date today = Date.valueOf(LocalDate.now());

            deleteStmt.setDate(1, today);
            deleteStmt.executeUpdate();

            updateStmt.setDate(1, today);
            updateStmt.executeUpdate();
        }
    }
}
