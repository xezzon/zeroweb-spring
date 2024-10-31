
-- changeset xezzon:42 labels:0.4
CREATE TABLE geom_openapi (
  id VARCHAR(64) NOT NULL,
  code VARCHAR(255) NOT NULL,
  status VARCHAR(255) NOT NULL,
  CONSTRAINT pk_geom_openapi PRIMARY KEY (id)
);
