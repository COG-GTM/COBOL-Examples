import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        System.out.println("COBOL SQL DB Example Program - Java Edition");
        System.out.println("--------------------------------------------");
        System.out.println("Session started: " + LocalDateTime.now().format(TIMESTAMP_FMT));

        AccountHandler handler = new AccountHandler();
        handler.connect();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("1) Display all accounts");
            System.out.println("2) Display disabled accounts");
            System.out.println("3) Query accounts");
            System.out.println("4) Exit");
            System.out.print("Selection: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    List<Account> allAccounts = handler.getAllAccounts();
                    handler.displayAccounts(allAccounts);
                    break;
                case "2":
                    List<Account> disabledAccounts = handler.getDisabledAccounts();
                    handler.displayAccounts(disabledAccounts);
                    break;
                case "3":
                    boolean searchAgain = true;
                    while (searchAgain) {
                        System.out.print("Enter search value: ");
                        String searchTerm = scanner.nextLine();
                        List<Account> results = handler.searchAccounts(searchTerm);
                        handler.displayAccounts(results);
                        System.out.print("Search again? (Y/[N]) ");
                        String again = scanner.nextLine();
                        if (!again.equalsIgnoreCase("Y")) {
                            searchAgain = false;
                        }
                    }
                    break;
                case "4":
                    handler.disconnect();
                    scanner.close();
                    return;
                default:
                    System.out.println("Please make a selection between 1-4");
                    break;
            }
        }
    }
}
