-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 28, 2026 at 03:19 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `toko_buku`
--

-- --------------------------------------------------------

--
-- Table structure for table `buku`
--

CREATE TABLE `buku` (
  `ISBN` varchar(20) NOT NULL,
  `Judul` varchar(200) NOT NULL,
  `Harga` bigint(20) DEFAULT NULL,
  `Penerbit` varchar(50) DEFAULT NULL,
  `Photo_Cover` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `buku`
--

INSERT INTO `buku` (`ISBN`, `Judul`, `Harga`, `Penerbit`, `Photo_Cover`) VALUES
('11234567', 'Tentang Kamu', 85000, 'Gramedia Pustaka Utama', 'assets/cover_buku/tentang_kamu.jpg'),
('12345678', 'I Have No Mouth, and I Must Scream', 250000, 'Open Road Media', 'assets/cover_buku/AM.jpg'),
('22345678', 'Vinland Saga', 250000, 'Kodansha Comics', 'assets/cover_buku/vinland.jpg'),
('23456789', 'Breaking Bad Habits', 120000, 'Karangan Creative Books', 'assets/cover_buku/breaking_bad.jpg'),
('34567890', 'Death Note', 200000, 'Viz Media', 'assets/cover_buku/death_note.jpg'),
('45678901', 'Fumetsu', 150000, 'Nova Press', 'assets/cover_buku/fumetsu.jpg'),
('56789012', 'Hujan', 90000, 'Gramedia Pustaka Utama', 'assets/cover_buku/hujan.jpg'),
('67890123', 'Metamorphosis', 120000, 'Penguin Classics', 'assets/cover_buku/metamorphosis.jpg'),
('78901234', 'Pulang', 85000, 'Indie Press', 'assets/cover_buku/pulang_tere_liye.jpg'),
('89012345', 'In Search of Time', 130000, 'Horizon Books', 'assets/cover_buku/search_time.jpg'),
('90123456', 'Sendiri', 75000, 'Rintik Press', 'assets/cover_buku/sendiri.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_nofak`
--

CREATE TABLE `tbl_nofak` (
  `id` int(11) NOT NULL,
  `no_faktur_akhir` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_nofak`
--

INSERT INTO `tbl_nofak` (`id`, `no_faktur_akhir`) VALUES
(1, 7);

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `No_Faktur` varchar(10) NOT NULL,
  `Tanggal` date DEFAULT NULL,
  `ISBN` varchar(20) NOT NULL,
  `Jumlah` int(3) DEFAULT NULL,
  `Total` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`No_Faktur`, `Tanggal`, `ISBN`, `Jumlah`, `Total`) VALUES
('F-0001', '2026-01-28', '34567890', 5, 800000),
('F-0002', '2026-01-28', '67890123', 2, 192000);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `buku`
--
ALTER TABLE `buku`
  ADD PRIMARY KEY (`ISBN`);

--
-- Indexes for table `tbl_nofak`
--
ALTER TABLE `tbl_nofak`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`No_Faktur`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
