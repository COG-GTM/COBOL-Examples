-- Seed data ported from sql/create_test_db.sql.
INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt) VALUES
    ('John',    'Tester',   '15555550100', '123 Fake St, Nowhere',  'Y', now(), now()),
    ('Mike',    'Tester1',  '15555550121', '122 Real St, Nowhere',  'Y', now(), now()),
    ('Mary',    'Tester2',  '15555550132', '121 ABC St, Nowhere',   'Y', now(), now()),
    ('Jack',    'Tester3',  '15555550143', '120 Rock St, Nowhere',  'Y', now(), now()),
    ('Bob',     'Tester4',  '15555550154', '119 Truck St, Nowhere', 'N', now(), now()),
    ('Paula',   'Tester5',  '1555550165',  '118 Car St, Nowhere',   'N', now(), now()),
    ('James',   'Tester6',  '1555550176',  '117 Land St, Nowhere',  'Y', now(), now()),
    ('Jane',    'Tester7',  '1555550187',  '116 Sea St, Nowhere',   'Y', now(), now()),
    ('Bill',    'Tester8',  '1555550198',  '115 Dock St, Nowhere',  'N', now(), now()),
    ('Lucy',    'Tester9',  '1555550209',  '114 Beach St, Nowhere', 'Y', now(), now()),
    ('Richard', 'Tester10', '1555550210',  '113 Water St, Nowhere', 'Y', now(), now());
