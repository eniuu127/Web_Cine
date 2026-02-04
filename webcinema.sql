-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: localhost    Database: webcinema
-- ------------------------------------------------------
-- Server version	8.0.29

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
-- Table structure for table `booking_items`
--

DROP TABLE IF EXISTS `booking_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_items` (
  `booking_item_id` bigint NOT NULL AUTO_INCREMENT,
  `booking_id` bigint NOT NULL,
  `seat_id` bigint NOT NULL,
  PRIMARY KEY (`booking_item_id`),
  UNIQUE KEY `uk_booking_seat` (`booking_id`,`seat_id`),
  KEY `idx_bi_booking` (`booking_id`),
  KEY `idx_bi_seat` (`seat_id`),
  CONSTRAINT `fk_bi_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
  CONSTRAINT `fk_bi_seat` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`seat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_items`
--

LOCK TABLES `booking_items` WRITE;
/*!40000 ALTER TABLE `booking_items` DISABLE KEYS */;
INSERT INTO `booking_items` VALUES (1,4,4),(2,5,5),(3,6,6),(4,7,7),(5,8,8),(6,9,47),(7,9,57),(8,9,67),(9,10,57),(10,10,58),(11,11,48),(12,12,47),(13,12,48),(14,13,9),(15,14,48),(16,15,70),(17,16,50),(18,17,59),(19,18,58),(20,19,60),(21,20,70),(22,21,69),(23,22,67),(24,23,49),(25,23,50),(26,24,47),(27,24,48),(28,25,60),(29,26,56),(30,27,57),(31,28,58),(32,29,54),(33,30,49),(34,31,64),(35,32,68),(36,33,69),(37,34,64),(38,35,69),(39,36,67),(40,36,68),(41,37,57),(42,37,58),(43,38,64),(44,39,65),(45,40,68),(46,40,69),(47,41,66),(48,42,47),(49,43,57),(50,43,67),(51,44,56),(52,45,55),(53,46,62),(54,46,63),(55,47,58),(56,48,16),(57,49,26),(58,50,27),(59,51,28),(60,52,66),(61,53,67),(62,54,65),(63,55,66),(64,56,64),(65,57,2),(66,57,10),(67,58,58);
/*!40000 ALTER TABLE `booking_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `booking_id` bigint NOT NULL AUTO_INCREMENT,
  `booking_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_amount` int NOT NULL,
  `showtime_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `hold_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `payment_method_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guest_mail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`booking_id`),
  UNIQUE KEY `uk_booking_code` (`booking_code`),
  KEY `fk_booking_showtime` (`showtime_id`),
  KEY `fk_booking_user` (`user_id`),
  KEY `fk_booking_payment_method` (`payment_method_id`),
  CONSTRAINT `fk_booking_payment_method` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_methods` (`code`),
  CONSTRAINT `fk_booking_showtime` FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`showtime_id`),
  CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,'A0A1B15F','2026-01-27 16:01:10.646597',NULL,'PENDING',240000,1,1,'334d52d0407d4ceb9f5cc28a1049cf0d',NULL,NULL,NULL),(2,'C92F0458','2026-01-27 16:25:27.746662','2026-01-27 16:30:27.741670','CANCELLED',240000,1,1,'77aa6e5bb99e45eba897149d730deea0',NULL,NULL,NULL),(3,'DF57923A','2026-01-27 16:58:13.218777','2026-01-27 17:03:13.209934','PAID',240000,1,1,'a3b314c1a61d48b8a6da1bced2807a12',NULL,NULL,'2026-01-27 16:58:13.218777'),(4,'511EFA73','2026-01-27 21:45:01.106875','2026-01-27 21:50:01.089583','CANCELLED',80000,1,1,'1a3959ced6c74083b2fc20c0e8c390bf',NULL,NULL,NULL),(5,'B3833563','2026-01-27 22:03:28.230354','2026-01-27 22:08:28.226825','PAID',80000,1,1,'67a3c9c2eaeb471aafff49f1e134147a',NULL,NULL,'2026-01-27 22:03:28.230354'),(6,'C524CFC1','2026-01-27 22:33:56.922812','2026-01-27 22:38:56.919825','PAID',80000,1,1,'403dacd3b4d741508a228590e8d01e3f',NULL,NULL,'2026-01-27 22:33:56.922812'),(7,'13CCA20C','2026-01-27 22:35:40.713231','2026-01-27 22:40:40.709246','CANCELLED',80000,1,1,'fded814ecab343fbb8ce41e546fdaf3d','MOMO',NULL,NULL),(8,'42CDCDCD','2026-01-28 00:32:46.261816','2026-01-28 00:37:46.256603','PAID',80000,1,1,'f6d89ac305e7419f8eebde754ff2b84b',NULL,NULL,'2026-01-28 00:32:46.261816'),(9,'8319239E','2026-01-29 21:49:56.982502','2026-01-29 21:54:56.962378','CANCELLED',300000,2,8,'39e91bf896ca48cbba5a69412bb45793',NULL,NULL,NULL),(10,'F5ED905D','2026-01-29 22:00:16.162540','2026-01-29 22:05:16.134258','CANCELLED',200000,2,8,'3cdf1f3776b44fa6be61a412b654aac7',NULL,NULL,NULL),(11,'A8540238','2026-01-29 22:02:44.005025','2026-01-29 22:07:43.986806','CANCELLED',100000,2,8,'38a24201ff4a4cd59c342a2cd3735301',NULL,NULL,NULL),(12,'AA7BFEED','2026-01-29 22:12:27.025773','2026-01-29 22:17:27.021894','CANCELLED',200000,2,8,'c5d1478f0d8a44d0894662cf325a3fbd',NULL,NULL,NULL),(13,'37E43D95','2026-01-29 22:16:00.782506','2026-01-29 22:21:00.778961','CANCELLED',80000,1,8,'653b2dbb4b104f59b1d1e6da8d05d2ed',NULL,NULL,NULL),(14,'08B0D8C7','2026-01-29 22:20:25.654959','2026-01-29 22:25:25.651398','CANCELLED',100000,2,8,'9fefc5c73a9a4ba8abb2f3ec0ef8a7bb',NULL,NULL,NULL),(15,'B0F2C752','2026-01-29 22:26:13.549385','2026-01-29 22:31:13.544729','CANCELLED',100000,2,8,'7e4cb0fcd0e94870a9f7b88940a3942a',NULL,NULL,NULL),(16,'2A5DA1D0','2026-01-29 22:34:09.036082','2026-01-29 22:39:09.031327','CANCELLED',100000,2,8,'df05107b1a07461eb70bd135a826c91f',NULL,NULL,NULL),(17,'B768FC88','2026-01-29 22:36:10.452388','2026-01-29 22:41:10.447371','CANCELLED',100000,2,8,'3ce5e7dadb1c43b7a31b582a7d9190b4',NULL,NULL,NULL),(18,'3B2672DA','2026-01-29 22:37:35.994158','2026-01-29 22:42:35.988597','CANCELLED',100000,2,8,'2165e870bf7a4bb6b6d617bd24c9f01b',NULL,NULL,NULL),(19,'A3FE77E0','2026-01-29 22:38:23.910894','2026-01-29 22:43:23.905360','CANCELLED',100000,2,8,'fe936ebaaf3345ee84c4640bb3546f99',NULL,NULL,NULL),(20,'1DADDBBF','2026-01-29 22:38:45.905770','2026-01-29 22:43:45.900224','CANCELLED',100000,2,8,'206fd46ab3d34670a11daf44e7a9ace3',NULL,NULL,NULL),(21,'C2A9629A','2026-01-29 22:40:05.089672','2026-01-29 22:45:05.086058','CANCELLED',100000,2,8,'d61cfd2d1d0948b6a466c3124b0deb57',NULL,NULL,NULL),(22,'649095E4','2026-01-29 22:42:41.278498','2026-01-29 22:47:41.269907','CANCELLED',100000,2,8,'1d953a04cc444e6e90d21cbc054d47ba',NULL,NULL,NULL),(23,'941C1EB5','2026-01-29 22:53:33.762999','2026-01-29 22:58:33.758398','CANCELLED',200000,2,8,'adc0a65a2e4942b1a19fda36ad6663d4',NULL,NULL,NULL),(24,'5A6C899B','2026-01-29 22:56:05.826155','2026-01-29 23:01:05.822139','CANCELLED',200000,2,8,'6592864b063b4faa86f5d44b268a36e0',NULL,NULL,NULL),(25,'8D45A107','2026-01-29 23:17:55.191668','2026-01-29 23:22:55.182764','CANCELLED',100000,2,8,'7d1e2212d12e4f04b9a1c8145e0c3de6',NULL,NULL,NULL),(26,'792A9453','2026-01-30 08:24:55.419346','2026-01-30 08:29:55.401810','CANCELLED',100000,2,8,'46c2c696b093448099f9d166a0618baa',NULL,NULL,NULL),(27,'691B0564','2026-01-30 08:27:24.124108','2026-01-30 08:32:24.113746','CANCELLED',100000,2,8,'7a236b1e664f4f83bb5de7653e7451b7',NULL,NULL,NULL),(28,'32A72217','2026-01-30 08:28:37.822116','2026-01-30 08:38:37.814999','CANCELLED',100000,2,8,'cb3848dc649a4560b794f4a760e6f1a0',NULL,NULL,NULL),(29,'325E26BB','2026-01-30 08:29:14.375127','2026-01-30 08:39:14.372129','CANCELLED',100000,2,8,'40545024441b4c8b9d72bfa44d289898',NULL,NULL,NULL),(30,'B2246418','2026-01-30 08:42:16.796984','2026-01-30 08:52:16.780095','CANCELLED',100000,2,8,'bb7fd47d3f7841899f80fc929a2bb68d',NULL,NULL,NULL),(31,'68961AC0','2026-01-30 08:55:12.122043','2026-01-30 09:05:12.119973','CANCELLED',100000,2,8,'5314307cd38841199edf68de5306c72f',NULL,NULL,NULL),(32,'35C55A76','2026-01-30 08:58:05.934749','2026-01-30 09:08:05.930870','CANCELLED',100000,2,8,'e556b9a4eee04248886463474ab186f9',NULL,NULL,NULL),(33,'93111AD3','2026-01-30 09:11:51.685577','2026-01-30 09:21:51.683575','CANCELLED',100000,2,8,'ccae9d2080b741bf9f83367ba12dc748',NULL,NULL,NULL),(34,'C59B6E29','2026-01-30 09:28:37.043995','2026-01-30 09:38:37.039034','CANCELLED',100000,2,8,'54989e2c5da945ad9e8b81e7cf49eb3e',NULL,NULL,NULL),(35,'BA6E470F','2026-01-30 09:30:19.079699','2026-01-30 09:40:19.076611','CANCELLED',100000,2,8,'d39dcdbdd7cc49199cbdd4c2bf6f7bb0',NULL,NULL,NULL),(36,'CBD74567','2026-01-30 10:02:59.730330','2026-01-30 10:12:59.728311','CANCELLED',200000,2,8,'09e8d0bdbca24fe7aec88101e8b0a58a','BANK',NULL,NULL),(37,'BE2EBE4E','2026-01-30 10:45:47.261611','2026-01-30 10:55:47.257612','CANCELLED',200000,2,8,'2cfe80cd9d434b9790fd304abae96418','MOMO',NULL,NULL),(38,'547B1170','2026-01-30 11:16:15.164694','2026-01-30 11:26:15.162731','PAID',100000,2,8,'3b4cba233ca049d19ad02cefdc2a8414','VNPAY',NULL,'2026-01-30 11:16:15.164694'),(39,'19AF8AB3','2026-01-30 11:19:24.200854','2026-01-30 11:29:24.197859','PAID',100000,2,8,'187ea9c52c7f4eda9172ec8af789401b','BANK',NULL,'2026-01-30 11:19:24.200854'),(40,'AB38FA5C','2026-01-30 11:19:45.035300','2026-01-30 11:29:45.033235','PAID',200000,2,8,'d1d122da2da64e98af2cd311f15c3eaf','MOMO',NULL,'2026-01-30 11:19:45.035300'),(41,'5BAE35AC','2026-01-30 11:20:49.211901','2026-01-30 11:30:49.207901','PAID',100000,2,8,'d6cef3c593324ea095db3b4a748d169f','MOMO',NULL,'2026-01-30 11:20:49.211901'),(42,'6C68AA5A','2026-01-30 11:24:35.827295','2026-01-30 11:34:35.824281','PAID',100000,2,8,'360d096567da46f6b2cac7621c3acfe4','MOMO',NULL,'2026-01-30 11:24:35.827295'),(43,'C65782DB','2026-01-30 11:30:05.135352','2026-01-30 11:40:05.133294','PAID',200000,2,8,'f8544699fc7f4f43a4f8c4a94d92f3da','MOMO',NULL,'2026-01-30 11:30:05.135352'),(44,'614F9473','2026-01-30 11:38:37.012853','2026-01-30 11:48:37.010771','CANCELLED',100000,2,8,'83e8e939ca8a404c84c28a216b588cf8','VNPAY',NULL,NULL),(45,'C7473417','2026-01-30 11:39:32.197714','2026-01-30 11:49:32.194708','CANCELLED',100000,2,8,'32aa14719c264a4ba3dc113e5b9c8472',NULL,NULL,NULL),(46,'557016F4','2026-01-30 13:19:12.791145','2026-01-30 13:29:12.775174','PAID',200000,2,8,'be317282c8184542a60d19ee950fc795','MOMO',NULL,'2026-01-30 13:19:12.791145'),(47,'35CBF03A','2026-01-31 02:01:35.558039','2026-01-31 02:11:35.547777','PAID',100000,2,8,'b6dbe8e2751a4f40b2a0d552eb01a28f','MOMO',NULL,'2026-01-31 02:01:35.558039'),(48,'D98D89D2','2026-01-31 16:02:52.686818','2026-01-31 16:12:52.677356','PAID',70000,3,8,'4f190d4f4d24490da486049f9896b772','MOMO',NULL,'2026-01-31 16:02:52.686818'),(49,'10C5F9CD','2026-01-31 17:41:15.301331','2026-01-31 17:51:15.296229','CANCELLED',70000,3,8,'98716425b96f47d1a9fbc863241bc42e',NULL,NULL,NULL),(50,'31FBB57D','2026-01-31 17:54:53.680429','2026-01-31 18:04:53.673421','CANCELLED',70000,3,8,'d0a500e39ce644a7a329ec664ffe287d',NULL,NULL,NULL),(51,'98C3939C','2026-01-31 17:55:29.424497','2026-01-31 18:05:29.420559','PAID',70000,3,8,'bd6ffd7a3be44380bbd2cc01db7175a0','MOMO',NULL,'2026-01-31 17:55:29.424497'),(52,'88050E86','2026-02-01 13:50:34.960243','2026-02-01 14:00:34.946211','CANCELLED',70000,4,9,'596d33c58e504cb5b220e981ad352193','MOMO',NULL,NULL),(53,'0CEE0827','2026-02-01 13:54:25.816394','2026-02-01 14:04:25.813401','CANCELLED',70000,4,9,'743f7f91b8054ffb859e29f8cac4eb4b','MOMO',NULL,NULL),(54,'2D1B09EB','2026-02-01 14:08:54.999034','2026-02-01 14:18:54.995526','PAID',70000,4,9,'cf8a04636b6e41f2abfc5e0804edb73b','MOMO',NULL,'2026-02-01 14:18:21.847575'),(55,'35BF732A','2026-02-01 14:20:02.835597','2026-02-01 14:30:02.832046','PAID',70000,4,9,'5f1f02a928af4361acf33170be2c0074','MOMO',NULL,'2026-02-01 14:20:07.649418'),(56,'7DFEDF62','2026-02-01 14:25:19.620397','2026-02-01 14:35:19.616352','PAID',70000,4,9,'37960bfece1e4bdc99303167cd625860','MOMO',NULL,'2026-02-01 14:25:22.459115'),(57,'7A18FC07','2026-02-03 11:59:30.728113','2026-02-03 12:09:30.720705','PAID',140000,3,9,'84a120c76cd5494fb0aabf7c235a25f9','MOMO',NULL,'2026-02-03 11:59:33.862312'),(58,'E331DF24','2026-02-03 12:05:02.841836','2026-02-03 12:15:02.839672','PAID',70000,6,9,'ebb28bd49c864debbeab6391d29cc04e','VNPAY',NULL,'2026-02-03 12:05:06.593922');
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movies`
--

DROP TABLE IF EXISTS `movies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movies` (
  `movie_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `poster_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `release_date` date DEFAULT NULL,
  `runtime` int NOT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `trailer_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`movie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movies`
--

LOCK TABLES `movies` WRITE;
/*!40000 ALTER TABLE `movies` DISABLE KEYS */;
INSERT INTO `movies` VALUES (1,'2026-01-15 09:46:44.258852','Phim hoạt hình gia đình','https://cdn-i.doisongphapluat.com.vn/media/dang-nhat-duy/2022/12/03/poster-phim-tran-thanh-nha-ba-nu-dspl-31220221.jpg','2024-06-01',105,'NOW_SHOWING','Nhà Bà Nữ','https://www.youtube.com/embed/IkaP0KJWTsQ'),(2,'2026-01-15 09:46:44.346853','Siêu anh hùng Marvel','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/3/5/350x495-tdm_2.jpg','2019-04-26',180,'NOW_SHOWING','Thiên Đường Máu','https://www.youtube.com/embed/46ASchtBIbE'),(3,'2026-01-31 01:04:24.612512',NULL,'https://i.redd.it/vmd1olc0ybzf1.jpeg',NULL,120,'NOW_SHOWING','Taxi Driver 3','https://www.youtube.com/embed/LkvvGGQcAAo'),(4,'2026-01-31 01:05:57.192356',NULL,'https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/p/o/poster_conan_qua_bom_choc_troi_6.jpg',NULL,95,'NOW_SHOWING','Thám Tử Lừng Danh Conan: Quả Bom Chọc Trời','https://www.youtube.com/embed/xatTgqiFABQ'),(5,'2026-01-31 01:07:41.905206',NULL,'https://upload.wikimedia.org/wikipedia/vi/b/b5/Alchemy_of_Souls.jpg',NULL,120,'NOW_SHOWING','Hoàn Hồn 1','https://www.youtube.com/embed/eWo85njHr6c'),(6,'2026-01-31 01:09:04.898740',NULL,'https://pub.edu.vn/upload/2025/10/than-den-oi-uoc-di-thumb.webp',NULL,120,'NOW_SHOWING','Thần Đèn Ơi, Ước Đi','https://www.youtube.com/embed/glQBKd2aIxc'),(7,'2026-01-31 01:14:03.159915',NULL,'https://bloganchoi.com/wp-content/uploads/2022/09/bong-dung-trung-so-2.jpg',NULL,120,'NOW_SHOWING','Bỗng Dưng Trúng Số','https://www.youtube.com/embed/D3KbO3QF-lg'),(8,'2026-01-31 01:16:10.372612',NULL,'https://upload.wikimedia.org/wikipedia/vi/4/49/Mua_do_poster.jpg',NULL,120,'NOW_SHOWING','Mưa Đỏ','https://www.youtube.com/embed/UEqjUBGjvwI'),(10,'2026-01-31 01:19:14.089085',NULL,'https://images2.thanhnien.vn/528068263637045248/2024/2/20/special-poster-2-mai-17084211313531000860296.jpg',NULL,120,'NOW_SHOWING','Mai','https://www.youtube.com/embed/EX6clvId19s'),(11,'2026-01-31 01:24:08.444660',NULL,'https://cloudcdnvod.tek4tv.vn/MAM/attach/upload/19032024204910/204910_exhumaquatmotrungmaexhuma2024poster1.jpeg',NULL,120,'NOW_SHOWING','Quật Mộ Trùng Ma','https://www.youtube.com/embed/66K9-l0EkE0'),(12,'2026-01-31 01:26:25.589445',NULL,'https://api.nongthonviet.com.vn/media/2025/03/27/67e4f1adfa15f4368d3c5f88_1_high.jpg',NULL,120,'NOW_SHOWING','Khi Cuộc Đời Cho Bạn Quả Quýt','https://www.youtube.com/embed/4ECAaQkNAbc'),(13,'2026-01-31 01:27:24.387757',NULL,'https://motchillki.fm/wp-content/uploads/2025/12/tieng-yeu-nay-anh-dich-duoc-khong-26070-thumb.webp',NULL,120,'NOW_SHOWING','Tiếng Yêu Này, Anh Dịch Được Không ?','https://www.youtube.com/embed/tNZG1aLBATY');
/*!40000 ALTER TABLE `movies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_methods`
--

DROP TABLE IF EXISTS `payment_methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_methods` (
  `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_methods`
--

LOCK TABLES `payment_methods` WRITE;
/*!40000 ALTER TABLE `payment_methods` DISABLE KEYS */;
INSERT INTO `payment_methods` VALUES ('BANK','2026-01-27 16:43:19.856322',_binary '','Bank Transfer',4),('CASH','2026-01-27 16:43:19.841948',_binary '','Cash',3),('MOMO','2026-01-27 16:43:19.811789',_binary '','MoMo Wallet',1),('VNPAY','2026-01-27 16:43:19.827876',_binary '','VNPay Gateway',2);
/*!40000 ALTER TABLE `payment_methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT,
  `amount` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `provider_txn_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `booking_id` bigint NOT NULL,
  `method_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `uk_payment_booking` (`booking_id`),
  KEY `fk_payment_method` (`method_code`),
  CONSTRAINT `fk_payment_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
  CONSTRAINT `fk_payment_method` FOREIGN KEY (`method_code`) REFERENCES `payment_methods` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,240000,'2026-01-27 16:58:22.428277','2026-01-27 17:01:15.649305',NULL,'SUCCESS',3,'MOMO'),(2,80000,'2026-01-27 22:04:07.106467','2026-01-27 22:04:16.482997',NULL,'SUCCESS',5,'MOMO'),(3,80000,'2026-01-27 22:34:15.483114','2026-01-27 22:34:21.728895',NULL,'SUCCESS',6,'MOMO'),(4,80000,'2026-01-27 22:38:23.785712',NULL,NULL,'INIT',7,'MOMO'),(5,100000,'2026-01-30 08:31:26.880391',NULL,NULL,'INIT',29,'BANK'),(6,100000,'2026-01-30 09:06:17.881622',NULL,NULL,'INIT',32,'MOMO'),(7,100000,'2026-01-30 09:13:43.527738',NULL,NULL,'INIT',33,'MOMO'),(8,100000,'2026-01-30 09:31:16.299374',NULL,NULL,'INIT',35,'MOMO'),(9,200000,'2026-01-30 10:03:21.713383',NULL,NULL,'INIT',36,'BANK'),(10,200000,'2026-01-30 10:45:50.675475',NULL,NULL,'INIT',37,'MOMO'),(11,100000,'2026-01-30 11:16:20.025950','2026-01-30 11:16:24.199018',NULL,'SUCCESS',38,'VNPAY'),(12,100000,'2026-01-30 11:19:28.309956','2026-01-30 11:19:32.145471',NULL,'SUCCESS',39,'BANK'),(13,200000,'2026-01-30 11:19:47.519719','2026-01-30 11:19:48.304827',NULL,'SUCCESS',40,'MOMO'),(14,100000,'2026-01-30 11:20:53.820469','2026-01-30 11:20:55.034904',NULL,'SUCCESS',41,'MOMO'),(15,100000,'2026-01-30 11:24:39.434526','2026-01-30 11:24:41.573523',NULL,'SUCCESS',42,'MOMO'),(16,200000,'2026-01-30 11:30:08.254921','2026-01-30 11:30:09.597159',NULL,'SUCCESS',43,'MOMO'),(17,100000,'2026-01-30 11:38:42.092665',NULL,NULL,'INIT',44,'VNPAY'),(18,200000,'2026-01-30 13:19:20.095359','2026-01-30 13:19:21.876470',NULL,'SUCCESS',46,'MOMO'),(19,100000,'2026-01-31 02:01:38.268938','2026-01-31 02:01:39.665569',NULL,'SUCCESS',47,'MOMO'),(20,70000,'2026-01-31 16:02:56.361741','2026-01-31 16:03:00.808372',NULL,'SUCCESS',48,'MOMO'),(21,70000,'2026-01-31 18:01:35.760780','2026-01-31 18:01:40.204706',NULL,'SUCCESS',51,'MOMO'),(22,70000,'2026-02-01 13:50:38.344732',NULL,NULL,'INIT',52,'MOMO'),(23,70000,'2026-02-01 13:54:28.987808',NULL,NULL,'INIT',53,'MOMO'),(24,70000,'2026-02-01 14:08:57.477884','2026-02-01 14:18:21.830413',NULL,'SUCCESS',54,'MOMO'),(25,70000,'2026-02-01 14:20:06.832586','2026-02-01 14:20:07.628677',NULL,'SUCCESS',55,'MOMO'),(26,70000,'2026-02-01 14:25:21.550182','2026-02-01 14:25:22.441465',NULL,'SUCCESS',56,'MOMO'),(27,140000,'2026-02-03 11:59:33.031889','2026-02-03 11:59:33.849430',NULL,'SUCCESS',57,'MOMO'),(28,70000,'2026-02-03 12:05:05.884812','2026-02-03 12:05:06.585843',NULL,'SUCCESS',58,'VNPAY');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `room_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `room_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `screen_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`room_id`),
  UNIQUE KEY `uk_room_name` (`room_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (1,'2026-01-15 09:46:44.375860','Room 1','2D'),(2,'2026-01-15 09:46:44.381852','Room 2','3D');
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seats`
--

DROP TABLE IF EXISTS `seats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seats` (
  `seat_id` bigint NOT NULL AUTO_INCREMENT,
  `col_index` int DEFAULT NULL,
  `row_index` int DEFAULT NULL,
  `seat_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `seat_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`seat_id`),
  UNIQUE KEY `uk_room_seat_code` (`room_id`,`seat_code`),
  CONSTRAINT `fk_seat_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seats`
--

LOCK TABLES `seats` WRITE;
/*!40000 ALTER TABLE `seats` DISABLE KEYS */;
INSERT INTO `seats` VALUES (1,0,0,'A1','STANDARD',1),(2,1,0,'A2','STANDARD',1),(3,2,0,'A3','STANDARD',1),(4,3,0,'A4','STANDARD',1),(5,4,0,'A5','STANDARD',1),(6,5,0,'A6','STANDARD',1),(7,6,0,'A7','STANDARD',1),(8,7,0,'A8','STANDARD',1),(9,8,0,'A9','STANDARD',1),(10,9,0,'A10','STANDARD',1),(11,0,1,'B1','STANDARD',1),(12,1,1,'B2','STANDARD',1),(13,2,1,'B3','STANDARD',1),(14,3,1,'B4','STANDARD',1),(15,4,1,'B5','STANDARD',1),(16,5,1,'B6','STANDARD',1),(17,6,1,'B7','STANDARD',1),(18,7,1,'B8','STANDARD',1),(19,8,1,'B9','STANDARD',1),(20,9,1,'B10','STANDARD',1),(21,0,2,'C1','STANDARD',1),(22,1,2,'C2','STANDARD',1),(23,2,2,'C3','STANDARD',1),(24,3,2,'C4','STANDARD',1),(25,4,2,'C5','STANDARD',1),(26,5,2,'C6','STANDARD',1),(27,6,2,'C7','STANDARD',1),(28,7,2,'C8','STANDARD',1),(29,8,2,'C9','STANDARD',1),(30,9,2,'C10','STANDARD',1),(31,0,3,'D1','STANDARD',1),(32,1,3,'D2','STANDARD',1),(33,2,3,'D3','STANDARD',1),(34,3,3,'D4','STANDARD',1),(35,4,3,'D5','STANDARD',1),(36,5,3,'D6','STANDARD',1),(37,6,3,'D7','STANDARD',1),(38,7,3,'D8','STANDARD',1),(39,8,3,'D9','STANDARD',1),(40,9,3,'D10','STANDARD',1),(41,0,0,'A1','STANDARD',2),(42,1,0,'A2','STANDARD',2),(43,2,0,'A3','STANDARD',2),(44,3,0,'A4','STANDARD',2),(45,4,0,'A5','STANDARD',2),(46,5,0,'A6','STANDARD',2),(47,6,0,'A7','STANDARD',2),(48,7,0,'A8','STANDARD',2),(49,8,0,'A9','STANDARD',2),(50,9,0,'A10','STANDARD',2),(51,0,1,'B1','STANDARD',2),(52,1,1,'B2','STANDARD',2),(53,2,1,'B3','STANDARD',2),(54,3,1,'B4','STANDARD',2),(55,4,1,'B5','STANDARD',2),(56,5,1,'B6','STANDARD',2),(57,6,1,'B7','STANDARD',2),(58,7,1,'B8','STANDARD',2),(59,8,1,'B9','STANDARD',2),(60,9,1,'B10','STANDARD',2),(61,0,2,'C1','STANDARD',2),(62,1,2,'C2','STANDARD',2),(63,2,2,'C3','STANDARD',2),(64,3,2,'C4','STANDARD',2),(65,4,2,'C5','STANDARD',2),(66,5,2,'C6','STANDARD',2),(67,6,2,'C7','STANDARD',2),(68,7,2,'C8','STANDARD',2),(69,8,2,'C9','STANDARD',2),(70,9,2,'C10','STANDARD',2);
/*!40000 ALTER TABLE `seats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `showtimes`
--

DROP TABLE IF EXISTS `showtimes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `showtimes` (
  `showtime_id` bigint NOT NULL AUTO_INCREMENT,
  `base_price` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `end_time` datetime(6) NOT NULL,
  `start_time` datetime(6) NOT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `movie_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`showtime_id`),
  KEY `idx_showtime_movie_start` (`movie_id`,`start_time`),
  KEY `idx_showtime_room_start` (`room_id`,`start_time`),
  CONSTRAINT `fk_showtime_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`),
  CONSTRAINT `fk_showtime_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `showtimes`
--

LOCK TABLES `showtimes` WRITE;
/*!40000 ALTER TABLE `showtimes` DISABLE KEYS */;
INSERT INTO `showtimes` VALUES (1,70000,'2026-01-15 09:46:44.594273','2026-02-04 12:45:00.000000','2026-02-04 11:00:00.000000','OPEN',1,1),(2,100000,'2026-01-15 09:46:44.597272','2026-01-16 16:01:00.000000','2026-01-16 13:00:00.000000','OPEN',2,2),(3,70000,'2026-01-31 16:01:08.031344','2026-01-31 19:00:00.000000','2026-01-31 16:00:00.000000','OPEN',13,1),(4,70000,'2026-02-01 08:18:51.383876','2026-02-01 14:30:00.000000','2026-02-01 12:00:00.000000','OPEN',11,2),(5,70000,'2026-02-03 03:27:33.630851','2026-02-04 03:27:00.000000','2026-02-01 03:27:00.000000','OPEN',2,1),(6,70000,'2026-02-03 12:04:50.077198','2026-02-03 14:04:00.000000','2026-02-03 12:04:00.000000','OPEN',10,2);
/*!40000 ALTER TABLE `showtimes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `ticket_id` bigint NOT NULL AUTO_INCREMENT,
  `price` int NOT NULL,
  `booking_id` bigint NOT NULL,
  `seat_id` bigint NOT NULL,
  `showtime_id` bigint NOT NULL,
  `ticket_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `qr_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ISSUED',
  `checked_in_at` datetime DEFAULT NULL,
  `checked_in_by` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`ticket_id`),
  UNIQUE KEY `uk_showtime_seat` (`showtime_id`,`seat_id`),
  UNIQUE KEY `uk_ticket_code` (`ticket_code`),
  KEY `idx_ticket_booking` (`booking_id`),
  KEY `fk_ticket_seat` (`seat_id`),
  KEY `idx_ticket_showtime` (`showtime_id`),
  KEY `idx_ticket_code` (`ticket_code`),
  CONSTRAINT `fk_ticket_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
  CONSTRAINT `fk_ticket_seat` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`seat_id`),
  CONSTRAINT `fk_ticket_showtime` FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`showtime_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickets`
--

LOCK TABLES `tickets` WRITE;
/*!40000 ALTER TABLE `tickets` DISABLE KEYS */;
INSERT INTO `tickets` VALUES (1,80000,3,1,1,'TCK-752787F3','TCK-752787F3','ISSUED',NULL,NULL,NULL),(2,80000,3,2,1,'TCK-75279AB1','TCK-75279AB1','ISSUED',NULL,NULL,NULL),(3,80000,3,3,1,'TCK-75279BD1','TCK-75279BD1','ISSUED',NULL,NULL,NULL),(4,80000,5,5,1,'TCK-75279C96','TCK-75279C96','ISSUED',NULL,NULL,NULL),(5,80000,6,6,1,'TCK-75279D49','TCK-75279D49','ISSUED',NULL,NULL,NULL),(6,80000,8,8,1,'TCK-75279DFC','TCK-75279DFC','ISSUED',NULL,NULL,NULL),(7,100000,38,64,2,'TCK-75279EA2','TCK-75279EA2','ISSUED',NULL,NULL,NULL),(8,100000,39,65,2,'TCK-75279F52','TCK-75279F52','ISSUED',NULL,NULL,NULL),(9,100000,40,68,2,'TCK-75279FF9','TCK-75279FF9','ISSUED',NULL,NULL,NULL),(10,100000,40,69,2,'TCK-7527A0AF','TCK-7527A0AF','ISSUED',NULL,NULL,NULL),(11,100000,41,66,2,'TCK-7527A15E','TCK-7527A15E','ISSUED',NULL,NULL,NULL),(12,100000,42,47,2,'TCK-7527A204','TCK-7527A204','ISSUED',NULL,NULL,NULL),(13,100000,43,57,2,'TCK-7527A2A5','TCK-7527A2A5','ISSUED',NULL,NULL,NULL),(14,100000,43,67,2,'TCK-7527A42C','TCK-7527A42C','ISSUED',NULL,NULL,NULL),(15,100000,46,62,2,'TCK-7527A4F8','TCK-7527A4F8','ISSUED',NULL,NULL,NULL),(16,100000,46,63,2,'TCK-7527A59F','TCK-7527A59F','ISSUED',NULL,NULL,NULL),(17,100000,47,58,2,'TCK-7527A642','TCK-7527A642','ISSUED',NULL,NULL,NULL),(18,70000,48,16,3,'TCK-7527A6E8','TCK-7527A6E8','ISSUED',NULL,NULL,NULL),(19,70000,51,28,3,'TCK-7527A78D','TCK-7527A78D','ISSUED',NULL,NULL,NULL),(20,70000,54,65,4,'TK684D656922','TICKET:TK684D656922','CHECKED_IN','2026-02-03 02:59:46','12',NULL),(21,70000,55,66,4,'TK0EF2F70496','TICKET:TK0EF2F70496','CHECKED_IN','2026-02-03 03:17:23','12',NULL),(22,70000,56,64,4,'TK1AC60C3201','TICKET:TK1AC60C3201','CHECKED_IN','2026-02-03 03:02:21','12',NULL),(23,70000,57,10,3,'TKA8552C622A','TICKET:TKA8552C622A','CHECKED_IN','2026-02-03 12:00:31','12','2026-02-03 11:59:33.859367'),(24,70000,57,2,3,'TK1EC7655130','TICKET:TK1EC7655130','CHECKED_IN','2026-02-03 12:00:40','12','2026-02-03 11:59:33.860355'),(25,70000,58,58,6,'TK37AA45714B','TICKET:TK37AA45714B','CHECKED_IN','2026-02-03 12:05:24','12','2026-02-03 12:05:06.591803');
/*!40000 ALTER TABLE `tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-01-27 01:19:01.427838','u1@gmail.com','Test User 1','123456','CUSTOMER',_binary '\0'),(2,'2026-01-28 10:56:18.844421','admin@cine.com','Admin','$2a$10$R3BMVnkxTMZbo3gv05oIhe7PV2RawAG1cgpnPjw06am92FIp8ZPAq','ADMIN',_binary ''),(3,'2026-01-28 10:56:19.152887','staff@cine.com','Staff','$2a$10$LtGrT5AomLMalStwR6wI1O/KBwnZBH4CgnW/aP/rFIKY8zFOJh6Fa','STAFF',_binary ''),(4,'2026-01-28 10:56:19.282893','user@cine.com','User','$2a$10$dkbEKPAaXx0jevq8d4z/numY2q/q9Nw99DuFHc55KsY0FLgFtV23O','USER',_binary ''),(5,'2026-01-28 11:32:18.991711','nv1@cine.com','Nhan vien 1','$2a$10$r/tZ.3qO2131XKCP1yH/4udcxgMdcu0GkDH4VMzBZgyJBfduoXqs.','STAFF',_binary '\0'),(6,'2026-01-28 11:36:01.839541','nv2@cine.com','Nhan vien 2','$2a$10$ovIJXG7db0Epch7hGCOzxOHoal39LclhYlysbR67R7Ntp1agR3XI6','STAFF',_binary ''),(7,'2026-01-29 15:12:05.000000','sqltest@gmail.com','Test User','dummy_hash','USER',_binary ''),(8,'2026-01-29 16:13:03.809532','user2@gmail.com','Vũ Yến','$2a$10$KgIzr71nhYCDj6jN8F83/ueXNz0DGeeJLggB30a0FLjNNNxpnt6T.','USER',_binary ''),(9,'2026-01-30 13:21:15.637231','user3@gmail.com','Hải Hà','$2a$10$lI8rsv.iGF/4WVRwc/1S7.M50IvMfZYR3ULNBcylNOQ4kKAcsowWK','USER',_binary ''),(12,'2026-02-03 00:57:59.642803','haihadz@gmail.com','Hai Ha DZ','$2a$10$2hTJMWIlGmpbF5u3ziBbFuBfG1gohPkF34k.We73GIxKSZpigw1fK','STAFF',_binary '');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-03 12:11:41
