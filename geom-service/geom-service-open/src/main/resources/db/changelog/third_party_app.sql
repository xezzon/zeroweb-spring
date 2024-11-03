
-- changeset xezzon:42 labels:0.4
CREATE TABLE geom_third_party_app (
  id VARCHAR(64) NOT NULL,
  name VARCHAR(255) NOT NULL,
  owner_id VARCHAR(64) NOT NULL,
  CONSTRAINT pk_geom_third_party_app PRIMARY KEY (id)
)
