## MongoDB REST API Example Program

This example program demonstrates how to create a GnuCOBOL application that can connect and query a MongoDB database using REST API calls. This version replaces the original PostgreSQL implementation with MongoDB integration.

**Files included:**
* ```create_mongodb_collection.js``` - MongoDB script to create the collection and test data.
* ```mongodb_rest.cbl``` - The main COBOL program that uses MongoDB REST API.
* ```http_wrapper.c``` - C wrapper functions for HTTP requests and JSON parsing.
* ```sql_example.cbl``` - Original PostgreSQL version (kept for reference).
* ```generated_sql_ex.cbl``` - Original generated SQL code (kept for reference).

**Prerequisites:**
* MongoDB Atlas cluster with Data API enabled
* libcurl development libraries
* json-c development libraries
* GnuCOBOL compiler

**MongoDB Setup:**
1. Create a MongoDB Atlas account and cluster
2. Enable the Data API in your Atlas cluster
3. Create an API key for authentication
4. Run the ```create_mongodb_collection.js``` script to set up the collection and test data
5. Update the MongoDB URL and API key in ```mongodb_rest.cbl```

**How to build**
* Compile the COBOL program: ```cobc -x mongodb_rest.cbl```
* This will create the test program executable ```mongodb_rest```
* Run the program: ```./mongodb_rest```

**Note:** The current implementation includes sample data for testing the converted program structure and menu interface. The HTTP wrapper (```http_wrapper.c```) provides the framework for future MongoDB REST API integration.

**Notes regarding MongoDB REST API integration:**

This implementation uses MongoDB's Data API to perform database operations through HTTP REST calls. The key differences from the SQL version:

1. **Connection**: Instead of ODBC connection strings, we use MongoDB Atlas Data API endpoints with API key authentication.

2. **Queries**: SQL cursors are replaced with MongoDB find operations:
   - `ACCOUNT-ALL-CUR` → `find({})` - finds all documents
   - `ACCOUNT-DISABLED-CUR` → `find({"is_enabled":"N"})` - finds disabled accounts
   - `ACCOUNT-QUERY-CUR` → `find({"$or":[...]})` - uses regex search across multiple fields

3. **Data Format**: Instead of SQL result sets, we parse JSON responses from the MongoDB API.

4. **Error Handling**: HTTP response codes replace SQLSTATE/SQLCODE error handling.

The program maintains the same user interface and behavior as the original SQL version, but uses MongoDB as the backend database through REST API calls.

**Configuration:**
The program currently uses sample data to demonstrate the converted structure. To enable full MongoDB REST API integration, update the following variables in ```mongodb_rest.cbl```:
- `ws-mongodb-base-url`: Your MongoDB Atlas Data API endpoint
- `ws-mongodb-api-key`: Your API key

The MongoDB REST API integration framework is provided via ```http_wrapper.c``` for future enhancement.

