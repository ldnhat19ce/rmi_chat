-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: chatroom
-- ------------------------------------------------------
-- Server version	8.0.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `discussion`
--

DROP TABLE IF EXISTS `discussion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussion` (
  `discussion_id` int NOT NULL AUTO_INCREMENT,
  `discussion_name` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`discussion_id`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussion`
--

LOCK TABLES `discussion` WRITE;
/*!40000 ALTER TABLE `discussion` DISABLE KEYS */;
INSERT INTO `discussion` VALUES (125,'lenhat_minhphuc');
/*!40000 ALTER TABLE `discussion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discussion_detail`
--

DROP TABLE IF EXISTS `discussion_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussion_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `discussion_id` int DEFAULT NULL,
  `type` enum('GROUP','PRIVATE') COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `fk_dc_d_idx` (`discussion_id`),
  KEY `dk_dc_u_idx` (`user_id`),
  CONSTRAINT `dk_dc_u` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `fk_dc_d` FOREIGN KEY (`discussion_id`) REFERENCES `discussion` (`discussion_id`)
) ENGINE=InnoDB AUTO_INCREMENT=290 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussion_detail`
--

LOCK TABLES `discussion_detail` WRITE;
/*!40000 ALTER TABLE `discussion_detail` DISABLE KEYS */;
INSERT INTO `discussion_detail` VALUES (288,10,125,'PRIVATE'),(289,11,125,'PRIVATE');
/*!40000 ALTER TABLE `discussion_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8_unicode_ci,
  `status` int DEFAULT NULL,
  `discussion_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `create_date` timestamp NULL DEFAULT NULL,
  `modify_date` timestamp NULL DEFAULT NULL,
  `type` enum('FILE','SMS','ICON','YT') COLLATE utf8_unicode_ci DEFAULT NULL,
  `discussion_type` enum('GROUP','PRIVATE') COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_msg_us_idx` (`user_id`),
  KEY `fk_msg_dis_idx` (`discussion_id`),
  CONSTRAINT `fk_msg_dis` FOREIGN KEY (`discussion_id`) REFERENCES `discussion` (`discussion_id`),
  CONSTRAINT `fk_msg_us` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=501 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (494,'xin chafo',1,125,10,'2022-01-16 07:00:27',NULL,'SMS',NULL),(495,'tét',1,125,11,'2022-01-16 07:00:32',NULL,'SMS',NULL),(496,'D:\\O_E\\icon\\ic1\\3.png',1,125,11,'2022-01-16 07:00:35',NULL,'ICON',NULL),(497,'D:\\O_E\\icon\\ic1\\4.png',1,125,10,'2022-01-16 07:00:42',NULL,'ICON',NULL),(498,'96_14.png',1,125,10,'2022-01-16 07:00:52',NULL,'FILE',NULL),(499,'268466078_1569997943361127_2365030191722166350_n_15.jpg',1,125,11,'2022-01-16 07:01:10',NULL,'FILE',NULL),(500,'x',1,125,11,'2022-01-16 07:01:22',NULL,'SMS',NULL);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sticker_detail`
--

DROP TABLE IF EXISTS `sticker_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sticker_detail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url` text COLLATE utf8_unicode_ci,
  `sticker_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_sk_skd_idx` (`sticker_id`),
  CONSTRAINT `fk_sk_skd` FOREIGN KEY (`sticker_id`) REFERENCES `stickers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sticker_detail`
--

LOCK TABLES `sticker_detail` WRITE;
/*!40000 ALTER TABLE `sticker_detail` DISABLE KEYS */;
INSERT INTO `sticker_detail` VALUES (1,'D:\\O_E\\icon\\ic1\\12.png',1),(2,'D:\\O_E\\icon\\ic1\\1.png',1),(3,'D:\\O_E\\icon\\ic1\\2.png',1),(4,'D:\\O_E\\icon\\ic1\\3.png',1),(5,'D:\\O_E\\icon\\ic1\\4.png',1),(6,'D:\\O_E\\icon\\ic1\\5.png',1),(7,'D:\\O_E\\icon\\ic1\\6.png',1),(8,'D:\\O_E\\icon\\ic1\\7.png',1),(9,'D:\\O_E\\icon\\ic1\\8.png',1),(10,'D:\\O_E\\icon\\ic1\\9.png',1),(11,'D:\\O_E\\icon\\ic1\\10.png',1),(12,'D:\\O_E\\icon\\ic1\\11.png',1);
/*!40000 ALTER TABLE `sticker_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stickers`
--

DROP TABLE IF EXISTS `stickers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stickers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stickers`
--

LOCK TABLES `stickers` WRITE;
/*!40000 ALTER TABLE `stickers` DISABLE KEYS */;
INSERT INTO `stickers` VALUES (1,'ic1');
/*!40000 ALTER TABLE `stickers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(55) COLLATE utf8_unicode_ci DEFAULT NULL,
  `username` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `image` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (9,'vku','vku','123456','/assets/user_picture.png'),(10,'Lê Đức Nhật','lenhat','123456','D:\\test_chat\\picture\\fsbS3A_1.jpg'),(11,'Từ Lê Minh Phúc','minhphuc','123456','D:\\test_chat\\picture\\icon_1_2.png'),(12,'Đỗ Quang Pháp','quangphap','123456','/assets/user_picture.png'),(14,'Lê Thiện Nhân','thiennhan','123456','/assets/user_picture.png');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'chatroom'
--

--
-- Dumping routines for database 'chatroom'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-01-16 20:28:24
