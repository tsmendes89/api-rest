ALTER TABLE `address` ADD `customer_id` INT NOT NULL AFTER `main`;

ALTER TABLE `address` ADD CONSTRAINT `fk_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;