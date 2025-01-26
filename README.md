# Slutprojekt - <i>Bibliotekssystem för Fulköping</i>
Ett Java-baserat bibliotekssystem designat för att ge användarna en enkel och intuitiv upplevelse. Systemet integrerar en MySQL-databas för effektiv hantering av media, inklusive sökning, lån och reservationer, samtidigt som det är byggt för att vara både robust och användarcentrerat.

## Funktioner
1.	Sök och filtrera media: Sök generellt eller specifikt (titel, författare, mediatyp)
2.	Lån och återlämning: Låna tillgängliga böcker, tidskrifter och annan media, med tydliga förfallodatum
3.	Reservationer: Reservera utlånad media. Reservationer gäller i 30 dagar efter tillgänglighet
4.	Användarhantering: Skapa konto, logga in och uppdatera profil. Visa lånestatus och historik

## Systemkrav
•	Java 17<br>
•	JavaFX 23.0.1<br>
•	MySQL Server (via Docker rekommenderas)

## Installationsguide
#### 1. Klona projektet
git clone https://github.com/MH-GRIT/fulk-pings-bibliotek-fridastephanie.git
#### 2. Konfigurera databasen
1.	Starta MySQL-servern (t.ex. via Docker)
2.	Importera SQL-filen i DBeaver:<br>
•	File > Open File och kör filen i rätt databas
3.	Uppdatera database.properties i projektet efter egna iställningar, t.ex.:<br>
db.host=localhost<br>
db.port=3306<br>
db.name=yourDatabaseName<br>
db.username=yourUsername<br>
db.password=yourPassword<br>
#### 3. Bygg och kör programmet
1.	Öppna projektet i din IDE (t.ex. IntelliJ IDEA)
2.	Kör huvudklassen Application för att starta applikationen

## Kör programmet
1.	Skapa konto och logga in
2.	Utforska biblioteket:<br>
•	Vänster sida: Personlig uppgifter<br>
Se pågående lån, reservationer, lånehistorik, ändra kontouppgifter, lämna tillbaka media och ta bort reservationer<br>
•	Höger sida: Bibliotek<br>
Sök media, visa media, låna ledig media eller reservera utlånad media

## Dataskydd och säkerhet
•	Lösenord lagras som hashade värden<br>
•	Användning av Prepared Statements skyddar mot SQL-injektion
