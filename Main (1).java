


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;

public class Main {

    static class User {
        String username;
        String password;

        User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    static class Product {
        String name;
        double price;
        int quantity;

        Product(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        double getTotalPrice() {
            return price * quantity;
        }

        public String toString() {
            return quantity + " x " + name + " @ P" + String.format("%.2f", price) + " = P" + String.format("%.2f", getTotalPrice());
        }
    }

    static class CashRegister {
        private List<Product> cart = new ArrayList<>();
        private final Scanner scanner = new Scanner(System.in);
        private final String cashier;

        CashRegister(String username) {
            this.cashier = username;
        }

        void addProduct() {
            System.out.println("\n[ADD PRODUCT]");
            String name;
            double price = -1;
            int quantity = -1;

            System.out.print("Enter product name: ");
            name = scanner.nextLine().trim();

            while (price <= 0) {
                System.out.print("Enter product price: ");
                try {
                    price = Double.parseDouble(scanner.nextLine());
                    if (price <= 0) System.out.println("Price must be > 0.");
                } catch (Exception e) {
                    System.out.println("Invalid price input. Try again.");
                }
            }

            while (quantity <= 0) {
                System.out.print("Enter product quantity: ");
                try {
                    quantity = Integer.parseInt(scanner.nextLine());
                    if (quantity <= 0) System.out.println("Quantity must be > 0.");
                } catch (Exception e) {
                    System.out.println("Invalid quantity input. Try again.");
                }
            }

            cart.add(new Product(name, price, quantity));
            System.out.println("Product added: " + name);
        }

        void displayCart() {
            System.out.println("\n[CURRENT CART]");
            if (cart.isEmpty()) {
                System.out.println("Cart is empty.");
                return;
            }
            double total = 0;
            for (int i = 0; i < cart.size(); i++) {
                System.out.println((i + 1) + ". " + cart.get(i));
                total += cart.get(i).getTotalPrice();
            }
            System.out.println("Total: P" + String.format("%.2f", total));
        }

        void removeProduct() {
            displayCart();
            if (cart.isEmpty()) return;

            System.out.print("Enter item number to remove: ");
            try {
                int idx = Integer.parseInt(scanner.nextLine()) - 1;
                if (idx >= 0 && idx < cart.size()) {
                    System.out.println("Removed: " + cart.get(idx).name);
                    cart.remove(idx);
                } else {
                    System.out.println("Invalid item number.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
            }
        }

        void updateQuantity() {
            displayCart();
            if (cart.isEmpty()) return;

            System.out.print("Enter item number to update: ");
            try {
                int idx = Integer.parseInt(scanner.nextLine()) - 1;
                if (idx >= 0 && idx < cart.size()) {
                    System.out.print("Enter new quantity: ");
                    int newQty = Integer.parseInt(scanner.nextLine());
                    if (newQty > 0) {
                        cart.get(idx).quantity = newQty;
                        System.out.println("Quantity updated.");
                    } else {
                        System.out.println("Quantity must be > 0.");
                    }
                } else {
                    System.out.println("Invalid item number.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
            }
        }

        double calculateTotal() {
            return cart.stream().mapToDouble(Product::getTotalPrice).sum();
        }

        void checkout() {
            double total = calculateTotal();
            if (cart.isEmpty()) {
                System.out.println("Cannot checkout. Cart is empty.");
                return;
            }
            displayCart();

            double payment = 0;
            while (payment < total) {
                try {
                    System.out.print("Enter payment amount: ");
                    payment = Double.parseDouble(scanner.nextLine());
                    if (payment < total) {
                        System.out.println("Insufficient. P" + String.format("%.2f", (total - payment)) + " more needed.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid amount. Try again.");
                }
            }

            double change = payment - total;
            System.out.println("Change: P" + String.format("%.2f", change));
            logTransaction(total, payment, change);
            cart.clear();
        }

        void logTransaction(double total, double payment, double change) {
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("transactions.txt", true)))) {
                pw.println("=== TRANSACTION RECORD ===");
                pw.println("Cashier: " + cashier);
                pw.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                pw.println("Items:");
                for (Product p : cart) {
                    pw.println("- " + p);
                }
                pw.printf("TOTAL: P%.2f\n", total);
                pw.printf("PAID: P%.2f\n", payment);
                pw.printf("CHANGE: P%.2f\n", change);
                pw.println("===========================\n");
            } catch (IOException e) {
                System.out.println("Error logging transaction.");
            }
        }

        void start() {
            while (true) {
                System.out.println("\n[MENU] 1-Add 2-View 3-Remove 4-Update 5-Checkout 6-Exit");
                System.out.print("Select option: ");
                String input = scanner.nextLine();
                switch (input) {
                    case "1": addProduct(); break;
                    case "2": displayCart(); break;
                    case "3": removeProduct(); break;
                    case "4": updateQuantity(); break;
                    case "5": checkout(); break;
                    case "6": return;
                    default: System.out.println("Invalid option. Try 1–6.");
                }
            }
        }
    }

    static List<User> users = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Final Cash Register System!");
        while (true) {
            System.out.print("\n1-Sign Up | 2-Login | 3-Exit\nEnter choice: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": signUp(); break;
                case "2":
                    String user = login();
                    if (user != null) new CashRegister(user).start();
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid. Choose 1–3.");
            }
        }
    }

    static void signUp() {
        String username, password;

        while (true) {
            try {
                System.out.print("Enter username (5-15 characters): ");
                username = scanner.nextLine().trim();

                if (username.matches("^.{5,15}$")) {
                    boolean exists = false;
                    for (User u : users) {
                        if (u.username.equals(username)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) break;
                    else System.out.println("Username already exists. Please choose a different one.");
                } else {
                    System.out.println("Invalid username. Must be 5-15 characters long.");
                }
            } catch (Exception e) {
                System.out.println("Error reading input. Please try again.");
            }
        }

        while (true) {
            try {
                System.out.print("Enter password (8-20 characters, 1 uppercase, 1 number): ");
                password = scanner.nextLine();

                if (password.matches("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,20}$")) {
                    break;
                } else {
                    System.out.println("Invalid password. Must include 1 uppercase letter and 1 number.");
                }
            } catch (Exception e) {
                System.out.println("Error reading input. Please try again.");
            }
        }

        users.add(new User(username, password));
        System.out.println("Signup successful!");
    }

    static String login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                System.out.println("Login successful! Welcome, " + username + "!");
                return username;
            }
        }

        System.out.println("Login failed. Invalid credentials.");
        return null;
    }

}
