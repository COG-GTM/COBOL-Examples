public class CmdArgsExample {
    public static void main(String[] args) {
        int numArgs = args.length;
        
        for (int counter = 0; counter < numArgs; counter++) {
            int displayCounter = counter + 1;
            System.out.println(args[counter]);
        }
    }
}
