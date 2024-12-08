
-- changeset xezzon:42 labels:0.4
CREATE TABLE geom_third_party_app (
  id VARCHAR(64) NOT NULL,
  name VARCHAR(255) NOT NULL,
  owner_id VARCHAR(64) NOT NULL,
  create_time TIMESTAMP NOT NULL,
  CONSTRAINT pk_geom_third_party_app PRIMARY KEY (id)
);

-- changeset xezzon:49 labels:0.4
ALTER TABLE geom_third_party_app ADD secret_key varchar(64) NULL;
