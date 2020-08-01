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
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointment` (
  `appointmentId` int(10) NOT NULL AUTO_INCREMENT,
  `customerId` int(10) NOT NULL,
  `userId` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `location` text NOT NULL,
  `contact` text NOT NULL,
  `type` text NOT NULL,
  `url` varchar(255) NOT NULL,
  `start` datetime NOT NULL,
  `end` datetime NOT NULL,
  `createDate` datetime NOT NULL,
  `createdBy` varchar(40) NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastUpdateBy` varchar(40) NOT NULL,
  PRIMARY KEY (`appointmentId`),
  KEY `userId` (`userId`),
  KEY `appointment_ibfk_1` (`customerId`),
  CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `customer` (`customerId`),
  CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointment`
--

LOCK TABLES `appointment` WRITE;
/*!40000 ALTER TABLE `appointment` DISABLE KEYS */;
INSERT INTO `appointment` VALUES (1,1,1,'First Event','Event 1 DESC','Undisclosed','Agent Dan','other','','2019-12-03 05:00:00','2019-12-03 06:00:00','2019-11-25 11:35:10','test','2020-04-09 23:18:25','test'),(7,3,1,'Example phone meeting','','866-284-4107','Hu Won Receiptionista','phone','','2020-05-05 07:00:00','2020-05-05 08:00:00','2020-05-04 20:29:15','test','2020-05-04 20:29:15','test'),(8,1,1,'Example virtual meeting','Test','http://mysite.com','Victor Tango Charlie','virtual','http://mysite.com','2020-05-05 07:00:00','2020-05-05 08:00:00','2020-05-04 20:31:04','test','2020-05-04 20:31:04','test'),(9,2,1,'Another test virtual meeting','','https://yoursite.com','','virtual','https://yoursite.com','2020-05-04 13:00:00','2020-05-04 14:00:00','2020-05-04 21:21:11','test','2020-05-04 21:21:11','test'),(11,1,1,'Vacation 2020','Planning','Houston','Mike','other','http://mysite.com','2020-05-05 06:00:00','2020-05-05 07:00:00','2020-05-05 17:10:19','test','2020-05-05 17:15:42','test'),(12,1,1,'Benefits 2020','Changes','1455896.','Lou','phone','http://mysite.com','2020-05-13 08:00:00','2020-05-13 09:00:00','2020-05-05 17:14:42','test','2020-05-05 17:16:45','test'),(13,5,2,'asdf','','wefwef','wefwef','other','','2020-07-26 12:00:00','2020-07-26 13:00:00','2020-07-26 04:15:13','test','2020-07-26 04:18:19','test'),(14,1,2,'asdf','','asdf','asdf','other','','2020-07-26 16:00:00','2020-07-26 17:00:00','2020-07-27 01:52:43','test','2020-07-27 01:52:43','test'),(15,2,1,'asdfasdf','','asdf','asdfasdf','other','','2020-07-27 04:00:00','2020-07-27 05:00:00','2020-07-27 02:55:00','test','2020-07-27 02:55:00','test'),(16,1,2,'Test for alert','','QWE','asdASD','other','','2020-07-30 08:05:00','2020-07-30 09:05:00','2020-07-30 08:03:18','test','2020-07-30 08:03:39','test'),(17,2,1,'asdfadf','','asdfadf','adfasdf','other','','2020-07-30 09:05:00','2020-07-30 10:05:00','2020-07-30 08:56:10','test','2020-07-30 08:56:10','test');
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
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
