
-- changeset xezzon:42 labels:0.4
CREATE TABLE geom_openapi_subscription (
  id VARCHAR(64) NOT NULL,
  app_id VARCHAR(255) NOT NULL,
  openapi_code VARCHAR(255) NOT NULL,
  status VARCHAR(255) NOT NULL,
  CONSTRAINT pk_geom_openapi_subscription PRIMARY KEY (id)
);
