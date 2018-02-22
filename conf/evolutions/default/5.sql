# --- !Ups
ALTER TABLE scripts
  ADD lastRun BIGINT;

# --- !Downs
ALTER TABLE scripts
  DROP COLUMN lastRun;