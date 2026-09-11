-- Database Schema & Sample Data for Library RMI System (lib_rmi_db)
CREATE DATABASE IF NOT EXISTS `lib_rmi_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `lib_rmi_db`;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Table: author
DROP TABLE IF EXISTS `author`;
CREATE TABLE `author` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Table: category
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Table: published (Publisher)
DROP TABLE IF EXISTS `published`;
CREATE TABLE `published` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Table: book
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `category_id` INT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_book_category_idx` (`category_id`),
  CONSTRAINT `FK_book_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Table: book_author
DROP TABLE IF EXISTS `book_author`;
CREATE TABLE `book_author` (
  `book_id` INT NOT NULL,
  `author_id` INT NOT NULL,
  PRIMARY KEY (`book_id`, `author_id`),
  KEY `FK_ba_author_idx` (`author_id`),
  CONSTRAINT `FK_ba_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_ba_author` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Table: book_copy
DROP TABLE IF EXISTS `book_copy`;
CREATE TABLE `book_copy` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `year_published` INT NOT NULL,
  `book_id` INT NOT NULL,
  `published_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bc_book_idx` (`book_id`),
  KEY `FK_bc_published_idx` (`published_id`),
  CONSTRAINT `FK_bc_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_bc_published` FOREIGN KEY (`published_id`) REFERENCES `published` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Table: patron_account
DROP TABLE IF EXISTS `patron_account`;
CREATE TABLE `patron_account` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(255) NOT NULL,
  `last_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT '1',
  `role` VARCHAR(50) NOT NULL DEFAULT 'PATRON',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Table: checkout
DROP TABLE IF EXISTS `checkout`;
CREATE TABLE `checkout` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `start_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_returned` TINYINT NOT NULL DEFAULT '0',
  `patron_id` INT NOT NULL,
  `book_copy_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_checkout_patron_idx` (`patron_id`),
  KEY `FK_checkout_book_copy_idx` (`book_copy_id`),
  CONSTRAINT `FK_checkout_patron` FOREIGN KEY (`patron_id`) REFERENCES `patron_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_checkout_book_copy` FOREIGN KEY (`book_copy_id`) REFERENCES `book_copy` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Table: hold
DROP TABLE IF EXISTS `hold`;
CREATE TABLE `hold` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `start_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `patron_id` INT NOT NULL,
  `book_copy_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_hold_patron_idx` (`patron_id`),
  KEY `FK_hold_book_copy_idx` (`book_copy_id`),
  CONSTRAINT `FK_hold_patron` FOREIGN KEY (`patron_id`) REFERENCES `patron_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_hold_book_copy` FOREIGN KEY (`book_copy_id`) REFERENCES `book_copy` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Table: notification
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `sent_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `message` TEXT NOT NULL,
  `patron_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_notification_patron_idx` (`patron_id`),
  CONSTRAINT `FK_notification_patron` FOREIGN KEY (`patron_id`) REFERENCES `patron_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Table: log
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `ip` VARCHAR(255) NOT NULL,
  `username` VARCHAR(255) NOT NULL DEFAULT 'Manage',
  `table_name` VARCHAR(45) NOT NULL,
  `col_id` INT NOT NULL,
  `time_start` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `time_end` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Table: db_log
DROP TABLE IF EXISTS `db_log`;
CREATE TABLE `db_log` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `table_name` VARCHAR(255) NOT NULL,
  `action` ENUM('INSERT','UPDATE','DELETE') NOT NULL,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ========================================================
-- TRIGGERS FOR DB_LOG
-- ========================================================
DELIMITER $$

CREATE TRIGGER `trg_db_log_insert_author` AFTER INSERT ON `author` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('author', 'INSERT');
END$$
CREATE TRIGGER `trg_db_log_update_author` AFTER UPDATE ON `author` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('author', 'UPDATE');
END$$
CREATE TRIGGER `trg_db_log_delete_author` AFTER DELETE ON `author` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('author', 'DELETE');
END$$

CREATE TRIGGER `trg_db_log_insert_category` AFTER INSERT ON `category` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('category', 'INSERT');
END$$
CREATE TRIGGER `trg_db_log_update_category` AFTER UPDATE ON `category` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('category', 'UPDATE');
END$$
CREATE TRIGGER `trg_db_log_delete_category` AFTER DELETE ON `category` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('category', 'DELETE');
END$$

CREATE TRIGGER `trg_db_log_insert_book` AFTER INSERT ON `book` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('book', 'INSERT');
END$$
CREATE TRIGGER `trg_db_log_update_book` AFTER UPDATE ON `book` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('book', 'UPDATE');
END$$
CREATE TRIGGER `trg_db_log_delete_book` AFTER DELETE ON `book` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('book', 'DELETE');
END$$

CREATE TRIGGER `trg_db_log_insert_book_copy` AFTER INSERT ON `book_copy` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('book_copy', 'INSERT');
END$$
CREATE TRIGGER `trg_db_log_update_book_copy` AFTER UPDATE ON `book_copy` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('book_copy', 'UPDATE');
END$$
CREATE TRIGGER `trg_db_log_delete_book_copy` AFTER DELETE ON `book_copy` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('book_copy', 'DELETE');
END$$

CREATE TRIGGER `trg_db_log_insert_published` AFTER INSERT ON `published` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('published', 'INSERT');
END$$
CREATE TRIGGER `trg_db_log_update_published` AFTER UPDATE ON `published` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('published', 'UPDATE');
END$$
CREATE TRIGGER `trg_db_log_delete_published` AFTER DELETE ON `published` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('published', 'DELETE');
END$$

CREATE TRIGGER `trg_db_log_insert_patron_account` AFTER INSERT ON `patron_account` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('patron_account', 'INSERT');
END$$
CREATE TRIGGER `trg_db_log_update_patron_account` AFTER UPDATE ON `patron_account` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('patron_account', 'UPDATE');
END$$
CREATE TRIGGER `trg_db_log_delete_patron_account` AFTER DELETE ON `patron_account` FOR EACH ROW BEGIN
  INSERT INTO `db_log` (`table_name`, `action`) VALUES ('patron_account', 'DELETE');
END$$

DELIMITER ;

-- ========================================================
-- SAMPLE DATA INSERTIONS
-- ========================================================

-- 1. Insert Categories
INSERT INTO `category` (`id`, `name`) VALUES
(1, 'Công nghệ thông tin'),
(2, 'Văn học Việt Nam'),
(3, 'Văn học Thế giới'),
(4, 'Kinh tế & Quản trị'),
(5, 'Tâm lý & Kỹ năng sống'),
(6, 'Khoa học & Vũ trụ'),
(7, 'Lịch sử & Địa lý'),
(8, 'Triết học & Lý luận');

-- 2. Insert Authors
INSERT INTO `author` (`id`, `name`) VALUES
(1, 'Nguyễn Nhật Ánh'),
(2, 'Nam Cao'),
(3, 'Tô Hoài'),
(4, 'Robert Kiyosaki'),
(5, 'Dale Carnegie'),
(6, 'Walter Isaacson'),
(7, 'J.K. Rowling'),
(8, 'Haruki Murakami'),
(9, 'George Orwell'),
(10, 'Paulo Coelho');

-- 3. Insert Publishers
INSERT INTO `published` (`id`, `name`) VALUES
(1, 'NXB Trẻ'),
(2, 'NXB Kim Đồng'),
(3, 'NXB Nhã Nam'),
(4, 'NXB Hội Nhà Văn'),
(5, 'NXB Tổng Hợp TP.HCM'),
(6, 'NXB Lao Động'),
(7, 'NXB O\'Reilly Media');

-- 4. Insert Books
INSERT INTO `book` (`id`, `title`, `category_id`) VALUES
(1, 'Mắt Biếc', 2),
(2, 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 2),
(3, 'Dế Mèn Phiêu Lưu Ký', 2),
(4, 'Chí Phèo', 2),
(5, 'Dạy Con Làm Giàu (Rich Dad Poor Dad)', 4),
(6, 'Đắc Nhân Tâm (How to Win Friends)', 5),
(7, 'Steve Jobs - Tiểu Sử', 6),
(8, 'Harry Potter và Hòn Đá Phù Thủy', 3),
(9, 'Rừng Na Uy (Norwegian Wood)', 3),
(10, '1984', 3),
(11, 'Nhà Giả Kim (The Alchemist)', 5),
(12, 'Lập Trình Java Từ Căn Bản Đến Nâng Cao', 1);

-- 5. Insert Book-Author Relationships
INSERT INTO `book_author` (`book_id`, `author_id`) VALUES
(1, 1),
(2, 1),
(3, 3),
(4, 2),
(5, 4),
(6, 5),
(7, 6),
(8, 7),
(9, 8),
(10, 9),
(11, 10),
(12, 6);

-- 6. Insert Book Copies (Physical Copies in Library)
INSERT INTO `book_copy` (`id`, `year_published`, `book_id`, `published_id`) VALUES
(1, 2020, 1, 1),
(2, 2022, 1, 1),
(3, 2021, 2, 1),
(4, 2019, 3, 2),
(5, 2018, 4, 4),
(6, 2021, 5, 5),
(7, 2022, 6, 6),
(8, 2020, 7, 3),
(9, 2023, 8, 2),
(10, 2019, 9, 3),
(11, 2021, 10, 3),
(12, 2022, 11, 3),
(13, 2024, 12, 7);

-- 7. Insert Patron & Admin Accounts
INSERT INTO `patron_account` (`id`, `first_name`, `last_name`, `email`, `password`, `status`, `role`) VALUES
(1, 'System', 'Admin', 'admin@library.com', 'admin123', 1, 'ADMIN'),
(2, 'Nguyễn Huệ', 'Minh', 'minh@gmail.com', '123456', 1, 'PATRON'),
(3, 'Trần Văn', 'Nam', 'nam.tran@gmail.com', '123456', 1, 'PATRON'),
(4, 'Lê Thị', 'Hoa', 'hoa.le@gmail.com', '123456', 1, 'PATRON'),
(5, 'Phạm Hoàng', 'Long', 'long.pham@gmail.com', '123456', 1, 'PATRON'),
(6, 'Vũ Thị', 'Mai', 'mai.vu@gmail.com', '123456', 1, 'PATRON');

-- 8. Insert Sample Checkout Records
INSERT INTO `checkout` (`id`, `start_time`, `end_time`, `is_returned`, `patron_id`, `book_copy_id`) VALUES
(1, '2026-09-01 08:30:00', '2026-09-08 08:30:00', 1, 2, 1),
(2, '2026-09-05 10:15:00', '2026-09-12 10:15:00', 0, 2, 3),
(3, '2026-09-06 14:00:00', '2026-09-13 14:00:00', 0, 3, 6),
(4, '2026-09-07 09:45:00', '2026-09-14 09:45:00', 0, 4, 9);

-- 9. Insert Sample Hold Records
INSERT INTO `hold` (`id`, `start_time`, `end_time`, `patron_id`, `book_copy_id`) VALUES
(1, '2026-09-08 11:00:00', '2026-09-15 11:00:00', 5, 8);

-- 10. Insert Sample Notifications
INSERT INTO `notification` (`id`, `sent_at`, `message`, `patron_id`) VALUES
(1, '2026-09-05 10:16:00', 'Chào mừng bạn đến với thư viện VKU Library! Phiếu mượn sách Mắt Biếc đã được phê duyệt.', 2),
(2, '2026-09-06 14:01:00', 'Sách Dạy Con Làm Giàu bạn đăng ký đã sẵn sàng để nhận tại quầy thư viện.', 3);
