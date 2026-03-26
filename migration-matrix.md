# COBOL to Java Migration Matrix

This table maps every COBOL source file in the repository to its Java equivalent in the `java-migration/` project.

| COBOL File | Java Class | Notes |
|---|---|---|
| sql/sql_example.cbl | Account.java, AccountRepository.java, AccountService.java, AccountCommands.java | Full DB + CLI migration |
| sql/create_test_db.sql | V1__create_accounts.sql | Flyway migration, reused as-is |
| json_generate/json_generate.cbl | SerializationService.generateJson() | Jackson ObjectMapper |
| xml_generate/xml_generate.cbl | SerializationService.generateXml() | Jackson XmlMapper |
| merge_sort/merge_sort_test.cbl | FileMergeService.java | Java Collections sort |
| report_writer/report_test.cbl | ReportService.java | Plain text formatter |
| sub_program/main_app.cbl + sub.cbl | SubProgramCommands.java | Spring @Service DI |
| trim/trim.cbl | StringUtilService.trim*() | String.strip() methods |
| unstring/unstring.cbl | StringUtilService.unstring() | String.split() |
| is_numeric/is_numeric.cbl | StringUtilService.isNumeric() | Regex/char check |
| redifines/redefines.cbl | Customer.java hierarchy | Inheritance replaces REDEFINES |
| search/search.cbl | SearchService.java | Stream filter + binarySearch |
| comp_test/comp_test.cbl | NumericConversionService.java | Java native int is binary |
| numval_test/numval_test.cbl | NumericConversionService.java | Java native type conversion |
| accept/accept.cbl | UtilityCommands.java | Spring Shell input |
| read_command_args/ | Application args | Spring Boot ApplicationArguments |
| mouse/mouse_example.cbl | NOT MIGRATED | Terminal curses paint demo, no business value |
