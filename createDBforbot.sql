USE myDBforbot;
CREATE TABLE `botconfig` (
  `param` varchar(20) NOT NULL,
  `value` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`param`)
) ENGINE=InnoDB COMMENT='Config info for bot';
CREATE TABLE `botusers` (
  `idmsg` int NOT NULL,
  `chatid` int DEFAULT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `text` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dateMsg` datetime DEFAULT CURRENT_TIMESTAMP,
  `acknowledge` int DEFAULT NULL,
  PRIMARY KEY (`idmsg`)
  ) ENGINE=InnoDB;
  CREATE TABLE `logtable` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  `message` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

INSERT INTO `myDBforbot`.`botconfig` (`param`, `value`) VALUES ('botAddress', '***');
INSERT INTO `myDBforbot`.`botconfig` (`param`, `value`) VALUES ('weatherAPI', '**');