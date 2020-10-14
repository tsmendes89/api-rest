CREATE TABLE `rest`.`customer` (
`id` INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`),
`name` VARCHAR(100) NOT NULL , `email` VARCHAR(100) NOT NULL ,
`birthdate` DATE NOT NULL , `cpf` VARCHAR(11) NOT NULL ,
`gender` VARCHAR(10) NOT NULL, UNIQUE `uk_cpf` (`cpf`));
