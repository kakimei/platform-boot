-- Create syntax for TABLE 'bo_user'
CREATE TABLE `bo_user` (
  `bo_user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `bo_password` varchar(255) NOT NULL DEFAULT '',
  `bo_user_name` varchar(255) NOT NULL DEFAULT '',
  `bo_role_type` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`bo_user_id`),
  KEY `bo_user_name_idx` (`bo_user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'feedback'
CREATE TABLE `feedback` (
  `feedback_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `feed_back_type` varchar(255) NOT NULL DEFAULT '',
  `reservation_info_id` bigint(20) DEFAULT NULL,
  `user_name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`feedback_id`),
  KEY `reservation_info_id_idx` (`reservation_info_id`),
  KEY `user_name_reservation_info_id_idx` (`user_name`,`reservation_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'meta_info'
CREATE TABLE `meta_info` (
  `meta_info_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `day` int(11) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `hour_begin` int(11) NOT NULL,
  `hour_end` int(11) NOT NULL,
  `interval_unit` varchar(255) NOT NULL DEFAULT '',
  `meta_type` varchar(255) NOT NULL DEFAULT '',
  `minute_begin` int(11) NOT NULL,
  `minute_end` int(11) NOT NULL,
  `times` int(11) NOT NULL,
  PRIMARY KEY (`meta_info_id`),
  KEY `meta_type_deleted_index` (`meta_type`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'reservation_info'
CREATE TABLE `reservation_info` (
  `reservation_info_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activity_type` varchar(255) NOT NULL DEFAULT '',
  `age` int(11) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL DEFAULT '',
  `deleted` bit(1) NOT NULL,
  `identity_card` varchar(255) NOT NULL DEFAULT '',
  `linkman_name` varchar(255) NOT NULL DEFAULT '',
  `people_count` int(11) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `reserve_begin_hour` int(11) NOT NULL,
  `reserve_begin_minute` int(11) NOT NULL,
  `reserve_date` datetime NOT NULL,
  `reserve_end_hour` int(11) NOT NULL,
  `reserve_end_minute` int(11) NOT NULL,
  `sex` varchar(255) NOT NULL DEFAULT '',
  `user_name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`reservation_info_id`),
  KEY `reservation_info_id_user_name_deleted_index` (`reservation_info_id`,`user_name`,`deleted`),
  KEY `reservation_info_id_deleted_index` (`reservation_info_id`,`deleted`),
  KEY `user_name_linkman_name_deleted_index` (`user_name`,`linkman_name`,`deleted`),
  KEY `user_name_phone_number_deleted_index` (`user_name`,`phone_number`,`deleted`),
  KEY `activity_type_deleted_index` (`activity_type`,`deleted`),
  KEY `deleted_index` (`deleted`),
  KEY `reserve_datetime_index` (`reserve_date`,`reserve_begin_hour`,`reserve_begin_minute`,`reserve_end_hour`,`reserve_end_minute`),
  KEY `reserve_date_deleted_index` (`reserve_date`,`deleted`),
  KEY `activity_type_reserve_date_deleted_index` (`activity_type`,`reserve_date`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'sign_reservation_info'
CREATE TABLE `sign_reservation_info` (
  `sign_reservation_info_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reservation_info_id` bigint(20) NOT NULL,
  `sign_in` bit(1) NOT NULL,
  `user_name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`sign_reservation_info_id`),
  KEY `user_name_reservation_info_id_sign_in_idx` (`user_name`,`reservation_info_id`,`sign_in`),
  KEY `user_name_sign_in_idx` (`user_name`,`sign_in`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'time_resource'
CREATE TABLE `time_resource` (
  `time_resource_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `hour_begin` int(11) NOT NULL,
  `hour_end` int(11) NOT NULL,
  `meta_type` varchar(255) NOT NULL DEFAULT '',
  `minute_begin` int(11) NOT NULL,
  `minute_end` int(11) NOT NULL,
  `remain_times` int(11) NOT NULL,
  `reservable_date` datetime NOT NULL,
  PRIMARY KEY (`time_resource_id`),
  KEY `meta_type_active_reservable_date_index` (`meta_type`,`active`,`reservable_date`),
  KEY `meta_type_remain_times_active_reservable_date_index` (`meta_type`,`remain_times`,`active`,`reservable_date`),
  KEY `meta_type_reservable_datetime_index` (`meta_type`,`reservable_date`,`hour_begin`,`minute_begin`,`hour_end`,`minute_end`,`active`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'user'
CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`user_id`),
  KEY `user_name_active_idx` (`user_name`,`active`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;