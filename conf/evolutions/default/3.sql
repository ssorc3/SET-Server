# --- !Ups
CREATE TABLE humidity(
  id INT AUTO_INCREMENT PRIMARY KEY,
  deviceID VARCHAR(120),
  timestamp BIGINT,
  value DECIMAL
);

CREATE TABLE light(
  id INT AUTO_INCREMENT PRIMARY KEY,
  deviceID VARCHAR(120),
  timestamp BIGINT,
  value DECIMAL
);

# --- !Downs

DROP TABLE humidity;
DROP TABLE light;