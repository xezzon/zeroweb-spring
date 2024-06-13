
-- changeset xezzon:10 labels:0.1
CREATE TABLE geom_user (
  id VARCHAR(64) NOT NULL,
  username VARCHAR(255) NOT NULL,
  nickname VARCHAR(255),
  cipher VARCHAR(255) NOT NULL,
  create_time TIMESTAMP NOT NULL,
  update_time TIMESTAMP NOT NULL,
  CONSTRAINT pk_geom_user PRIMARY KEY (id)
);
ALTER TABLE geom_user ADD CONSTRAINT uc_geom_user_username UNIQUE (username);
