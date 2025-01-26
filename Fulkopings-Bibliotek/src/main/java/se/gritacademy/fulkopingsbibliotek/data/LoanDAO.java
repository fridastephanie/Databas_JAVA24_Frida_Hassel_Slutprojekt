package se.gritacademy.fulkopingsbibliotek.data;

import se.gritacademy.fulkopingsbibliotek.model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    //Referens till databasanslutningen som används för att utföra databasoperationer
    private Connection connection;

    /**
     * Konstuktor för LoanDAO som atr emot en databasanslutning och sätter den som referens för att utföra databasoperationer
     *
     * @param connection - Databasanslutningen
     */
    public LoanDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Lånar ut ett Media-objekt till en användare genom att lägga till en post i Loan-tabellen
     * och uppdatera Media-tabellen för att markera att Media-objektet är utlånat
     *
     * @param mediaId - Id för Media-objektet som ska lånas ut
     * @param userId - Id för användaren som lånar Media-objektet
     * @param startDate - Startdatumet för lånet
     * @param endDate - Slutdatumet för lånet
     * @throws SQLException
     */
    public void loanMedia(int mediaId, int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String loanSql = "INSERT INTO Loan (mediaId, userId, startDate, endDate) VALUES (?, ?, ?, ?)";
        String updateMediaSql = "UPDATE Media SET isRented = TRUE WHERE id = ?";

        try (PreparedStatement loanStatement = connection.prepareStatement(loanSql);
             PreparedStatement updateMediaStatement = connection.prepareStatement(updateMediaSql)) {

            loanStatement.setInt(1, mediaId);
            loanStatement.setInt(2, userId);
            loanStatement.setDate(3, Date.valueOf(startDate));
            loanStatement.setDate(4, Date.valueOf(endDate));
            loanStatement.executeUpdate();

            updateMediaStatement.setInt(1, mediaId);
            updateMediaStatement.executeUpdate();
        }
    }

    /**
     * Hämtar userId för den användare som för närvarande lånar ett specifikt Media-objekt
     *
     * @param mediaId - Id för Media-objektet som ska kontrolleras
     * @return - userId för den användare som lånar Media-objektet, eller -1 om inget pågående lån finns
     * @throws SQLException
     */
    public int getRentedByUserId(int mediaId) throws SQLException {
        String sql = "SELECT userId FROM Loan WHERE mediaId = ? AND CURDATE() BETWEEN startDate AND endDate AND returnedDate IS NULL";
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
     * Hämtar slutdatumet för ett pågående lån av ett specifikt Media-objekt
     *
     * @param mediaId - Id för Media-objektet vars slutdatum ska hämtas
     * @return - Slutdatum för lånet om Media-objektet är utlånat, annars null
     * @throws SQLException
     */
    public LocalDate getEndDateOfLoan(int mediaId) throws SQLException {
        String sql = "SELECT endDate FROM Loan WHERE mediaId = ? AND returnedDate IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("endDate").toLocalDate();
            }
        }
        return null;
    }

    /**
     * Hämtar alla aktiva lån för en specifik användare, sorterat efter slutdatumet på lånet
     *
     * @param userId - Id för användaren vars aktiva lån ska hämtas
     * @return - En lista med användarens aktiva lån inklusive information om lånet och Media-objektets titel
     * @throws SQLException
     */
    public List<Loan> getActiveLoansByUserId(int userId) throws SQLException {
        String sql = "SELECT Loan.id, Loan.mediaId, Loan.userId, Loan.startDate, Loan.endDate, Loan.returnedDate, Media.title " +
                "FROM Loan " +
                "JOIN Media ON Loan.mediaId = Media.id " +
                "WHERE Loan.userId = ? AND Loan.returnedDate IS NULL " +
                "ORDER BY Loan.endDate ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Loan> loans = new ArrayList<>();
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setId(rs.getInt("id"));
                loan.setMediaId(rs.getInt("mediaId"));
                loan.setUserId(rs.getInt("userId"));
                loan.setStartDate(rs.getDate("startDate"));
                loan.setEndDate(rs.getDate("endDate"));
                loan.setReturnedDate(rs.getDate("returnedDate"));
                loan.setMediaTitle(rs.getString("title"));
                loans.add(loan);
            }
            return loans;
        }
    }

    /**
     * Hämtar en specifik användares lånehistorik inklusive tidigare lån som har returnerats,
     * sorterat efter startdatumet på lånet
     *
     * @param userId - Id för användaren vars lånehistorik ska hämtas
     * @return - En lista med alla lån som användaren har gjort
     * @throws SQLException
     */
    public List<Loan> getLoanHistoryByUserId(int userId) throws SQLException {
        String sql = "SELECT Loan.id, Loan.mediaId, Loan.userId, Loan.startDate, Loan.endDate, Loan.returnedDate, Media.title " +
                "FROM Loan " +
                "JOIN Media ON Loan.mediaId = Media.id " +
                "WHERE Loan.userId = ? " +
                "ORDER BY Loan.startDate ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Loan> loanHistory = new ArrayList<>();
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setId(rs.getInt("id"));
                loan.setMediaId(rs.getInt("mediaId"));
                loan.setUserId(rs.getInt("userId"));
                loan.setStartDate(rs.getDate("startDate"));
                loan.setEndDate(rs.getDate("endDate"));
                loan.setReturnedDate(rs.getDate("returnedDate"));
                loan.setMediaTitle(rs.getString("title"));
                loanHistory.add(loan);
            }
            return loanHistory;
        }
    }

    /**
     * Registrerar returneringen av ett lån genom att uppdatera returnedDate för det angivna lånet
     *
     * @param loanId - Id för lånet som ska uppdateras
     * @param returnDate - Datumet för återlämningen
     * @throws SQLException
     */
    public void returnLoan(int loanId, LocalDate returnDate) throws SQLException {
        String sql = "UPDATE Loan SET returnedDate = ? WHERE id = ? AND returnedDate IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(returnDate));
            stmt.setInt(2, loanId);
            stmt.executeUpdate();
        }
    }
}
