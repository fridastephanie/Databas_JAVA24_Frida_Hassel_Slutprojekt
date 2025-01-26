-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Värd: mysql
-- Tid vid skapande: 23 jan 2025 kl 15:31
-- Serverversion: 8.0.40
-- PHP-version: 8.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databas: `Fulkopings_bibliotek`
--

-- --------------------------------------------------------

--
-- Tabellstruktur `Loan`
--

CREATE TABLE `Loan` (
  `id` bigint NOT NULL,
  `mediaId` bigint NOT NULL,
  `userId` bigint NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL,
  `returnedDate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Tabellstruktur `Media`
--

CREATE TABLE `Media` (
  `id` bigint NOT NULL,
  `title` varchar(50) NOT NULL,
  `author` varchar(50) NOT NULL,
  `isbn` varchar(50) NOT NULL,
  `genre` varchar(50) NOT NULL,
  `isRented` tinyint(1) NOT NULL DEFAULT '0',
  `isReserved` tinyint(1) NOT NULL DEFAULT '0',
  `mediaType` enum('BOOK','MAGAZINE','MOVIE','CD') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumpning av Data i tabell `Media`
--

INSERT INTO `Media` (`id`, `title`, `author`, `isbn`, `genre`, `isRented`, `isReserved`, `mediaType`) VALUES
(1, 'Alla vänner', 'Freja Friendly', '978-3-16-148410-0', 'Ungdom', 0, 0, 'BOOK'),
(2, 'Baby blues', 'Billy Blues', '978-6-54-321098-7', 'Blues', 0, 0, 'CD'),
(3, 'Cykeltjuven', 'Cecilia Cykla', '978-2-34-567890-1', 'Deckare', 0, 0, 'BOOK'),
(4, 'Dahlians hemlighet', 'Tommy Trädgård', '978-4-12-345678-9', 'Trädgård', 0, 0, 'MAGAZINE'),
(5, 'Dansande stjärnor', 'Dino Dexter', '978-3-45-678901-2', 'Science Fiction', 0, 0, 'MOVIE'),
(6, 'Jazzå?', 'Jasmine Jazz', '978-2-34-567890-2', 'Jazz', 0, 0, 'CD'),
(7, 'En magisk resa', 'Elsa Enhörning', '978-4-56-789012-3', 'Fantasy', 0, 0, 'BOOK'),
(8, 'Fikapausen', 'Filippa Fika', '978-5-43-210987-6', 'Humor', 0, 0, 'BOOK'),
(9, 'Fulköpings dagblad', 'Fulköpings kommun', '978-8-76-543210-9', 'Nyheter', 0, 0, 'MAGAZINE'),
(10, 'Guldskattens återkomst', 'Göran Guld', '978-6-78-901234-5', 'Äventyr', 0, 0, 'BOOK'),
(11, 'Gåspappan', 'Nils Holgersson', '978-0-98-765432-1', 'Deckare', 0, 0, 'MOVIE'),
(12, 'Hallonmysteriet', 'Hanna Hallon', '978-7-89-012345-6', 'Mysterium', 0, 0, 'BOOK'),
(13, 'Klassiska toner', 'Kalle Klassisk', '978-7-43-210987-5', 'Klassisk', 0, 0, 'CD'),
(14, 'Livet på landet', 'Lotta Lantbruk', '978-8-76-543210-9', 'Natur', 0, 0, 'MAGAZINE'),
(15, 'Månlandningen', 'Magnus Mån', '978-0-98-765432-1', 'Science Fiction', 0, 0, 'MOVIE'),
(16, 'Popmix', 'Petra Pop', '978-5-67-890123-4', 'Musik', 0, 0, 'CD'),
(17, 'Robotens revansch', 'Ragnar Robot', '978-5-43-210987-6', 'Science Fiction', 0, 0, 'BOOK'),
(18, 'Semesterresan', 'Rikard Reza', '978-3-45-678901-1', 'Äventyr', 0, 0, 'BOOK'),
(19, 'Skattjakten', 'Peter Pirat', '978-4-56-789012-0', 'Action', 0, 0, 'MOVIE'),
(20, 'Urskogens varelser', 'Ulla Urskog', '978-3-45-678901-1', 'Äventyr', 0, 0, 'BOOK'),
(21, 'Vintersport', 'Viktor Vinter', '978-0-98-765432-1', 'Sport', 0, 0, 'MAGAZINE'),
(22, 'Vovvarnas äventyr', 'Vera Voff', '978-1-23-456789-0', 'Familj', 0, 0, 'MOVIE'),
(23, 'Yoga livsstilen', 'Ylva Yoga', '978-7-89-012345-6', 'Hälsa', 0, 0, 'BOOK'),
(24, 'Zebrans försvunna rand', 'Zara Zebra', '978-3-45-678901-2', 'Mysterium', 0, 0, 'BOOK');

-- --------------------------------------------------------

--
-- Tabellstruktur `Reservation`
--

CREATE TABLE `Reservation` (
  `id` bigint NOT NULL,
  `mediaId` bigint NOT NULL,
  `userId` bigint NOT NULL,
  `startDate` date DEFAULT NULL,
  `expiryDate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Tabellstruktur `User`
--

CREATE TABLE `User` (
  `id` bigint NOT NULL,
  `name` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Index för dumpade tabeller
--

--
-- Index för tabell `Loan`
--
ALTER TABLE `Loan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mediaId` (`mediaId`),
  ADD KEY `userId` (`userId`);

--
-- Index för tabell `Media`
--
ALTER TABLE `Media`
  ADD PRIMARY KEY (`id`);

--
-- Index för tabell `Reservation`
--
ALTER TABLE `Reservation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mediaId` (`mediaId`),
  ADD KEY `userId` (`userId`);

--
-- Index för tabell `User`
--
ALTER TABLE `User`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT för dumpade tabeller
--

--
-- AUTO_INCREMENT för tabell `Loan`
--
ALTER TABLE `Loan`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT för tabell `Media`
--
ALTER TABLE `Media`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT för tabell `Reservation`
--
ALTER TABLE `Reservation`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT för tabell `User`
--
ALTER TABLE `User`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- Restriktioner för dumpade tabeller
--

--
-- Restriktioner för tabell `Loan`
--
ALTER TABLE `Loan`
  ADD CONSTRAINT `Loan_ibfk_1` FOREIGN KEY (`mediaId`) REFERENCES `Media` (`id`),
  ADD CONSTRAINT `Loan_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `User` (`id`);

--
-- Restriktioner för tabell `Reservation`
--
ALTER TABLE `Reservation`
  ADD CONSTRAINT `Reservation_ibfk_1` FOREIGN KEY (`mediaId`) REFERENCES `Media` (`id`),
  ADD CONSTRAINT `Reservation_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `User` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
