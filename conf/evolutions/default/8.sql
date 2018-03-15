# --- !Ups
ALTER TABLE zones
    ADD lightGroup INT

# --- !Downs
ALTER TABLE zones
    DROP COLUMN lightGroup