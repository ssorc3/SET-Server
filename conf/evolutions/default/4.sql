# --- !Ups
CREATE TABLE bridges(
  bridgeID VARCHAR(120)
);

CREATE TABLE scripts(
  userID VARCHAR(120),
  script VARCHAR(65535)
);

CREATE TABLE noise(
  id INT AUTO_INCREMENT PRIMARY KEY,
  deviceID VARCHAR(120),
  timestamp BIGINT,
  value DECIMAL
);

# --- !Downs
DROP TABLE bridges;
DROP TABLE scripts;