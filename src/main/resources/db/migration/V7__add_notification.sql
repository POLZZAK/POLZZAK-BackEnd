CREATE TABLE `notification` (
                                `id` bigint NOT NULL,
                                `title` varchar(50) NOT NULL,
                                `content` varchar(200) NOT NULL,
                                `sender_id` bigint DEFAULT NULL,
                                `receiver_id` bigint NOT NULL,
                                `data` text,
                                `created_date` timestamp NOT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE `coupon`
    ADD COLUMN `request_date` TIMESTAMP NULL DEFAULT NULL AFTER `state`;
