package se.gritacademy.fulkopingsbibliotek.data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    // Referens till databasanslutningen som används för att hantera alla databasoperationer
    private static Connection dataBase = null;

    /**
     * Sjaoar eb anslutning till databasen om det inte redan finns en aktiv anslutning,
     * isåfall returneras den befintliga anslutningen
     *
     * @return Connection-objekt för databasanslutningen eller null om anslutningen inte kan etableras
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        if (dataBase != null && !dataBase.isClosed()) {
            return dataBase;
        }

        Properties props = loadDatabaseProperties();
        if (props == null) {
            return null;
        }

        String url = buildDatabaseUrl(props);
        String username = props.getProperty("dataBase.username");
        String password = props.getProperty("dataBase.password");
        dataBase = createDatabaseConnection(url, username, password);
        return dataBase;
    }

    /**
     * Laddar databasens konfigurationsinställningar från filen database.properties i resourcesmappen
     *
     * @return Properties-objekt som innehåller databasinställningarna, eller null vid inläsningsfil
     */
    private static Properties loadDatabaseProperties() {
        Properties props = new Properties();
        try (InputStream inputStream = Database.class.getResourceAsStream("/database.properties")) {
            props.load(inputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return props;
    }

    /**
     * Skapar en URL-sträng för att ansluta till en databas baserat på värden från en properties-fil
     *
     * @param props - Properties-objekt som innehåller anslutningsparametrar (host, port, namn på databasen)
     * @return - En URL-sträng för databasanslutningen
     */
    private static String buildDatabaseUrl(Properties props) {
        String host = props.getProperty("dataBase.host");
        String port = props.getProperty("dataBase.port");
        String name = props.getProperty("dataBase.name");
        return "jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=false&autoReconnect=true";
    }

    /**
     * Skapar en anslutning till databasen med en angiven URL-sträng samt användarnamn och lösenord.
     * Returnerar null och loggar felmeddelandet vid misslyckad anslutning
     *
     * @param url - Angiven URL-sträng
     * @param username - Angivet användarnamn för att autentisera till databasen
     * @param password - Angivet lösenord för att autentisera till databasen
     * @return Connection-objekt om anslutningen lyckades, annars null
     */
    private static Connection createDatabaseConnection(String url, String username, String password) {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            SQLExceptionPrint(e);
            return null;
        }
    }

    /**
     * Skriver ut ett felmeddelande vid SQLException
     *
     * @param sqle - SQLException som ska åtgärdas
     */
    public static void SQLExceptionPrint(SQLException sqle) {
        SQLExceptionPrint(sqle, false);
    }

    /**
     * Skriver ut ett detaljerat felmeddelande vid SQLExeption
     *
     * @param sqle - SQLException som ska åtgärdas
     * @param printStackTrace - Skriver ut stacketracen för undantaget (detaljerad information om felet)
     */
    public static void SQLExceptionPrint(SQLException sqle, Boolean printStackTrace) {
        while (sqle != null) {
            System.out.println("\n----SQLException caugth---\n");
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Severity: " + sqle.getErrorCode());
            System.out.println("Message: " + sqle.getMessage());

            if (printStackTrace) sqle.printStackTrace();
            sqle = sqle.getNextException();
        }
    }
}