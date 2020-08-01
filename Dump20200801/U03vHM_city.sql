-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: 3.227.166.251    Database: U03vHM
-- ------------------------------------------------------
-- Server version	5.7.31-0ubuntu0.18.04.1

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
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `city` (
  `cityId` int(10) NOT NULL AUTO_INCREMENT,
  `city` varchar(50) NOT NULL,
  `countryId` int(10) NOT NULL,
  `createDate` datetime NOT NULL,
  `createdBy` varchar(40) NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastUpdateBy` varchar(40) NOT NULL,
  PRIMARY KEY (`cityId`),
  KEY `countryId` (`countryId`),
  CONSTRAINT `city_ibfk_1` FOREIGN KEY (`countryId`) REFERENCES `country` (`countryId`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (1,'Washington, DC',1,'2019-11-25 11:31:22','test','2020-07-10 17:33:41','test'),(2,'San Jose, CA',1,'2019-11-26 09:55:25','test','2020-07-10 17:33:41','test'),(3,'Berlin',2,'2019-11-27 16:43:26','test','2020-07-10 17:33:41','test'),(4,'Armstedt',2,'2019-11-27 16:43:39','test','2020-07-10 17:33:41','test'),(5,'New Delhi',3,'2019-11-27 16:44:19','test','2020-07-10 17:33:41','test'),(6,'Bangalore',3,'2019-11-27 16:44:38','test','2020-07-10 17:33:41','test'),(7,'Vieques',4,'2019-11-27 16:45:19','test','2020-07-10 17:33:41','test'),(8,'Bathesda, MD',1,'2020-01-19 12:00:05','test','2020-07-10 17:33:41','test'),(28,'adfasdf',32,'2020-07-20 03:04:59','test','2020-07-20 03:04:59','test'),(29,'erwerwerw',33,'2020-07-20 03:06:16','test','2020-07-20 03:06:16','test'),(30,'asdf',31,'2020-07-22 00:55:00','test','2020-07-22 00:55:52','test'),(31,'hhhh',31,'2020-07-22 01:37:00','test','2020-07-22 01:37:59','test'),(32,'erree',31,'2020-07-22 19:47:06','test','2020-07-22 19:47:06','test'),(35,'rtututur',36,'2020-07-22 20:36:36','test','2020-07-22 20:36:36','test'),(36,'New York',1,'2020-07-26 00:19:22','test','2020-07-26 00:19:22','test'),(38,'Mumbai',3,'2020-07-26 00:19:23','test','2020-07-26 00:19:23','test'),(39,'Guatemala City',37,'2020-07-26 00:19:23','test','2020-07-26 00:19:23','test'),(40,'London',38,'2020-07-26 00:19:23','test','2020-07-26 00:19:23','test'),(44,'Phoenix',1,'2020-07-26 02:09:13','deepstate','2020-07-26 02:09:13','deepstate'),(45,'Burbank, California',1,'2020-07-26 02:12:22','deepstate','2020-07-26 02:12:22','deepstate'),(46,'Châteauroux',39,'2020-08-01 10:01:07','test','2020-08-01 10:01:07','test'),(47,'Northeim',2,'2020-08-01 10:04:34','test','2020-08-01 10:04:34','test'),(48,'Kinross',38,'2020-08-01 10:07:24','test','2020-08-01 10:07:24','test');
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-08-01  6:36:18
