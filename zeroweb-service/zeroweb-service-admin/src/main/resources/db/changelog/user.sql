
-- changeset xezzon:10 labels:0.1
CREATE TABLE zeroweb_user (
  id VARCHAR(64) NOT NULL,
  username VARCHAR(255) NOT NULL,
  nickname VARCHAR(255),
  cipher VARCHAR(255) NOT NULL,
  create_time TIMESTAMP NOT NULL,
  update_time TIMESTAMP NOT NULL,
  CONSTRAINT pk_zeroweb_user PRIMARY KEY (id)
);
ALTER TABLE zeroweb_user ADD CONSTRAINT uc_zeroweb_user_username UNIQUE (username);
