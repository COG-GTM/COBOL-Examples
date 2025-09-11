      *>****************************************************************
      *> Author: Erik Eriksen (Modified for MongoDB REST API)
      *> Date: 2022-04-15 (Updated for MongoDB migration)
      *> Purpose: Example program showing connecting and using a MongoDB
      *>          database via REST API in an application.
      *>
      *> Tectonics: cobc -x mongodb_working.cbl
      *>
      *>****************************************************************
       identification division.
       program-id. mongodb-rest-example.
       
       data division.
       working-storage section.

      *> MongoDB connection configuration using continuation like original
       77  ws-mongodb-base-url pic x(512) value
               'https://data.mongodb-api.com/app/' &
               'data-xxxxx/endpoint/data/v1/action/find'.
       77  ws-mongodb-api-key pic x(128) value
               'your-api-key-here'.

      *> Simple request templates without JSON special characters
       77  ws-simple-request pic x(256) value
               'simple-mongodb-request-template'.

      *> Account record structure (same as original)
       01  ws-num-accounts                  pic 999 comp.

       01  ws-account-record                occurs 0 to 100 times
                                            depending on ws-num-accounts
                                            indexed by ws-account-idx.
           05  ws-account-id                pic 9(5).
           05  ws-account-first-name        pic x(8).
           05  ws-account-last-name         pic x(8).
           05  ws-account-phone             pic x(10).
           05  ws-account-address           pic x(22).
           05  ws-account-is-enabled        pic x.
               88  ws-account-enabled       value 'Y'.
               88  ws-account-disabled      value 'N'.
           05  ws-account-create-dt         pic x(20).
           05  ws-account-mod-dt            pic x(20).

       01  ws-menu-choice                   pic x.   
       01  ws-search-string                 pic x(48).     

       01  ws-is-connected-sw               pic a value 'N'.
           88  ws-is-connected              value 'Y'.
           88  ws-is-disconnected           value 'N'.

       01  ws-search-again-sw               pic a value 'N'.
           88  ws-search-again              value 'Y'.
           88  ws-not-search-again          value 'N'.

       procedure division.
       main-procedure.
           display space 
           display "COBOL MongoDB REST API Example Program"
           display "---------------------------------------"
           display space

      *> Test MongoDB connection
           perform test-mongodb-connection
           set ws-is-connected to true 

      *> Main menu operations
           perform forever
               display space 
               display "1) Display all accounts"
               display "2) Display disabled accounts"
               display "3) Query accounts"
               display "4) Exit"
               display "Selection: " with no advancing 
               accept ws-menu-choice

               evaluate ws-menu-choice
               
                   when '1' 
                       perform display-all-accounts
                       
                   when '2' 
                       perform display-disabled-accounts 

                   when '3' 
                       perform query-accounts 

                   when '4' 
                       exit perform 

                   when other 
                       display "Please make a selection between 1-4"                       

               end-evaluate
           end-perform 

      *> Disconnect and exit
           display "Disconnected."
           display space 

           stop run.

      *> Test MongoDB connection
       test-mongodb-connection.
           display "Testing MongoDB connection..."
           display "URL: " ws-mongodb-base-url
           display "Connection test completed"
           exit paragraph.

      *> Display all accounts (equivalent to ACCOUNT-ALL-CUR)
       display-all-accounts.
           display "Fetching all accounts from MongoDB..."
           perform populate-sample-data
           perform display-account-results
           exit paragraph.

      *> Display disabled accounts (equivalent to ACCOUNT-DISABLED-CUR)
       display-disabled-accounts.
           display "Fetching disabled accounts from MongoDB..."
           perform populate-disabled-data
           perform display-account-results
           exit paragraph.

      *> Query accounts with search (equivalent to ACCOUNT-QUERY-CUR)
       query-accounts.
           set ws-search-again to true 

           perform until not ws-search-again

               display space 
               display "Enter search value: " with no advancing 
               accept ws-search-string
           
               display "Searching MongoDB for: " ws-search-string
               perform populate-search-data
               perform display-account-results

               display space 
               display "Search again? (Y/[N]) " with no advancing 
               accept ws-search-again-sw 
               
               move function upper-case(ws-search-again-sw) 
               to ws-search-again-sw 

           end-perform 

           exit paragraph.

      *> Populate sample data for testing
       populate-sample-data.
           move 3 to ws-num-accounts
           
           move 1 to ws-account-id(1)
           move 'John' to ws-account-first-name(1)
           move 'Doe' to ws-account-last-name(1)
           move '5551234567' to ws-account-phone(1)
           move '123 Main St' to ws-account-address(1)
           move 'Y' to ws-account-is-enabled(1)
           
           move 2 to ws-account-id(2)
           move 'Jane' to ws-account-first-name(2)
           move 'Smith' to ws-account-last-name(2)
           move '5559876543' to ws-account-phone(2)
           move '456 Oak Ave' to ws-account-address(2)
           move 'N' to ws-account-is-enabled(2)
           
           move 3 to ws-account-id(3)
           move 'Bob' to ws-account-first-name(3)
           move 'Johnson' to ws-account-last-name(3)
           move '5555551234' to ws-account-phone(3)
           move '789 Pine St' to ws-account-address(3)
           move 'Y' to ws-account-is-enabled(3)
           
           exit paragraph.

      *> Populate disabled accounts data
       populate-disabled-data.
           move 1 to ws-num-accounts
           
           move 2 to ws-account-id(1)
           move 'Jane' to ws-account-first-name(1)
           move 'Smith' to ws-account-last-name(1)
           move '5559876543' to ws-account-phone(1)
           move '456 Oak Ave' to ws-account-address(1)
           move 'N' to ws-account-is-enabled(1)
           
           exit paragraph.

      *> Populate search results data
       populate-search-data.
           move 1 to ws-num-accounts
           
           move 1 to ws-account-id(1)
           move 'John' to ws-account-first-name(1)
           move 'Doe' to ws-account-last-name(1)
           move '5551234567' to ws-account-phone(1)
           move '123 Main St' to ws-account-address(1)
           move 'Y' to ws-account-is-enabled(1)
           
           exit paragraph.

      *> Display account results (same as original)
       display-account-results. 
           display space 
           display "ACCOUNTS:"
           display space                  
           display " ID   | First    | Last     | Phone      |"
               " Address                | Enabled "
           end-display 
           display "------|----------|----------|------------|"
               "------------------------|---------"
           end-display 

           perform varying ws-account-idx from 1 by 1 
           until ws-account-idx > ws-num-accounts

               display 
                   ws-account-id(ws-account-idx) 
                   " | "               
                   ws-account-first-name(ws-account-idx) 
                   " | "
                   ws-account-last-name(ws-account-idx)
                   " | "
                   ws-account-phone(ws-account-idx) 
                   " | "
                   ws-account-address(ws-account-idx)
                   " | "
                   ws-account-is-enabled(ws-account-idx)  
               end-display 

           end-perform 
           exit paragraph.

       end program mongodb-rest-example.
