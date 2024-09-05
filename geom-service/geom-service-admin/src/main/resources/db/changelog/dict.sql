
-- changeset xezzon:23 labels:0.2
CREATE TABLE geom_dict (
  id VARCHAR(64) NOT NULL,
  tag VARCHAR(255) NOT NULL,
  code VARCHAR(255) NOT NULL,
  label VARCHAR(255),
  ordinal INTEGER NOT NULL,
  parent_id VARCHAR(255) NOT NULL,
  enabled BOOLEAN NOT NULL,
  CONSTRAINT pk_geom_dict PRIMARY KEY (id)
);

-- changeset xezzon:32 labels:0.2
ALTER TABLE geom_dict ADD editable BOOLEAN DEFAULT true NOT NULL;
