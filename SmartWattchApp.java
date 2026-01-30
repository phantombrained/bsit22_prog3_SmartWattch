import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

class Appliance {
    String name;
    double watts;
    double hours;

    Appliance(String name, double watts, double hours) {
        this.name = name;
        this.watts = watts;
        this.hours = hours;
    }

    public String getRank() {
        if (watts < 100) return "High Efficiency (Low Power)";
        if (watts <= 500) return "Moderate Power Draw";
        return "Low Efficiency (High Power Draw)";
    }
}

class User {
    String username, password;
    int streak = 1; 
    double goalKwh = 0;
    double rate = 12.0; 
    ArrayList<Appliance> list = new ArrayList<>();

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

public class SmartWattchApp {
    private static final Scanner sc = new Scanner(System.in);
    private static User current = null;

    public static void main(String[] args) {
        while (true) {
            if (current == null) {
                System.out.println("\n--- SMARTWATTCH LOGIN ---");
                System.out.println("1. Sign Up\n2. Sign In\n3. Exit");
                int choice = inputInt("Select: ", 1, 3);

                if (choice == 1) register();
                else if (choice == 2) login();
                else System.exit(0);
            } else {
                menu();
            }
        }
    }

    private static void register() {
        System.out.print("Username: ");
        String u = sc.nextLine().trim();
        System.out.print("Password: ");
        String p1 = sc.nextLine();
        System.out.print("Confirm: ");
        String p2 = sc.nextLine();

        if (!u.isEmpty() && p1.equals(p2) && p1.length() >= 4) {
            saveToDatabase(u, p1);
        } else {
            System.out.println("Invalid input or password mismatch.");
        }
    }

    private static void saveToDatabase(String u, String p) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u);
            pstmt.setString(2, p);
            pstmt.executeUpdate();
            System.out.println("Account saved to MySQL!");
        } catch (SQLException e) {
            System.out.println("Registration Error: " + e.getMessage());
        }
    }

    private static void login() {
        System.out.print("Username: ");
        String u = sc.nextLine();
        System.out.print("Password: ");
        String p = sc.nextLine();

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, u);
            pstmt.setString(2, p);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                current = new User(rs.getString("username"), rs.getString("password"));
                current.streak = rs.getInt("login_streak");
                current.goalKwh = rs.getDouble("monthly_goal_kwh");
                current.rate = rs.getDouble("electricity_rate");
                
                updateStreak(u);
                loadAppliances(u);
                System.out.println("Logged in successfully!");
            } else {
                System.out.println("Invalid credentials.");
            }
        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }
    }

    private static void menu() {
        System.out.println("\n--- DASHBOARD | Streak: " + current.streak + " Days ---");
        System.out.println("1. Add Appliance");
        System.out.println("2. Electricity Rate (P" + current.rate + "/kWh)");
        System.out.println("3. Set Monthly Goal");
        System.out.println("4. Analytics & Insights");
        System.out.println("5. Delete Appliance");
        System.out.println("6. Sign Out");
        
        int choice = inputInt("Action: ", 1, 6);
        switch (choice) {
            case 1 -> add();
            case 2 -> {
                current.rate = inputDouble("New Rate (PHP): ", 1, 100);
                updatePreference("electricity_rate", current.rate);
            }
            case 3 -> {
                current.goalKwh = inputDouble("Goal (kWh): ", 1, 10000);
                updatePreference("monthly_goal_kwh", current.goalKwh);
            }
            case 4 -> showAnalytics();
            case 5 -> deleteAppliance();
            case 6 -> current = null;
        }
    }

    private static void add() {
        System.out.print("Device Name: ");
        String name = sc.nextLine();
        double w = inputDouble("Watts: ", 0.1, 10000);
        double h = inputDouble("Hours/Day: ", 0.1, 24);

        String sql = "INSERT INTO appliances (user_id, appliance_name, watts, hours_per_day) " +
                     "VALUES ((SELECT user_id FROM users WHERE username = ?), ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, current.username);
            pstmt.setString(2, name);
            pstmt.setDouble(3, w);
            pstmt.setDouble(4, h);
            pstmt.executeUpdate();
            
            current.list.add(new Appliance(name, w, h));
            System.out.println("Appliance saved permanently.");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    private static void updateStreak(String username) {
        String sql = "UPDATE users SET login_streak = login_streak + 1 WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    private static void loadAppliances(String username) {
        String sql = "SELECT * FROM appliances WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                current.list.add(new Appliance(rs.getString("appliance_name"), rs.getDouble("watts"), rs.getDouble("hours_per_day")));
            }
        } catch (SQLException e) { }
    }

    private static void updatePreference(String column, double value) {
        String sql = "UPDATE users SET " + column + " = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, value);
            pstmt.setString(2, current.username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database Update Error: " + e.getMessage());
        }
    }

    private static void deleteAppliance() {
        System.out.print("Enter exact Name of the appliance to delete: ");
        String name = sc.nextLine();

        String sql = "DELETE FROM appliances WHERE appliance_name = ? AND user_id = (SELECT user_id FROM users WHERE username = ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, current.username);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                current.list.removeIf(a -> a.name.equalsIgnoreCase(name));
                System.out.println("Appliance deleted from database.");
            } else {
                System.out.println("Appliance not found.");
            }
        } catch (SQLException e) {
            System.out.println("Delete Error: " + e.getMessage());
        }
    }

    // --- Analytics ---
    private static void showAnalytics() {
        if (current.list.isEmpty()) {
            System.out.println("Add appliances first.");
            return;
        }

        double totalDailyKwh = 0, totalW = 0, totalH = 0;
        System.out.println("\n--- DETAILED ANALYTICS ---");
        
        for (Appliance a : current.list) {
            double dKwh = (a.watts * a.hours) / 1000;
            totalDailyKwh += dKwh;
            totalW += a.watts;
            totalH += a.hours;
            System.out.printf("- %s: %.2f kWh/day | %s\n", a.name, dKwh, a.getRank());
        }

        double mKwh = totalDailyKwh * 30;
        double yKwh = totalDailyKwh * 365;
        double mCost = mKwh * current.rate;

        System.out.println("-------------------------");
        System.out.printf("Total Load: %.0f Watts\n", totalW);
        System.out.printf("Average Daily Usage: %.1f Hours\n", (totalH / current.list.size()));
        System.out.printf("Usage: %.2f Daily | %.2f Monthly | %.2f Yearly (kWh)\n", totalDailyKwh, mKwh, yKwh);
        System.out.printf("Est. Cost: PHP %.2f/mo | PHP %.2f/yr\n", mCost, (yKwh * current.rate));
        
        if (current.goalKwh > 0) {
            double diff = current.goalKwh - mKwh;
            if (diff >= 0) {
                System.out.printf("Goal Status: Under by %.2f kWh (Saved P%.2f)\n", diff, (diff * current.rate));
            } else {
                System.out.printf("Goal Status: Over by %.2f kWh (Extra P%.2f)\n", Math.abs(diff), (Math.abs(diff) * current.rate));
            }
        }

        System.out.println("\n--- USAGE INSIGHTS ---");
        if (totalH > 12) System.out.println("* Heavy usage detected. Check for standby devices.");
        if (totalW > 1500) System.out.println("* High power draw. Check heating/cooling appliances.");
        System.out.println("* Tip: Unplugging small electronics can save ~10% monthly.");
    }

    // --- Input Validation ---
    private static int inputInt(String p, int min, int max) {
        while (true) {
            try {
                System.out.print(p);
                int v = Integer.parseInt(sc.nextLine());
                if (v >= min && v <= max) return v;
            } catch (Exception e) {}
            System.out.println("Invalid choice.");
        }
    }

    private static double inputDouble(String p, double min, double max) {
        while (true) {
            try {
                System.out.print(p);
                double v = Double.parseDouble(sc.nextLine());
                if (v >= min && v <= max) return v;
            } catch (Exception e) {}
            System.out.println("Invalid numeric value.");
        }
    }
}