import java.util.*;
import java.util.concurrent.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureSystemCallInterface {

    private static final Map<String, String> userDatabase = new ConcurrentHashMap<>();
    private static final List<String> systemCallLogs = new CopyOnWriteArrayList<>();

    static {
        userDatabase.put("admin", hashPassword("password123"));
        userDatabase.put("user1", hashPassword("pass1"));
        userDatabase.put("user2", hashPassword("securePass2"));
        userDatabase.put("guest", hashPassword("guest123"));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Secure System Call Interface ---");

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (authenticateUser(username, password)) {
            System.out.println("Authentication Successful!");
            System.out.println("1. Perform System Call\n2. View Logs\n3. Exit");

            while (true) {
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (choice == 1) {
                    performSystemCall(username);
                } else if (choice == 2) {
                    viewLogs();
                } else if (choice == 3) {
                    System.out.println("Exiting...");
                    break;
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            }
        } else {
            System.out.println("Authentication Failed. Access Denied.");
        }

        scanner.close();
    }

    private static boolean authenticateUser(String username, String password) {
        return userDatabase.containsKey(username) && userDatabase.get(username).equals(hashPassword(password));
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password");
        }
    }

    private static void performSystemCall(String username) {
        String systemCall = "Checked Disk Space";
        logSystemCall(username, systemCall);
        System.out.println("System Call Performed: " + systemCall);
    }

    private static void logSystemCall(String username, String action) {
        String logEntry = "User: " + username + " | Action: " + action + " | Timestamp: " + new Date().toString();
        systemCallLogs.add(logEntry);
    }

    private static void viewLogs() {
        System.out.println("--- System Call Logs ---");
        for (String log : systemCallLogs) {
            System.out.println(log);
        }
    }
}
