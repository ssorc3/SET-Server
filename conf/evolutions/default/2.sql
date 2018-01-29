# --- !Ups

CREATE TABLE "devices"(
  "deviceID" VARCHAR PRIMARY KEY,
  "userID" VARCHAR,
  "deviceName" VARCHAR
);

CREATE TABLE "temperature"(
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "deviceID" VARCHAR,
  "timestamp" BIGINT,
  "value" DECIMAL
);

# --- !Downs

DROP TABLE "devices";
DROP TABLE "temperature";