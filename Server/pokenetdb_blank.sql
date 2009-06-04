-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.34


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema pokenet
--

CREATE DATABASE IF NOT EXISTS pokenet;
USE pokenet;

--
-- Definition of table `pokenet`.`pn_bag`
--

DROP TABLE IF EXISTS `pokenet`.`pn_bag`;
CREATE TABLE  `pokenet`.`pn_bag` (
  `member` int(11) NOT NULL,
  `item` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  UNIQUE KEY `memberitem` (`member`,`item`),
  KEY `Memberid` (`member`)
) ENGINE=MyISAM DEFAULT CHARSET=ascii;

--
-- Dumping data for table `pokenet`.`pn_bag`
--

/*!40000 ALTER TABLE `pn_bag` DISABLE KEYS */;
LOCK TABLES `pn_bag` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `pn_bag` ENABLE KEYS */;


--
-- Definition of table `pokenet`.`pn_box`
--

DROP TABLE IF EXISTS `pokenet`.`pn_box`;
CREATE TABLE  `pokenet`.`pn_box` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member` int(11) DEFAULT NULL,
  `pokemon0` int(11) DEFAULT NULL,
  `pokemon1` int(11) DEFAULT NULL,
  `pokemon2` int(11) DEFAULT NULL,
  `pokemon3` int(11) DEFAULT NULL,
  `pokemon4` int(11) DEFAULT NULL,
  `pokemon5` int(11) DEFAULT NULL,
  `pokemon6` int(11) DEFAULT NULL,
  `pokemon7` int(11) DEFAULT NULL,
  `pokemon8` int(11) DEFAULT NULL,
  `pokemon9` int(11) DEFAULT NULL,
  `pokemon10` int(11) DEFAULT NULL,
  `pokemon11` int(11) DEFAULT NULL,
  `pokemon12` int(11) DEFAULT NULL,
  `pokemon13` int(11) DEFAULT NULL,
  `pokemon14` int(11) DEFAULT NULL,
  `pokemon15` int(11) DEFAULT NULL,
  `pokemon16` int(11) DEFAULT NULL,
  `pokemon17` int(11) DEFAULT NULL,
  `pokemon18` int(11) DEFAULT NULL,
  `pokemon19` int(11) DEFAULT NULL,
  `pokemon20` int(11) DEFAULT NULL,
  `pokemon21` int(11) DEFAULT NULL,
  `pokemon22` int(11) DEFAULT NULL,
  `pokemon23` int(11) DEFAULT NULL,
  `pokemon24` int(11) DEFAULT NULL,
  `pokemon25` int(11) DEFAULT NULL,
  `pokemon26` int(11) DEFAULT NULL,
  `pokemon27` int(11) DEFAULT NULL,
  `pokemon28` int(11) DEFAULT NULL,
  `pokemon29` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pokenet`.`pn_box`
--

/*!40000 ALTER TABLE `pn_box` DISABLE KEYS */;
LOCK TABLES `pn_box` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `pn_box` ENABLE KEYS */;


--
-- Definition of table `pokenet`.`pn_members`
--

DROP TABLE IF EXISTS `pokenet`.`pn_members`;
CREATE TABLE  `pokenet`.`pn_members` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(12) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `dob` varchar(12) DEFAULT NULL,
  `email` varchar(32) DEFAULT NULL,
  `lastLoginTime` varchar(42) DEFAULT NULL,
  `lastLoginServer` varchar(24) DEFAULT NULL,
  `lastLoginIP` varchar(16) DEFAULT NULL,
  `sprite` int(11) DEFAULT NULL,
  `pokemons` int(11) DEFAULT NULL,
  `money` int(11) DEFAULT NULL,
  `npcMul` varchar(24) DEFAULT NULL,
  `skHerb` int(11) DEFAULT NULL,
  `skCraft` int(11) DEFAULT NULL,
  `skFish` int(11) DEFAULT NULL,
  `skTrain` int(11) DEFAULT NULL,
  `skCoord` int(11) DEFAULT NULL,
  `skBreed` int(11) DEFAULT NULL,
  `x` int(11) DEFAULT NULL,
  `y` int(11) DEFAULT NULL,
  `mapX` int(11) DEFAULT NULL,
  `mapY` int(11) DEFAULT NULL,
  `bag` int(11) DEFAULT NULL,
  `badges` varchar(50) DEFAULT NULL,
  `healX` int(11) DEFAULT NULL,
  `healY` int(11) DEFAULT NULL,
  `healMapX` int(11) DEFAULT NULL,
  `healMapY` int(11) DEFAULT NULL,
  `isSurfing` varchar(5) DEFAULT NULL,
  `adminLevel` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pokenet`.`pn_members`
--

/*!40000 ALTER TABLE `pn_members` DISABLE KEYS */;
LOCK TABLES `pn_members` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `pn_members` ENABLE KEYS */;


--
-- Definition of table `pokenet`.`pn_mypokes`
--

DROP TABLE IF EXISTS `pokenet`.`pn_mypokes`;
CREATE TABLE  `pokenet`.`pn_mypokes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member` int(11) DEFAULT NULL,
  `party` int(11) DEFAULT NULL,
  `box0` int(11) DEFAULT NULL,
  `box1` int(11) DEFAULT NULL,
  `box2` int(11) DEFAULT NULL,
  `box3` int(11) DEFAULT NULL,
  `box4` int(11) DEFAULT NULL,
  `box5` int(11) DEFAULT NULL,
  `box6` int(11) DEFAULT NULL,
  `box7` int(11) DEFAULT NULL,
  `box8` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pokenet`.`pn_mypokes`
--

/*!40000 ALTER TABLE `pn_mypokes` DISABLE KEYS */;
LOCK TABLES `pn_mypokes` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `pn_mypokes` ENABLE KEYS */;


--
-- Definition of table `pokenet`.`pn_party`
--

DROP TABLE IF EXISTS `pokenet`.`pn_party`;
CREATE TABLE  `pokenet`.`pn_party` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member` int(11) DEFAULT NULL,
  `pokemon0` int(11) DEFAULT NULL,
  `pokemon1` int(11) DEFAULT NULL,
  `pokemon2` int(11) DEFAULT NULL,
  `pokemon3` int(11) DEFAULT NULL,
  `pokemon4` int(11) DEFAULT NULL,
  `pokemon5` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pokenet`.`pn_party`
--

/*!40000 ALTER TABLE `pn_party` DISABLE KEYS */;
LOCK TABLES `pn_party` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `pn_party` ENABLE KEYS */;


--
-- Definition of table `pokenet`.`pn_pokemon`
--

DROP TABLE IF EXISTS `pokenet`.`pn_pokemon`;
CREATE TABLE  `pokenet`.`pn_pokemon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(24) DEFAULT NULL,
  `speciesName` varchar(32) DEFAULT NULL,
  `exp` varchar(32) DEFAULT NULL,
  `baseExp` int(11) DEFAULT NULL,
  `expType` varchar(16) DEFAULT NULL,
  `isFainted` varchar(5) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `happiness` int(11) DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `nature` varchar(24) DEFAULT NULL,
  `abilityName` varchar(24) DEFAULT NULL,
  `itemName` varchar(28) DEFAULT NULL,
  `isShiny` varchar(5) DEFAULT NULL,
  `originalTrainerName` varchar(12) DEFAULT NULL,
  `move0` varchar(32) DEFAULT NULL,
  `move1` varchar(32) DEFAULT NULL,
  `move2` varchar(32) DEFAULT NULL,
  `move3` varchar(32) DEFAULT NULL,
  `hp` int(11) DEFAULT NULL,
  `atk` int(11) DEFAULT NULL,
  `def` int(11) DEFAULT NULL,
  `speed` int(11) DEFAULT NULL,
  `spATK` int(11) DEFAULT NULL,
  `spDEF` int(11) DEFAULT NULL,
  `evHP` int(11) DEFAULT NULL,
  `evATK` int(11) DEFAULT NULL,
  `evDEF` int(11) DEFAULT NULL,
  `evSPD` int(11) DEFAULT NULL,
  `evSPATK` int(11) DEFAULT NULL,
  `evSPDEF` int(11) DEFAULT NULL,
  `ivHP` int(11) DEFAULT NULL,
  `ivATK` int(11) DEFAULT NULL,
  `ivDEF` int(11) DEFAULT NULL,
  `ivSPD` int(11) DEFAULT NULL,
  `ivSPATK` int(11) DEFAULT NULL,
  `ivSPDEF` int(11) DEFAULT NULL,
  `pp0` int(11) DEFAULT NULL,
  `pp1` int(11) DEFAULT NULL,
  `pp2` int(11) DEFAULT NULL,
  `pp3` int(11) DEFAULT NULL,
  `maxpp0` int(11) DEFAULT NULL,
  `maxpp1` int(11) DEFAULT NULL,
  `maxpp2` int(11) DEFAULT NULL,
  `maxpp3` int(11) DEFAULT NULL,
  `ppUp0` int(11) DEFAULT NULL,
  `ppUp1` int(11) DEFAULT NULL,
  `ppUp2` int(11) DEFAULT NULL,
  `ppUp3` int(11) DEFAULT NULL,
  `date` varchar(28) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pokenet`.`pn_pokemon`
--

/*!40000 ALTER TABLE `pn_pokemon` DISABLE KEYS */;
LOCK TABLES `pn_pokemon` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `pn_pokemon` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
