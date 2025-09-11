
use('cobol_db_example');

db.accounts.drop();

db.accounts.insertMany([
    {
        "_id": 1,
        "first_name": "John",
        "last_name": "Tester",
        "phone": "15555550100",
        "address": "123 Fake St, Nowhere",
        "is_enabled": "Y",
        "create_dt": new Date(),
        "mod_dt": new Date()
    },
    {
        "_id": 2,
        "first_name": "Mike",
        "last_name": "Tester1",
        "phone": "15555550121",
        "address": "122 Real St, Nowhere",
        "is_enabled": "Y",
        "create_dt": new Date(),
        "mod_dt": new Date()
    },
    {
        "_id": 3,
        "first_name": "Mary",
        "last_name": "Tester2",
        "phone": "15555550132",
        "address": "121 ABC St, Nowhere",
        "is_enabled": "Y",
        "create_dt": new Date(),
        "mod_dt": new Date()
    },
    {
        "_id": 4,
        "first_name": "Jack",
        "last_name": "Tester3",
        "phone": "15555550143",
        "address": "120 Rock St, Nowhere",
        "is_enabled": "Y",
        "create_dt": new Date(),
        "mod_dt": new Date()
    },
    {
        "_id": 5,
        "first_name": "Bob",
        "last_name": "Tester4",
        "phone": "15555550154",
        "address": "119 Truck St, Nowhere",
        "is_enabled": "N",
        "create_dt": new Date(),
        "mod_dt": new Date()
    },
    {
        "_id": 6,
        "first_name": "Paula",
        "last_name": "Tester5",
        "phone": "1555550165",
        "address": "118 Car St, Nowhere",
        "is_enabled": "N",
        "create_dt": new Date(),
        "mod_dt": new Date()
    },
    {
        "_id": 7,
        "first_name": "James",
        "last_name": "Tester6",
        "phone": "1555550176",
        "address": "117 Land St, Nowhere",
        "is_enabled": "Y",
        "create_dt": new Date(),
        "mod_dt": new Date()
    },
    {
        "_id": 8,
        "first_name": "Jane",
        "last_name": "Tester7",
        "phone": "1555550187",
        "address": "116 Sea St, Nowhere",
        "is_enabled": "Y",
        "create_dt": new Date(),
        "mod_dt": new Date()
    },
    {
        "_id": 9,
        "first_name": "Bill",
        "last_name": "Tester8",
        "phone": "1555550198",
        "address": "115 Dock St, Nowhere",
        "is_enabled": "N",
        "create_dt": new Date(),
        "mod_dt": new Date()
    },
    {
        "_id": 10,
        "first_name": "Lucy",
        "last_name": "Tester9",
        "phone": "1555550209",
        "address": "114 Beach St, Nowhere",
        "is_enabled": "Y",
        "create_dt": new Date(),
        "mod_dt": new Date()
    },
    {
        "_id": 11,
        "first_name": "Richard",
        "last_name": "Tester10",
        "phone": "1555550210",
        "address": "113 Water St, Nowhere",
        "is_enabled": "Y",
        "create_dt": new Date(),
        "mod_dt": new Date()
    }
]);

db.accounts.createIndex({ "is_enabled": 1 });
db.accounts.createIndex({ "first_name": "text", "last_name": "text", "phone": "text", "address": "text" });

print("MongoDB collection 'accounts' created successfully with sample data");
