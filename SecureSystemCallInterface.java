import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class SecureSystemCallInterface {

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static Map<String, String> userDatabase = new HashMap<>();
    private static Map<String, Integer> loginAttempts = new HashMap<>();
    private static List<String> systemCallLogs = new ArrayList<>();
    private static Map<String, Integer> systemCallCounts = new HashMap<>();

    static {
        // Initialize with some users
        userDatabase.put("admin", "password123");
        userDatabase.put("user1", "pass1");
        userDatabase.put("user2", "securePass2");
        userDatabase.put("guest", "guest123");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Secure System Call Interface ---");

        while (true) {
            String username = login(scanner);
            if (username == null) {
                System.out.println("Too many failed attempts. Exiting...");
                break;
            }

            runMenu(username, scanner);
        }

        scanner.close();
    }

    private static String login(Scanner scanner) {
        String username;
        String password;
        int attempts = 0;

        System.out.print("Enter Username: ");
        username = scanner.nextLine();

        loginAttempts.putIfAbsent(username, 0); // Track login attempts

        while (attempts < MAX_LOGIN_ATTEMPTS) {
            System.out.print("Enter Password: ");
            password = scanner.nextLine();

            if (authenticateUser(username, password)) {
                System.out.println("Authentication Successful!");
                logSystemCall(username, "Login Successful");
                return username;
            } else {
                attempts++;
                loginAttempts.put(username, attempts);
                int remainingAttempts = MAX_LOGIN_ATTEMPTS - attempts;
                System.out.println("Authentication Failed. Try again. (" + remainingAttempts + " attempts left)");
                logSystemCall(username, "Failed Login Attempt");
            }
        }

        return null; // Too many failed attempts
    }

    private static boolean authenticateUser(String username, String password) {
        return userDatabase.containsKey(username) && userDatabase.get(username).equals(password);
    }

    private static void runMenu(String username, Scanner scanner) {
        while (true) {
            System.out.println("\n1. Check Disk Space");
            System.out.println("2. View Logs");
            System.out.println("3. Show Usage Stats");
            System.out.println("4. Logout & Switch User");
            System.out.println("5. Logout & Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                checkDiskSpace(username);
            } else if (choice == 2) {
                viewLogs();
            } else if (choice == 3) {
                showUsageStats();
            } else if (choice == 4) {
                System.out.println("Logging out...\n");
                logSystemCall(username, "Logged out");
                return; // Return to login screen
            } else if (choice == 5) {
                System.out.println("Logging out and exiting...");
                logSystemCall(username, "Logged out and exited");
                System.exit(0);
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void checkDiskSpace(String username) {
        File root = new File("/");
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        System.out.println("\n--- Disk Space Details ---");
        System.out.printf("Total Space : %.2f GB\n", totalSpace / 1e9);
        System.out.printf("Used Space  : %.2f GB\n", usedSpace / 1e9);
        System.out.printf("Free Space  : %.2f GB\n", freeSpace / 1e9);
        System.out.println("---------------------------");

        logSystemCall(username, "Checked Disk Space");
        systemCallCounts.put(username, systemCallCounts.getOrDefault(username, 0) + 1);
    }

    private static void logSystemCall(String username, String action) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logEntry = String.format("[%s] User: %-10s | Action: %s", timestamp, username, action);
        systemCallLogs.add(logEntry);
    }

    private static void viewLogs() {
        if (systemCallLogs.isEmpty()) {
            System.out.println("\n--- No System Call Logs Available ---\n");
            return;
        }

        System.out.println("\n--- System Call Logs ---");
        System.out.printf("%-25s %-12s %-25s\n", "Timestamp", "User", "Action");
        System.out.println("---------------------------------------------------------------");

        for (String log : systemCallLogs) {
            System.out.println(log);
        }
        System.out.println("---------------------------------------------------------------\n");
    }

    private static void showUsageStats() {
        System.out.println("\n--- System Call Usage Statistics ---");
        for (Map.Entry<String, Integer> entry : systemCallCounts.entrySet()) {
            String username = entry.getKey();
            int count = entry.getValue();
            System.out.printf("%-10s: ", username);
            for (int i = 0; i < count; i++) {
                System.out.print("█");
            }
            System.out.println(" (" + count + ")");
        }

        System.out.println("\n--- Login Attempts ---");
        for (Map.Entry<String, Integer> entry : loginAttempts.entrySet()) {
            String username = entry.getKey();
            int attempts = entry.getValue();
            System.out.printf("%-10s: ", username);
            for (int i = 0; i < attempts; i++) {
                System.out.print("█");
            }
            System.out.println(" (" + attempts + ")");
        }
    }
}
