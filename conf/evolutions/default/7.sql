# --- !Ups
CREATE TABLE idealTemps(
  userID VARCHAR(120),
  temp DECIMAL
);

# --- Downs
DROP TABLE idealTemps;