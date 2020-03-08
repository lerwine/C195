
INSERT INTO `U03vHM`.`user`
	(`userId`, `userName`, `password`, `active`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (1, 'test', 'MZFrVPiO381l+/ZsPSZRuR+JP+PUUFjMR/eIoX38MT/3VUiQxQ', 2, '2019-11-25 07:13:42', 'test', '2019-11-25 07:13:42', 'test');
INSERT INTO `U03vHM`.`country`
	(`countryId`, `country`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (1, 'USA', '2019-11-25 07:13:59', 'test', '2019-11-25 07:13:59', 'test');
INSERT INTO `U03vHM`.`city`
	(`cityId`, `city`, `countryId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (1, 'Washington DC', 1, '2019-11-25 11:31:22', 'test', '2019-11-25 11:31:22', 'test');
INSERT INTO `U03vHM`.`address`
	(`addressId`, `address`, `address2`, `cityId`, `postalCode`, `phone`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (1, '1600 Pennsylvania Ave NW', 'Oval Office', 1, '20500', '(202) 456-1414', '2019-11-25 11:31:45', 'test', '2019-11-25 11:31:45', 'test');
INSERT INTO `U03vHM`.`customer`
	(`customerId`, `customerName`, `addressId`, `active`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
    VALUES (1, 'President Trump', 1, TRUE, '2019-11-25 11:32:18', 'test', '2019-11-25 11:32:18', 'test');
INSERT INTO `U03vHM`.`appointment`
	(`appointmentId`, `customerId`, `userId`, `title`, `description`, `location`, `contact`, `type`, `url`, `start`, `end`, `createDate`, `createdBy`, `lastUpdate`,`lastUpdateBy`)
	VALUES (1, 1, 1, 'First Event', 'Event 1 DESC', 'Undisclosed', 'Agent Dan', 'Type', 'Url', '2019-12-03 05:00:00', '2019-12-03 06:00:00', '2019-11-25 11:35:10', 'test', '2019-11-25 11:35:10', 'test');

INSERT INTO `U03vHM`.`city`
	(`cityId`, `city`, `countryId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (2, 'San Jose', 1, '2019-11-26 09:55:25', 'test', '2019-11-26 09:55:25', 'test');
INSERT INTO `U03vHM`.`address`
	(`addressId`, `address`, `address2`, `cityId`, `postalCode`, `phone`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (2, '191 N 1st St', 'Department 1', 1, '95113', '(408) 882-2100', '2019-11-26 09:55:44', 'test', '2019-11-26 09:55:44', 'test');
INSERT INTO `U03vHM`.`customer`
	(`customerId`, `customerName`, `addressId`, `active`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
    VALUES (2, 'Honorable  Brian C. Walsh', 1, TRUE, '2019-11-26 09:56:12', 'test', '2019-11-26 09:56:12', 'test');
INSERT INTO `U03vHM`.`country`
	(`countryId`, `country`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (2, 'Germany', '2019-11-27 16:43:19', 'test', '2019-11-27 16:43:19', 'test');
INSERT INTO `U03vHM`.`city`
	(`cityId`, `city`, `countryId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (3, 'Berlin', 1, '2019-11-27 16:43:26', 'test', '2019-11-27 16:43:26', 'test');
INSERT INTO `U03vHM`.`city`
	(`cityId`, `city`, `countryId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (4, 'Armstedt', 1, '2019-11-27 16:43:39', 'test', '2019-11-27 16:43:39', 'test');
INSERT INTO `U03vHM`.`country`
	(`countryId`, `country`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (3, 'India', '2019-11-27 16:44:05', 'test', '2019-11-27 16:44:05', 'test');
INSERT INTO `U03vHM`.`city`
	(`cityId`, `city`, `countryId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (5, 'New Delhi', 1, '2019-11-27 16:44:19', 'test', '2019-11-27 16:44:19', 'test');
INSERT INTO `U03vHM`.`city`
	(`cityId`, `city`, `countryId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (6, 'Bangalore', 1, '2019-11-27 16:44:38', 'test', '2019-11-27 16:44:38', 'test');
INSERT INTO `U03vHM`.`country`
	(`countryId`, `country`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (4, 'Puerto Rico', '2019-11-27 16:45:01', 'test', '2019-11-27 16:45:01', 'test');
INSERT INTO `U03vHM`.`city`
	(`cityId`, `city`, `countryId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`)
	VALUES (7, 'Vieques', 1, '2019-11-27 16:45:19', 'test', '2019-11-27 16:45:19', 'test');