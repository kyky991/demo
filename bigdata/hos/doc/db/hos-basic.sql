CREATE DATABASE IF NOT EXISTS hos
  DEFAULT CHARACTER SET UTF8
  COLLATE UTF8_GENERAL_CI;

USE hos;

--
-- Table structure for table `user_info`
--
DROP TABLE IF EXISTS user_info;

CREATE TABLE user_info
(
  user_id     VARCHAR(32) NOT NULL,
  username   VARCHAR(32) NOT NULL,
  password    VARCHAR(64) NOT NULL COMMENT 'password md5',
  system_role VARCHAR(32) NOT NULL COMMENT 'ADMIN OR USER',
  create_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  detail      VARCHAR(256),
  PRIMARY KEY (user_id),
  UNIQUE KEY AK_UQ_USER_NAME (username)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT = '用户信息';

--
-- Table structure for table `token_info`
--

DROP TABLE IF EXISTS token_info;

CREATE TABLE token_info
(
  token        VARCHAR(32) NOT NULL,
  expire_time  INT(11)     NOT NULL,
  create_time  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  refresh_time TIMESTAMP   NOT NULL,
  active       TINYINT     NOT NULL,
  creator      VARCHAR(32) NOT NULL,
  PRIMARY KEY (token)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT = 'token 信息表';

--
-- Table structure for table `hos_bucket`
--

DROP TABLE IF EXISTS hos_bucket;

CREATE TABLE hos_bucket (
  bucket_id   VARCHAR(32),
  bucket_name VARCHAR(32),
  create_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  detail      VARCHAR(256),
  creator     VARCHAR(32) NOT NULL,
  UNIQUE KEY AK_KEY_BUCKET_NAME(bucket_name),
  PRIMARY KEY (bucket_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT = 'hos bucket';

--
-- Table structure for table service_auth
--

DROP TABLE IF EXISTS service_auth;

CREATE TABLE service_auth
(
  bucket_name  VARCHAR(32) NOT NULL,
  target_token VARCHAR(32) NOT NULL COMMENT '被授权对象token',
  auth_time    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (bucket_name, target_token)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT = '对象存储服务授权表';

