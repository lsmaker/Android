CREATE TABLE preferences (
    _id	INTEGER PRIMARY KEY AUTOINCREMENT,
    invert_mode INTEGER NOT NULL DEFAULT 0,
    driving_theme INTEGER NOT NULL DEFAULT 0
);

INSERT INTO preferences (invert_mode, driving_theme) VALUES (0, 0);

