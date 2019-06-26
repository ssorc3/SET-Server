# --- !Ups
CREATE TABLE zones(
  zoneID INT AUTO_INCREMENT PRIMARY KEY,
  userID VARCHAR(120),
  deviceID VARCHAR(120)
);

ALTER TABLE devices
    ADD zoneID INT;

ALTER TABLE scripts
    ADD scriptName LONGTEXT;

# --- !Downs
DROP TABLE zones;
ALTER TABLE devices
    DROP COLUMN zoneID;
ALTER TABLE scripts
    DROP COLUMN scriptName;