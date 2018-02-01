# noinspection SqlNoDataSourceInspectionForFile

# --- !Ups

CREATE TABLE "devices"(
  "deviceID" VARCHAR(120) PRIMARY KEY,
  "userID" VARCHAR(120),
  "deviceName" VARCHAR(120)
);

CREATE TABLE "temperature"(
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "deviceID" VARCHAR(120),
  "timestamp" BIGINT,
  "value" DECIMAL
);

# --- !Downs

DROP TABLE "devices";
DROP TABLE "temperature";