      ******************************************************************
      * Stand-in oracle mirroring merge_sort_test.cbl paragraphs
      * merge-and-display-files and sort-and-display-file, statement
      * for statement, minus create-test-data (inputs are supplied).
      ******************************************************************
       identification division.
       program-id. merge-sort-oracle.

       environment division.
       input-output section.

       file-control.

           select fd-test-file-1 assign to "test-file-1.txt"
           organization is line sequential
           file status is ws-fs-status-1.

           select fd-test-file-2 assign to "test-file-2.txt"
           organization is line sequential
           file status is ws-fs-status-2.

           select fd-sorting-file assign to "work-temp.txt".

           select fd-merged-file assign to "merge-output.txt"
           organization is line sequential
           file status is ws-fs-status-merge.

           select fd-sorted-contract-id
           assign to "sorted-contract-id.txt"
           organization is line sequential
           file status is ws-fs-status-sorted.


       data division.

       file section.

       sd  fd-sorting-file.
       01  f-customer-record-sort.
           05  f-customer-id                       pic 9(5).
           05  f-customer-last-name                pic x(50).
           05  f-customer-first-name               pic x(50).
           05  f-customer-contract-id              pic 9(5).
           05  f-customer-comment                  pic x(25).

       fd  fd-test-file-1 recording mode F.
       01  f-customer-record-east.
           05  f-customer-id                       pic 9(5).
           05  f-customer-last-name                pic x(50).
           05  f-customer-first-name               pic x(50).
           05  f-customer-contract-id              pic 9(5).
           05  f-customer-comment                  pic x(25).

       fd  fd-test-file-2 recording mode F.
       01  f-customer-record-west.
           05  f-customer-id                       pic 9(5).
           05  f-customer-last-name                pic x(50).
           05  f-customer-first-name               pic x(50).
           05  f-customer-contract-id              pic 9(5).
           05  f-customer-comment                  pic x(25).

       fd  fd-merged-file recording mode F.
       01  f-customer-record-merged.
           05  f-customer-id                       pic 9(5).
           05  f-customer-last-name                pic x(50).
           05  f-customer-first-name               pic x(50).
           05  f-customer-contract-id              pic 9(5).
           05  f-customer-comment                  pic x(25).

       fd  fd-sorted-contract-id recording mode F.
       01  f-customer-record-sorted-contract-id.
           05  f-customer-id                       pic 9(5).
           05  f-customer-last-name                pic x(50).
           05  f-customer-first-name               pic x(50).
           05  f-customer-contract-id              pic 9(5).
           05  f-customer-comment                  pic x(25).

       working-storage section.

       01  ws-fs-status-1                          pic xx.
       01  ws-fs-status-2                          pic xx.
       01  ws-fs-status-merge                      pic xx.
       01  ws-fs-status-sorted                     pic xx.

       01  ws-eof-sw                               pic x value 'N'.
           88  ws-eof                              value 'Y'.
           88  ws-not-eof                          value 'N'.

       procedure division.
       main-procedure.

           perform merge-and-display-files

           perform sort-and-display-file

           display "Done."

           stop run.


       merge-and-display-files.

           display "Merging and sorting files..."

           merge fd-sorting-file
               on ascending key f-customer-id
               of f-customer-record-merged
               using fd-test-file-1 fd-test-file-2 giving fd-merged-file

           open input fd-merged-file

               if ws-fs-status-merge not = "00" then
                   display "Error opening merged output file: "
                       ws-fs-status-merge
                   end-display
                   stop run
               end-if

               set ws-not-eof to true

               perform until ws-eof
                   read fd-merged-file
                       at end
                           set ws-eof to true
                       not at end
                           display f-customer-record-merged
                   end-read
               end-perform

           close fd-merged-file

           exit paragraph.



       sort-and-display-file.

           display "Sorting merged file on descending contract id...."

           sort fd-sorting-file
               on descending key f-customer-contract-id
               of f-customer-record-sorted-contract-id
               using fd-merged-file giving fd-sorted-contract-id

           open input fd-sorted-contract-id

               if ws-fs-status-sorted not = "00" then
                   display "Error opening sorted output file: "
                       ws-fs-status-sorted
                   end-display
                   stop run
               end-if

               set ws-not-eof to true

               perform until ws-eof
                   read fd-sorted-contract-id
                       at end
                           set ws-eof to true
                       not at end
                           display f-customer-record-sorted-contract-id
                   end-read
               end-perform

           close fd-sorted-contract-id

           exit paragraph.

       end program merge-sort-oracle.
