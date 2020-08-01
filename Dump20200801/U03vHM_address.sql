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
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `addressId` int(10) NOT NULL AUTO_INCREMENT,
  `address` varchar(50) NOT NULL,
  `address2` varchar(50) NOT NULL,
  `cityId` int(10) NOT NULL,
  `postalCode` varchar(10) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `createDate` datetime NOT NULL,
  `createdBy` varchar(40) NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastUpdateBy` varchar(40) NOT NULL,
  PRIMARY KEY (`addressId`),
  KEY `cityId` (`cityId`),
  CONSTRAINT `address_ibfk_1` FOREIGN KEY (`cityId`) REFERENCES `city` (`cityId`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'1600 Pennsylvania Ave NW','Oval Office',1,'20500','(202) 456-1414','2019-11-25 11:31:45','test','2019-11-25 11:31:45','test'),(2,'191 N 1st St','Department 1',1,'95113','(408) 882-2100','2019-11-26 09:55:44','test','2019-11-26 09:55:44','test'),(3,'5601 Fishers Lane','MSC 9806',8,'20892','866-284-4107','2020-01-19 12:02:29','test','2020-01-19 12:02:29','test'),(7,'asdfasdf','',29,'','','2020-07-20 03:06:42','test','2020-07-20 03:06:42','test'),(8,'1234 abcdef','',30,'','','2020-07-22 00:55:52','test','2020-07-22 00:55:52','test'),(9,'1234c wwww','',31,'','','2020-07-22 01:37:59','test','2020-07-22 01:37:59','test'),(11,'rtyrtyrtyrtyfghfgh','',35,'','','2020-07-22 20:37:49','test','2020-07-22 20:37:49','test'),(12,'f','fwefwefw',35,'','','2020-07-23 04:24:27','test','2020-07-23 04:28:37','test'),(13,'Alexanderpl. 5-7','',3,'10178','+49 30 13572462','2020-07-26 00:19:22','test','2020-07-26 00:19:22','test'),(14,'405 Lexington Ave','MS 4428',36,'10174','(212) 447-1836','2020-07-26 00:19:22','test','2020-07-26 00:19:22','test'),(16,'Road No.28b Sion East','Sion Mumbai',38,'400022','+91 22 2502 2348','2020-07-26 00:19:23','test','2020-07-26 00:19:23','test'),(17,'15 Calle','',39,'','+502 6854 2872','2020-07-26 00:19:23','test','2020-07-26 00:19:23','test'),(18,'10 Upper Bank Street','Suite 3210',40,'E14 5NP','+44 7825 278103','2020-07-26 00:19:23','test','2020-07-26 00:19:23','test'),(22,'127 N 2nd St','',44,'85004','(602) 440-1131','2020-07-26 02:09:13','deepstate','2020-07-26 02:31:05','deepstate'),(23,'245 E. Olive Avenue','#200',45,'91502','(323) 315-5555','2020-07-26 02:14:41','deepstate','2020-07-26 02:14:41','deepstate'),(27,'127 N 2nd St','',44,'85004','(602) 440-1132','2020-07-26 03:25:46','test','2020-07-26 03:25:46','test'),(28,'1947 Pont Arago','',46,'36000','','2020-08-01 10:01:15','test','2020-08-01 10:01:15','test'),(29,'Carl-Loewe-Weg 15','',47,'37154','','2020-08-01 10:04:46','test','2020-08-01 10:04:46','test'),(30,'554 Stanma Ln','',48,'KY13 9LB','','2020-08-01 10:07:34','test','2020-08-01 10:07:34','test');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-08-01  6:36:19
