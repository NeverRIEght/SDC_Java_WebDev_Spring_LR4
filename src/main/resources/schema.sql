CREATE TABLE notes (
                       id      UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                       title   VARCHAR(255),
                       content TEXT
);