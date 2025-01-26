package se.gritacademy.fulkopingsbibliotek.model;

import se.gritacademy.fulkopingsbibliotek.security.PasswordHashing;

public class User {
    //Användarens Id
    private int id;
    //Användarens namn
    private String name;
    //Användarens email
    private String email;
    //Användarens lösenord
    private String password;

    /**
     * Konstruktor för User som inhämtar användarens namn, email och lösenord
     *
     * @param name - Användarens angivna namn
     * @param email - Användarens angivna email
     * @param password - Användarens angivna lösenord
     */
    public User(String name, String email, String password) {
        setName(name);
        setEmail(email);
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * Sätter namnet efter att ha formaterat det så att varje ord börjar med stor bokstav och resten av bokstäverna är små
     *
     * @param name - Namnet som ska sättas för objektet
     */
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            String[] words = name.split("(?<=\\b)(?=\\s|-)|(?<=\\s|-)");
            StringBuilder formattedName = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    formattedName.append(word.substring(0, 1).toUpperCase());
                    formattedName.append(word.substring(1).toLowerCase());
                }
            }
            name = formattedName.toString().trim();
        }
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    /**
     * Sätter e-post efter att ha validerat att den är i rätt format, samt gör alla bokstäver till små bokstäver
     *
     * @param email - Den e-postadress som ska sättas för objektet
     */
    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Ogiltig e-postadress: " + email);
        }
        this.email = email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    /**
     * Kontrollerar att password inte är null eller tom, därefter hashas lösenordet
     *
     * @param password - Användarens inmatade lösenord
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Lösenordet får inte vara tomt");
        }
        this.password = PasswordHashing.createHash(password);
    }
}