package se.gritacademy.fulkopingsbibliotek.data;

import se.gritacademy.fulkopingsbibliotek.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    //Referens till databasanslutningen som används för att utföra databasoperationer
    private Connection connection;

    /**
     * Konstuktor för UserDAO som atr emot en databasanslutning och sätter den som referens för att utföra databasoperationer
     *
     * @param connection - Databasanslutningen
     */
    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Skapar en ny användare med namn, email och lösenord i databasen
     *
     * @param user - User-objekt med information om användaren som ska skapas
     * @throws SQLException
     */
    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO User (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Kontrollerar om en användare med den angivna e-postadressen redan finns i databasen
     *
     * @param email - E-postadressen som ska kontrolleras
     * @return - True om använaren finns, annars False
     * @throws SQLException
     */
    public boolean checkUserExist(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM User WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Hämtar en användare från databasen baserat på angiven e-postadress
     *
     * @param userEmail - De angivna e-postadressen för användaren som ska hämtas
     * @return - User-objekt om en användare med den angivna e-postadressen hittas, annars null
     * @throws SQLException
     */
    public User getUserByEmail(String userEmail) throws SQLException {
        String sql = "SELECT id, name, email, password FROM User WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                User user = new User(name, email, password);
                user.setId(id);
                return user;
            }
        }
        return null;
    }

    /**
     * Uppdaterar namnet på en användare i databasen baserat på användarens Id
     *
     * @param userId - Id för användaren vars namn ska uppdateras
     * @param newName - Det nya namnet som användaren angivit
     * @throws SQLException
     */
    public void updateUserName(int userId, String newName) throws SQLException {
        String sql = "UPDATE User SET name = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Kontrollerar om e-postadressen som angivits redan finns sparad i datasbasen,
     * om e-postadressen inte finns uppdateras e-postadressen hos användare
     *
     * @param userId - Id för användaren vars e-postadress ska uppdateras
     * @param newEmail - Den nya e-postadressen som användaren angivit
     * @throws SQLException
     */
    public void updateUserEmail(int userId, String newEmail) throws SQLException {
        if (checkUserExist(newEmail)) {
            throw new IllegalArgumentException("E-postadressen används redan av en annan användare!");
        }
        String sql = "UPDATE User SET email = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newEmail);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Uppdaterar lösenordet hos en användare i databasen baserat på användarens Id
     *
     * @param userId - Id för användaren vars lösenord ska uppdateras
     * @param hashedPassword - Den hashade versionen av användarens inmatade lösenord
     * @throws SQLException
     */
    public void updateUserPassword(int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE User SET password = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
}