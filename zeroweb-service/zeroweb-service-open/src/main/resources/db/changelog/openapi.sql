
-- changeset xezzon:42 labels:0.4
CREATE TABLE zeroweb_openapi (
  id VARCHAR(64) NOT NULL,
  code VARCHAR(255) NOT NULL,
  status VARCHAR(255) NOT NULL,
  CONSTRAINT pk_zeroweb_openapi PRIMARY KEY (id)
);

-- changeset xezzon:54 labels:0.4
ALTER TABLE zeroweb_openapi ADD destination varchar(2048) NOT NULL;
ALTER TABLE zeroweb_openapi ADD http_method varchar(16) NOT NULL;
