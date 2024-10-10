
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        List<Product> products = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        try {
            // อ่านข้อมูลพนักงาน
            FileReader employeeReader = new FileReader("employees.json");
            JSONArray employeesJsonArray = (JSONArray) parser.parse(employeeReader);
            Employee[] employees = new Employee[employeesJsonArray.size()];

            for (int i = 0; i < employeesJsonArray.size(); i++) {
                JSONObject employeeObject = (JSONObject) employeesJsonArray.get(i);
                String id = (String) employeeObject.get("id");
                String name = (String) employeeObject.get("name");
                String position = (String) employeeObject.get("position");
                String username = (String) employeeObject.get("username");
                String password = (String) employeeObject.get("password");

                employees[i] = new Employee(id, name, position, username, password);
            }

            // อ่านข้อมูลสินค้า
            FileReader productReader = new FileReader("Products.json");
            JSONArray productsJsonArray = (JSONArray) parser.parse(productReader);

            for (int i = 0; i < productsJsonArray.size(); i++) {
                JSONObject productObject = (JSONObject) productsJsonArray.get(i);
                String productId = (String) productObject.get("productId");
                String productName = (String) productObject.get("name");
                double price = (Double) productObject.get("price");
                long stock = (Long) productObject.get("stock");

                Product product = new Product(productId, productName, price, (int) stock);
                products.add(product);
            }

            // เริ่มต้นการทำงานของระบบ
            while (true) {
                System.out.println("\n=== Welcome to the System ===");
                System.out.print("Enter username: ");
                String inputUsername = scanner.nextLine();

                System.out.print("Enter password: ");
                String inputPassword = scanner.nextLine();

                boolean isLoginSuccessful = false;
                Employee loggedInEmployee = null;

                for (int i = 0; i < employees.length; i++) {
                    Employee emp = employees[i];
                    if (emp.login(inputUsername, inputPassword)) {
                        System.out.println("Login successful! Welcome " + emp.getName());
                        isLoginSuccessful = true;
                        loggedInEmployee = emp;
                        break;
                    }
                }

                if (!isLoginSuccessful) {
                    clearScreen();
                    System.out.println("\nLogin failed! Incorrect username or password.");
                } else {
                    boolean exit = false;
                    while (!exit) {
                        clearScreen();
                        System.out.println("\n=== Main Menu ===");
                        System.out.println("1. Manage Products");
                        System.out.println("2. Purchase Products");
                        System.out.println("3. Manage Personal Information");
                        System.out.println("4. Logout");
                        System.out.print("Choose an option: ");
                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        switch (choice) {
                            case 1:
                                clearScreen();
                                manageProducts(scanner, products);
                                break;
                            case 2:
                                clearScreen();
                                purchaseProducts(scanner, products, loggedInEmployee);
                                break;
                            case 3:
                                clearScreen();
                                List<Employee> employeeList = Arrays.asList(employees);
                                managePersonalInfo(scanner, loggedInEmployee, employeeList);
                                break;
                            case 4:
                                clearScreen();
                                loggedInEmployee = null;
                                exit = true;
                                break;
                            default:
                                System.out.println("Invalid option. Please try again.");
                                break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

// ฟังก์ชันสำหรับจัดการสินค้า
    private static void manageProducts(Scanner scanner, List<Product> products) {
        boolean backToMainMenu = false;

        while (!backToMainMenu) {
            clearScreen();
            System.out.println("=== Manage Products ===");
            System.out.println("1. Add Product");
            System.out.println("2. Remove Product");
            System.out.println("3. Edit Product");
            System.out.println("4. View Products");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addProduct(scanner, products);
                    break;
                case 2:
                    removeProduct(scanner, products);
                    break;
                case 3:
                    editProduct(scanner, products);
                    break;
                case 4:
                    viewProducts(products);
                    break;
                case 0:
                    backToMainMenu = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

            if (!backToMainMenu) {
                System.out.println("\nPress Enter to return to the Manage Products menu...");
                scanner.nextLine();
            }
        }
    }

    private static void addProduct(Scanner scanner, List<Product> products) {
        System.out.println("\n=== Add Product ===");
        System.out.print("Enter Product ID: ");
        String productId = scanner.nextLine();
        System.out.print("Enter Product Name: ");
        String productName = scanner.nextLine();
        System.out.print("Enter Product Price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter Product Stock: ");
        int stock = scanner.nextInt();
        scanner.nextLine();

        Product newProduct = new Product(productId, productName, price, stock);
        products.add(newProduct);
        Product.updateProductsJson(products);
        System.out.println("Product added successfully.");
    }

    private static void removeProduct(Scanner scanner, List<Product> products) {
        System.out.println("\n=== Remove Product ===");
        System.out.print("Enter Product ID to remove: ");
        String productId = scanner.nextLine();

        boolean removed = false;
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (product.getProductId().equalsIgnoreCase(productId)) {
                products.remove(i);
                Product.updateProductsJson(products);
                System.out.println("Product removed successfully.");
                removed = true;
                break;
            }
        }

        if (!removed) {
            System.out.println("Product not found.");
        }
    }

    private static void editProduct(Scanner scanner, List<Product> products) {
        System.out.println("\n=== Edit Product ===");
        System.out.print("Enter Product ID to edit: ");
        String productId = scanner.nextLine();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (product.getProductId().equalsIgnoreCase(productId)) {
                System.out.println("Editing Product: " + product.getName());
                System.out.print("Enter new name (or press Enter to keep current): ");
                String newName = scanner.nextLine();
                System.out.print("Enter new price (or press Enter to keep current): ");
                String newPrice = scanner.nextLine();
                System.out.print("Enter new stock (or press Enter to keep current): ");
                String newStock = scanner.nextLine();

                if (!newName.isEmpty()) {
                    product.setName(newName);
                }
                if (!newPrice.isEmpty()) {
                    try {
                        double price = Double.parseDouble(newPrice);
                        product.setPrice(price);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price. Keeping the current price.");
                    }
                }
                if (!newStock.isEmpty()) {
                    try {
                        int stock = Integer.parseInt(newStock);
                        product.setStock(stock);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid stock. Keeping the current stock.");
                    }
                }
                Product.updateProductsJson(products);
                System.out.println("Product updated successfully.");
                return;
            }
        }

        System.out.println("Product not found.");
    }

    private static void viewProducts(List<Product> products) {
        System.out.println("\n=== View Products ===");
        if (products.isEmpty()) {
            System.out.println("No products available.");
        } else {
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                System.out.println("ID: " + product.getProductId() + ", Name: " + product.getName()
                        + ", Price: " + product.getPrice() + " THB, Stock: " + product.getStock());
            }
        }
    }

    // ฟังก์ชันสำหรับการซื้อสินค้า
    private static void purchaseProducts(Scanner scanner, List<Product> products, Employee employee) {
        System.out.println("=== Purchase Products ===");

        List<Product> purchasedProducts = new ArrayList<>();
        List<Integer> purchasedQuantities = new ArrayList<>();

        boolean continuePurchasing = true;
        while (continuePurchasing) {
            System.out.println("\nAvailable Products:");
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                System.out.println("Product ID: " + product.getProductId() + ", Name: " + product.getName()
                        + ", Price: " + product.getPrice() + " THB, Stock: " + product.getStock());
            }

            System.out.print("Choose a product to purchase (by Product ID) or type 'done' to finish: ");
            String productId = scanner.nextLine();

            if (productId.equalsIgnoreCase("done")) {
                continuePurchasing = false;
                System.out.println("Finished selecting products.");
            } else {
                Product selectedProduct = null;

                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    if (product.getProductId().equalsIgnoreCase(productId)) {
                        selectedProduct = product;
                        break;
                    }
                }

                if (selectedProduct != null) {
                    System.out.print("Enter quantity to purchase: ");
                    try {
                        int quantity = Integer.parseInt(scanner.nextLine());

                        if (quantity > selectedProduct.getStock()) {
                            System.out.println("Not enough stock available.");
                        } else if (quantity <= 0) {
                            System.out.println("Please enter a valid quantity.");
                        } else {
                            purchasedProducts.add(selectedProduct);
                            purchasedQuantities.add(quantity);
                            System.out.println("Added to cart: " + quantity + " of " + selectedProduct.getName());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity. Please enter a valid number.");
                    }
                } else {
                    System.out.println("Invalid Product ID. Please try again.");
                }
            }
        }

        // สร้าง Transaction สำหรับสินค้าทั้งหมดที่ซื้อ
        if (!purchasedProducts.isEmpty()) {
            String transactionId = "T" + System.currentTimeMillis();
            Transaction transaction = new Transaction(transactionId, employee, purchasedProducts, purchasedQuantities);
            clearScreen();
            System.out.println("");
            // แสดงบิลก่อนกลับไปที่เมนูหลัก
            System.out.println(transaction.getBillContent());

            // อัปเดตข้อมูลสินค้าลงไฟล์ JSON โดยเรียกใช้เมธอดที่อยู่ในคลาส Product
            Product.updateProductsJson(products);

            System.out.println("Press Enter to return to the main menu...");
            scanner.nextLine();
        } else {
            System.out.println("No items were purchased.");
        }

    }

    private static void managePersonalInfo(Scanner scanner, Employee employee, List<Employee> employees) {
        boolean backToMainMenu = false;
        while (!backToMainMenu) {
            clearScreen();
            System.out.println("=== Manage Personal Information ===");
            System.out.println("Current Information:");
            System.out.println("Name: " + employee.getName());
            System.out.println("Position: " + employee.getPosition());
            System.out.println("Username: " + employee.getUsername());
            System.out.println("-----------------------------------");
            System.out.println("1. Edit Name");
            System.out.println("2. Edit Position");
            System.out.println("3. Edit Username");
            System.out.println("4. Edit Password");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            boolean updated = false;

            switch (choice) {
                case 1:
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    if (!newName.isEmpty()) {
                        employee.setName(newName);
                        System.out.println("Name updated successfully.");
                        updated = true;
                    } else {
                        System.out.println("No changes made to name.");
                    }
                    break;
                case 2:
                    System.out.print("Enter new position: ");
                    String newPosition = scanner.nextLine();
                    if (!newPosition.isEmpty()) {
                        employee.setPosition(newPosition);
                        System.out.println("Position updated successfully.");
                        updated = true;
                    } else {
                        System.out.println("No changes made to position.");
                    }
                    break;
                case 3:
                    System.out.print("Enter new username: ");
                    String newUsername = scanner.nextLine();
                    if (!newUsername.isEmpty()) {
                        employee.setUsername(newUsername);
                        System.out.println("Username updated successfully.");
                        updated = true;
                    } else {
                        System.out.println("No changes made to username.");
                    }
                    break;
                case 4:
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();
                    if (!newPassword.isEmpty()) {
                        employee.setPassword(newPassword);
                        System.out.println("Password updated successfully.");
                        updated = true;
                    } else {
                        System.out.println("No changes made to password.");
                    }
                    break;
                case 0:
                    backToMainMenu = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

            // อัปเดตข้อมูลในไฟล์ JSON หากมีการเปลี่ยนแปลง
            if (updated) {
                Employee.updateEmployeesJson(employees);
            }

            if (!backToMainMenu) {
                System.out.println("\nPress Enter to return to the Manage Personal Information menu...");
                scanner.nextLine();
            }
        }
    }

    public final static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (final Exception e) {
            System.out.println("Error while clearing console: " + e.getMessage());
        }
    }
}
