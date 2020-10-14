CREATE TABLE `rest`.`address` ( `id` INT NOT NULL AUTO_INCREMENT, `state` VARCHAR(2) NOT NULL ,
`city` VARCHAR(100) NOT NULL , `neighborhood` VARCHAR(100) NOT NULL ,
`zipcode` VARCHAR(8) NOT NULL , `street` VARCHAR(100) NOT NULL ,
`number` VARCHAR(5) NOT NULL , `additional_information` VARCHAR(100) NOT NULL ,
`main` INT(1) NOT NULL , PRIMARY KEY (`id`));