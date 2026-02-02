DELETE
FROM notes;
DELETE
FROM categories;

INSERT INTO categories (id, name)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Work & Coding'),
       ('550e8400-e29b-41d4-a716-446655440002', 'Gaming & Leisure');

INSERT INTO notes (id, title, content, category_id)
VALUES (random_uuid(), 'Spring Security', 'Review OAuth2 flow', '550e8400-e29b-41d4-a716-446655440001'),
       (random_uuid(), 'JPA/Hibernate', 'Fix LazyInitializationException in Category',
        '550e8400-e29b-41d4-a716-446655440001'),
       (random_uuid(), 'Docker Compose', 'Add Redis container to local setup', '550e8400-e29b-41d4-a716-446655440001'),
       (random_uuid(), 'Refactoring', 'Replace all remaining records with classes for JPA',
        '550e8400-e29b-41d4-a716-446655440001'),
       (random_uuid(), 'SQL Performance', 'Check index on notes.title', '550e8400-e29b-41d4-a716-446655440001'),
       (random_uuid(), 'Code Review', 'Check PR #42 for SOLID principles', '550e8400-e29b-41d4-a716-446655440001'),
       (random_uuid(), 'CI/CD', 'Fix Jenkins pipeline for lab-3', '550e8400-e29b-41d4-a716-446655440001'),
       (random_uuid(), 'Monitoring', 'Configure Actuator with Prometheus', '550e8400-e29b-41d4-a716-446655440001'),
       (random_uuid(), 'Meeting', 'Sync with mentor about architecture', '550e8400-e29b-41d4-a716-446655440001'),
       (random_uuid(), 'Documentation', 'Update README with profile instructions',
        '550e8400-e29b-41d4-a716-446655440001');

INSERT INTO notes (id, title, content, category_id)
VALUES (random_uuid(), 'Elden Ring', 'Find the last smithing stone', '550e8400-e29b-41d4-a716-446655440002'),
       (random_uuid(), 'Cyberpunk 2077', 'Finish Phantom Liberty DLC', '550e8400-e29b-41d4-a716-446655440002'),
       (random_uuid(), 'Hollow Knight', 'Beat Nightmare King Grimm', '550e8400-e29b-41d4-a716-446655440002'),
       (random_uuid(), 'Hardware', 'Check RTX 5080 benchmarks', '550e8400-e29b-41d4-a716-446655440002'),
       (random_uuid(), 'Factorio', 'Automate purple science packs', '550e8400-e29b-41d4-a716-446655440002'),
       (random_uuid(), 'Baldurs Gate 3', 'Start Honor Mode run', '550e8400-e29b-41d4-a716-446655440002'),
       (random_uuid(), 'Indie Games', 'Check Manor Lords roadmap', '550e8400-e29b-41d4-a716-446655440002'),
       (random_uuid(), 'Steam Deck', 'Optimize battery for long sessions', '550e8400-e29b-41d4-a716-446655440002'),
       (random_uuid(), 'Discord', 'Join Dev community server', '550e8400-e29b-41d4-a716-446655440002'),
       (random_uuid(), 'Minecraft', 'Build auto-sorting storage system', '550e8400-e29b-41d4-a716-446655440002');