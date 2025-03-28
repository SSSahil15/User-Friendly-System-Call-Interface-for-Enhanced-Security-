import java.io.File;
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

        String username;
        String password;
        boolean authenticated = false;

        System.out.print("Enter Username: ");
        username = scanner.nextLine();

        // Initialize login attempts count if user is new
        loginAttempts.putIfAbsent(username, 0);

        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("Enter Password: ");
            password = scanner.nextLine();

            if (authenticateUser(username, password)) {
                System.out.println("Authentication Successful!");
                authenticated = true;
                runMenu(username, scanner);
                break; // Exit the login loop
            } else {
                loginAttempts.put(username, loginAttempts.get(username) + 1);
                int remainingAttempts = MAX_LOGIN_ATTEMPTS - loginAttempts.get(username);
                if (remainingAttempts > 0) {
                    System.out.println("Authentication Failed. Try again. (" + remainingAttempts + " attempts left)");
                } else {
                    System.out.println("Too many failed login attempts. Exiting...");
                    scanner.close();
                    return;
                }
            }
        }

        if (!authenticated) {
            scanner.close();
        }
    }

    private static boolean authenticateUser(String username, String password) {
        return userDatabase.containsKey(username) && userDatabase.get(username).equals(password);
    }

    private static void runMenu(String username, Scanner scanner) {
        System.out.println("1. Check Disk Space\n2. View Logs\n3. Show Usage Stats\n4. Exit");

        while (true) {
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
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void checkDiskSpace(String username) {
        File root = new File("/");
        long totalSpace = root.getTotalSpace(); // Total disk space
        long freeSpace = root.getFreeSpace();   // Free space
        long usedSpace = totalSpace - freeSpace; // Used space

        System.out.println("\n--- Disk Space Details ---");
        System.out.printf("Total Space : %.2f GB\n", totalSpace / 1e9);
        System.out.printf("Used Space  : %.2f GB\n", usedSpace / 1e9);
        System.out.printf("Free Space  : %.2f GB\n", freeSpace / 1e9);
        System.out.println("---------------------------");

        logSystemCall(username, "Checked Disk Space");
        systemCallCounts.put(username, systemCallCounts.getOrDefault(username, 0) + 1);
    }

    private static void logSystemCall(String username, String action) {
        String logEntry = "User: " + username + " | Action: " + action + " | Timestamp: " + new Date();
        systemCallLogs.add(logEntry);
    }

    private static void viewLogs() {
        System.out.println("--- System Call Logs ---");
        for (String log : systemCallLogs) {
            System.out.println(log);
        }
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
