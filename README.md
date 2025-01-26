# Slutprojekt - <i>Bibliotekssystem för Fulköping</i>
Ett Java-baserat bibliotekssystem designat för att ge användarna en enkel och intuitiv upplevelse. Systemet integrerar en MySQL-databas för effektiv hantering av media, inklusive sökning, lån och reservationer, samtidigt som det är byggt för att vara både robust och användarcentrerat.

## Funktioner
1.	Sök och filtrera media: <i>Sök generellt eller specifikt (titel, författare, mediatyp)</i>
2.	Lån och återlämning: <i>Låna tillgängliga böcker, tidskrifter och annan media, med tydliga förfallodatum</i>
3.	Reservationer: <i>Reservera utlånad media. Reservationer gäller i 30 dagar efter tillgänglighet</i>
4.	Användarhantering: <i>Skapa konto, logga in och uppdatera profil. Visa lånestatus och historik</i>

## Systemkrav
•	Java 17<br>
•	JavaFX 23.0.1<br>
•	MySQL Server (via Docker rekommenderas)

## Installationsguide
#### 1. Klona projektet
git clone https://github.com/MH-GRIT/fulk-pings-bibliotek-fridastephanie.git
#### 2. Konfigurera databasen
1.	Starta MySQL-servern (t.ex. via Docker)
2.	Importera SQL-filen till en tom databas och se till nedladdningen lyckas

#### 3. Bygg och kör programmet
1.	Öppna projektet i din IDE (t.ex. IntelliJ IDEA)
2.  Uppdatera database.properties i projektet efter egna iställningar, t.ex.:<br>
db.host=localhost<br>
db.port=3306<br>
db.name=yourDatabaseName<br>
db.username=yourUsername<br>
db.password=yourPassword<br>
3.	Kör huvudklassen Application för att starta applikationen

## Kör programmet
1.	Skapa konto och logga in
2.	Utforska biblioteket:<br>
Vänster sida: <i>Personlig uppgifter</i><br>
•	Se pågående lån, reservationer, lånehistorik, ändra kontouppgifter, lämna tillbaka media och ta bort reservationer<br>
Höger sida: <i>Bibliotek</i><br>
•	Sök media, visa media, låna ledig media eller reservera utlånad media<br>
•	Mediatypen böcker kan lånas i max 30 dagar, medan all annan media kan lånas i max 10 dagar<br>
•	En reservation är endast giltigt i 30 dagar, därefter försvinner reservationen<br>
•	Reservationen får först slutdatum efter när boken senast förväntas återlämnas,<br>
men när boken återlämnats uppdateras datumet till 30 dagar efter den faktiska återlämningen

## Dataskydd och säkerhet
•	Lösenord lagras som hashade värden<br>
•	Användning av Prepared Statements skyddar mot SQL-injektion
