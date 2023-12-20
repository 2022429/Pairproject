/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kuldeepshukla
 */
// Github Link https://github.com/2022429/Pairproject/blob/master/Pairproject/src/Pairproject.java


import java.sql.*;
import java.util.Scanner;

public class Pairproject {
   



    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/ca50";
    static final String USER = "root";
    static final String PASS = "password";

    static Connection conn = null;
    static Statement stmt = null;
    
    static final double PRSI_RATE = 0.04; // 4% PRSI rate
    static final double USC_RATE = 0.02;  // 2% USC rate

    public static void main(String[] args) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create tables if not exists
            createTables();

            // Display menu based on user/admin
              Scanner scanner = new Scanner(System.in);
            System.out.println("Are you an admin or a user? (admin/user)");
            String userType = scanner.next();

            if (userType.equalsIgnoreCase("admin")) {
                adminMenu();
            } else if (userType.equalsIgnoreCase("user")) {
                userMenu();
            } else {
                System.out.println("Invalid choice. Exiting...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    private static void createTables() {
    try {
        stmt = conn.createStatement();

        // Create User table
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) NOT NULL," +
                "name VARCHAR(255) NOT NULL," +
                "lastname VARCHAR(255) NOT NULL)";
        
        stmt.executeUpdate(createUserTableSQL);

        // Create Work table
      String createWorkTableSQL = "CREATE TABLE IF NOT EXISTS works (" +
        "id INT AUTO_INCREMENT PRIMARY KEY," +
        "user_id INT," +
        "work_description VARCHAR(255)," +
        "tax_amount DOUBLE," +
        "prsi_amount DOUBLE," +
        "usc_amount DOUBLE," +
        "FOREIGN KEY (user_id) REFERENCES users(id))";
        stmt.executeUpdate(createWorkTableSQL);

        System.out.println("Tables created successfully.");
    } catch (SQLException se) {
        se.printStackTrace();
    }
}
    private static void adminMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter admin username: ");
        String adminUsername = scanner.next();

        System.out.println("Enter admin password: ");
        String adminPassword = scanner.next();

        if (adminUsername.equals("CCT") && adminPassword.equals("Dublin")) {
            System.out.println("Admin login successful.");

            while (true) {
                System.out.println("\nAdmin Menu:");
                System.out.println("1. View all users");
                System.out.println("2. Modify user data");
                System.out.println("3. Delete user");
                System.out.println("4. View all user works");
                System.out.println("5. Modify admin details");
            System.out.println("6. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        viewAllUsers();
                        break;
                    case 2:
                        modifyUserData();
                        break;
                    case 3:
                        deleteUser();
                        break;
                    case 4:
                        viewAllUserWorks();
                        break;
                    case 5:
                    modifyAdminDetails(adminUsername);
                    break;
                case 6:
                    System.out.println("Exiting admin menu.");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
                }
            }
        } else {
            System.out.println("Invalid admin credentials. Exiting...");
        }
    }

    private static void modifyAdminDetails(String adminUsername) {
    try {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the new admin password: ");
        String newAdminPassword = scanner.next();

        String modifyAdminSQL = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(modifyAdminSQL)) {
            preparedStatement.setString(1, newAdminPassword);
            preparedStatement.setString(2, adminUsername);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Admin details modified successfully.");
            } else {
                System.out.println("Admin not found or modification unsuccessful.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    private static void viewAllUsers() {
    try {
        String selectAllUsersSQL = "SELECT * FROM users";
        try (PreparedStatement preparedStatement = conn.prepareStatement(selectAllUsersSQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String name = resultSet.getString("name");
                String lastName = resultSet.getString("lastname");

                System.out.println("User ID: " + userId +
                        ", Username: " + username +
                        ", Email: " + email +
                        ", Password: " + password +
                        ", Name: " + name +
                        ", Last Name: " + lastName);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    private static void modifyUserData() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the username of the user you want to modify: ");
            String usernameToModify = scanner.next();

            System.out.println("Enter the new email address: ");
            String newEmail = scanner.next();

            String modifyUserSQL = "UPDATE users SET email = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(modifyUserSQL)) {
                preparedStatement.setString(1, newEmail);
                preparedStatement.setString(2, usernameToModify);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User data modified successfully.");
                } else {
                    System.out.println("User not found or modification unsuccessful.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteUser() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the username of the user you want to delete: ");
            String usernameToDelete = scanner.next();

            String deleteUserSQL = "DELETE FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(deleteUserSQL)) {
                preparedStatement.setString(1, usernameToDelete);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User deleted successfully.");
                } else {
                    System.out.println("User not found or deletion unsuccessful.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewAllUserWorks() {
         try {
        String selectAllUserWorksSQL = "SELECT * FROM works";
        try (PreparedStatement preparedStatement = conn.prepareStatement(selectAllUserWorksSQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int workId = resultSet.getInt("id");
                int userId = resultSet.getInt("user_id");
                String workDescription = resultSet.getString("work_description");
                double taxAmount = resultSet.getDouble("tax_amount");

                System.out.println("Work ID: " + workId +
                        ", User ID: " + userId +
                        ", Description: " + workDescription +
                        ", Tax Amount: " + taxAmount);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

   private static boolean isUsernameTaken(String username) {
        try {
            String checkUsernameSQL = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(checkUsernameSQL)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next(); // If the result set has a next entry, the username is taken
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return false; // Return false in case of an exception
        }
    }

    private static void userSignup() {
        try {
            // Implement user registration (signup) logic here
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter a new username: ");
            String newUsername = scanner.next();

            // Check if the username already exists
            if (isUsernameTaken(newUsername)) {
                System.out.println("Username already taken. Please choose another one.");
                return;
            }

            System.out.println("Enter your email: ");
            String email = scanner.next();

            System.out.println("Enter a password: ");
            String newPassword = scanner.next();

            System.out.println("Enter your name: ");
            String name = scanner.next();

            System.out.println("Enter your last name: ");
            String lastName = scanner.next();

            // Insert the new user into the 'users' table
            String insertUserSQL = "INSERT INTO users (username, email, password, name, lastname) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(insertUserSQL)) {
                preparedStatement.setString(1, newUsername);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, newPassword);
                preparedStatement.setString(4, name);
                preparedStatement.setString(5, lastName);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User signed up successfully.");
                } else {
                    System.out.println("User signup unsuccessful.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    private static boolean validateUserCredentials(String username, String password) {
        try {
            String selectUserSQL = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(selectUserSQL)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return false;
        }
    }

   private static void userMenu() {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Are you a new user or an existing user? (new/existing)");
    String userChoice = scanner.next();

    if (userChoice.equalsIgnoreCase("new")) {
        userSignup();
    } else if (userChoice.equalsIgnoreCase("existing")) {
        boolean exitUserMenu = false;

        while (!exitUserMenu) {
            System.out.println("Enter your username: ");
            String username = scanner.next();

            System.out.println("Enter your password: ");
            String password = scanner.next();

            System.out.println("Welcome, " + username + "! You are now in the Tax Calculation Area.");

            // Get user's income (you may replace this with actual income retrieval logic)
            System.out.println("Enter your annual income: ");
            double income = scanner.nextDouble();

            // Calculate tax
            double taxAmount = calculateTax(income);
            System.out.println("Your estimated tax for the year: " + taxAmount);

            // Save tax calculation to the "works" table
             saveTaxCalculation(username, taxAmount, income * PRSI_RATE, income * USC_RATE);
           

            // Validate user credentials
            if (validateUserCredentials(username, password)) {
                System.out.println("User login successful.");
                
                while (true) {
                    System.out.println("\nUser Menu:");
                    System.out.println("1. View your details");
                    System.out.println("2. Modify your details");
                    System.out.println("3. View your works");
                    System.out.println("4. Delete your work");
                    System.out.println("5. Exit");
                    
                    System.out.println("Enter your choice: ");
                    int choice = scanner.nextInt();
                    
                    switch (choice) {
                        case 1:
                            viewUserDetails(username);
                            break;
                        case 2:
                            modifyUserDetails(username);
                            break;
                        case 3:
                            viewUserWorks(username);
                            break;
                        case 4:
                            deleteUserWork(username);
                            break;
                        case 5:
                            System.out.println("Exiting user menu.");
                            exitUserMenu = true; // Set the flag to exit the outer loop
                            break;
                        default:
                            System.out.println("Invalid choice. Try again.");
                    }
                    
                    if (exitUserMenu) {
                        break; // Exit the inner loop as well
                    }
                }
            } else {
                System.out.println("Invalid user credentials. Exiting...");
            }
        }
    } else {
        System.out.println("Invalid choice. Exiting...");
    }
}
   private static double calculateTax(double income) {
    // Simplified tax, PRSI, and USC calculation for demonstration purposes
    // Replace this with actual tax, PRSI, and USC calculation logic
    
    double prsiAmount = income * PRSI_RATE;
    double uscAmount = income * USC_RATE;
    double taxableIncome = income - prsiAmount - uscAmount;
    double taxRate = 0.20;  // 20% tax rate
    double taxAmount = taxableIncome * taxRate;

    System.out.println("PRSI: " + prsiAmount);
    System.out.println("USC: " + uscAmount);

    return taxAmount;
}

private static void saveTaxCalculation(String username, double taxAmount, double prsiAmount, double uscAmount) {
    try {
        // Get user ID from the "users" table
        int userId = getUserIdByUsername(username);

        // Insert the tax calculation into the "works" table
       String insertWorkSQL = "INSERT INTO works (user_id, work_description, tax_amount, prsi_amount, usc_amount) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(insertWorkSQL)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, "Tax Calculation");
            preparedStatement.setDouble(3, taxAmount);
            preparedStatement.setDouble(4, prsiAmount);
            preparedStatement.setDouble(5, uscAmount);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Tax calculation saved.");
            } else {
                System.out.println("Failed to save tax calculation.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle the exception appropriately
    }
}


    private static int getUserIdByUsername(String username) throws SQLException {
        String selectUserIdSQL = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(selectUserIdSQL)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new SQLException("User not found.");
            }
        }
    }   
    

    private static void viewUserDetails(String username) {
        try {
            String selectUserSQL = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(selectUserSQL)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int userId = resultSet.getInt("id");
                    String userEmail = resultSet.getString("email");

                    System.out.println("User ID: " + userId + ", Username: " + username + ", Email: " + userEmail);
                } else {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    private static void modifyUserDetails(String username) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the new email address: ");
            String newEmail = scanner.next();

            String modifyUserSQL = "UPDATE users SET email = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(modifyUserSQL)) {
                preparedStatement.setString(1, newEmail);
                preparedStatement.setString(2, username);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User data modified successfully.");
                } else {
                    System.out.println("User not found or modification unsuccessful.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    private static void viewUserWorks(String username) {
        try {
            String selectUserWorksSQL = "SELECT * FROM works WHERE user_id = (SELECT id FROM users WHERE username = ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(selectUserWorksSQL)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int workId = resultSet.getInt("id");
                    String workDescription = resultSet.getString("work_description");
                    double taxAmount = resultSet.getDouble("tax_amount");

                    System.out.println("Work ID: " + workId +
                            ", Description: " + workDescription + ", Tax Amount: " + taxAmount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();     
            
            
            // Handle the exception appropriately
        }
    }

    private static void deleteUserWork(String username) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Display user's works for reference
            viewUserWorks(username);

            // Get the work ID to be deleted
            System.out.println("Enter the ID of the work you want to delete:");
            int workIdToDelete = scanner.nextInt();

            // Delete the work
            String deleteWorkSQL = "DELETE FROM works WHERE id = ? AND user_id = (SELECT id FROM users WHERE username = ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(deleteWorkSQL)) {
                preparedStatement.setInt(1, workIdToDelete);
                preparedStatement.setString(2, username);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Work deleted successfully.");
                } else {
                    System.out.println("Work not found or deletion unsuccessful.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    // Other methods...


}
    

