
-- changeset xezzon:42 labels:0.4
CREATE TABLE zeroweb_openapi_subscription (
  id VARCHAR(64) NOT NULL,
  app_id VARCHAR(64) NOT NULL,
  openapi_code VARCHAR(255) NOT NULL,
  status VARCHAR(255) NOT NULL,
  CONSTRAINT pk_zeroweb_openapi_subscription PRIMARY KEY (id)
);
