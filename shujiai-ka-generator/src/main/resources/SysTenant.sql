CREATE TABLE `sys_tenant`  (
   `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
   `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   `begin_date` datetime NULL DEFAULT NULL,
   `end_date` datetime NULL DEFAULT NULL,
   `status` tinyint NULL DEFAULT NULL,
   `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   `gmt_create` datetime NULL DEFAULT NULL,
   `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   `gmt_modified` datetime NULL DEFAULT NULL,
   `is_deleted` bigint NULL DEFAULT 0,
   `bpm_instance_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   `bpm_instance_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;