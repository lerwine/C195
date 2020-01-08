DELETE FROM `U03vHM`.`appointment`;
DELETE FROM `U03vHM`.`customer`;
DELETE FROM `U03vHM`.`user`;
DELETE FROM `U03vHM`.`address`;
DELETE FROM `U03vHM`.`city`;
DELETE FROM `U03vHM`.`country`;

SET @t = TIMESTAMPADD(YEAR, 1, NOW());
SELECT @t;
