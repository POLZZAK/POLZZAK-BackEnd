CREATE TABLE coupon (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `guardian_id` bigint NOT NULL,
                        `kid_id` bigint NOT NULL,
                        `stamp_board_id` bigint NOT NULL,
                        `reward` varchar(50) NOT NULL,
                        `state` varchar(50) NOT NULL,
                        `reward_date` timestamp NOT NULL,
                        `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE stamp_board
    ADD COLUMN `is_deleted` TINYINT NOT NULL AFTER `reward`;
