INSERT INTO notes (id, title, content)
SELECT
    RANDOM_UUID(),
    'Note title ' || x,
    'Note number ' || x
FROM SYSTEM_RANGE(1, 100);