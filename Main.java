
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Arrays;
import java.util.HashMap;
import java.time.format.DateTimeParseException;
import java.text.DecimalFormat;

public class Main {

    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        List<Product> products = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        try {
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

            FileReader productReader = new FileReader("Products.json");
            JSONArray productsJsonArray = (JSONArray) parser.parse(productReader);

            for (int i = 0; i < productsJsonArray.size(); i++) {
                JSONObject productObject = (JSONObject) productsJsonArray.get(i);
                String productId = (String) productObject.get("productId");
                String productName = (String) productObject.get("name");

                Object priceObj = productObject.get("price");
                double price;
                if (priceObj instanceof String) {
                    price = Double.parseDouble((String) priceObj);
                } else if (priceObj instanceof Double) {
                    price = (Double) priceObj;
                } else {
                    throw new IllegalArgumentException("Invalid price format");
                }

                int stock = ((Long) productObject.get("stock")).intValue();

                String importDateStr = (String) productObject.get("importDate");
                LocalDate importDate = LocalDate.parse(importDateStr, DateTimeFormatter.ISO_DATE);

                Product product = new Product(productId, productName, price, stock, importDate);
                products.add(product);
            }

            while (true) {
                clearScreen();
                System.out.println("===================================");
                System.out.println("    MAHANAKORN MINIMART LOGIN         ");
                System.out.println("===================================");

                System.out.print("  Enter username: ");
                String inputUsername = scanner.nextLine();

                System.out.print("  Enter password: ");
                String inputPassword = scanner.nextLine();

                boolean isLoginSuccessful = false;
                Employee loggedInEmployee = null;

                for (int i = 0; i < employees.length; i++) {
                    Employee emp = employees[i];
                    if (emp.login(inputUsername, inputPassword)) {
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
                        if ("Manager".equalsIgnoreCase(loggedInEmployee.getPosition())) {
                            System.out.println("4. Manage Employees");
                            System.out.println("5. View Sales Report");
                            System.out.println("6. Logout");
                        } else {
                            System.out.println("4. Logout");
                        }

                        int choice = -1;
                        boolean validInput = false;

                        while (!validInput) {
                            System.out.print("Choose an option: ");
                            if (scanner.hasNextInt()) {
                                choice = scanner.nextInt();
                                scanner.nextLine();
                                if ("Manager".equalsIgnoreCase(loggedInEmployee.getPosition())) {
                                    if (choice >= 1 && choice <= 6) {
                                        validInput = true;
                                    } else {
                                        System.out.println("Invalid option. Please choose a number between 1 and 6.");
                                    }
                                } else {
                                    if (choice >= 1 && choice <= 4) {
                                        validInput = true;
                                    } else {
                                        System.out.println("Invalid option. Please choose a number between 1 and 4.");
                                    }
                                }
                            } else {
                                System.out.println("Invalid input. Please enter a valid number.");
                                scanner.nextLine();
                            }
                        }

                        if ("Manager".equalsIgnoreCase(loggedInEmployee.getPosition())) {
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
                                    manageEmployees(scanner, employees);
                                    break;
                                case 5:
                                    clearScreen();
                                    generateMonthlyReport();
                                    break;
                                case 6:
                                    System.out.println("Logging out...");
                                    loggedInEmployee = null;
                                    exit = true;
                                    break;
                                default:
                                    System.out.println("Invalid option. Please try again.");
                                    break;
                            }
                        } else {
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
                                    System.out.println("Logging out...");
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
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void manageEmployees(Scanner scanner, Employee[] employees) {
        boolean backToMainMenu = false;

        while (!backToMainMenu) {
            clearScreen();
            System.out.println("===================================");
            System.out.println("          MANAGE EMPLOYEE        ");
            System.out.println("===================================");
            System.out.println("1. Add Employee");
            System.out.println("2. Remove Employee");
            System.out.println("3. Edit Employee");
            System.out.println("4. View Employees");
            System.out.println("0. Back to Main Menu");

            int choice = -1;
            boolean validInput = false;

            while (!validInput) {
                System.out.print("Choose an option: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice >= 0 && choice <= 4) {
                        validInput = true;
                    } else {
                        System.out.println("Invalid option. Please choose a number between 0 and 4.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.nextLine();
                }
            }

            switch (choice) {
                case 1:
                    addEmployee(scanner, employees);
                    break;
                case 2:
                    removeEmployee(scanner, employees);
                    break;
                case 3:
                    editEmployee(scanner, employees);
                    break;
                case 4:
                    viewEmployees(employees);
                    break;
                case 0:
                    backToMainMenu = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

            if (!backToMainMenu) {
                System.out.println("\nPress Enter to return to the Manage Employees menu...");
                scanner.nextLine();
            }
        }
    }

    private static void displayAllEmployees(Employee[] employees) {
        System.out.println("===================================");
        System.out.println("           LIST EMPLOYEE        ");
        System.out.println("===================================");
        if (employees.length == 0) {
            System.out.println("No employees available.");
        } else {
            for (int i = 0; i < employees.length; i++) {
                Employee emp = employees[i];
                System.out.println((i + 1) + ". ID: " + emp.getId() + " | Name: " + emp.getName()
                        + " |  Position: " + emp.getPosition() + " | Username: " + emp.getUsername());
            }
        }
        System.out.println("===================================");
    }

    private static void addEmployee(Scanner scanner, Employee[] employees) {
        System.out.println("===================================");
        System.out.println("            ADD EMPLOYEE        ");
        System.out.println("===================================");
        System.out.println("Enter '0' to cancel and go back.");

        String id = "";
        while (id.isEmpty()) {
            System.out.print("Enter Employee ID: ");
            id = scanner.nextLine();
            if (id.equals("0")) {
                System.out.println("Cancelled. Returning to Manage Employees menu.");
                return;
            } else if (id.isEmpty()) {
                System.out.println("Employee ID cannot be empty. Please enter a valid ID.");
            }
        }

        String name = "";
        while (name.isEmpty()) {
            System.out.print("Enter Employee Name: ");
            name = scanner.nextLine();
            if (name.equals("0")) {
                System.out.println("Cancelled. Returning to Manage Employees menu.");
                return;
            } else if (name.isEmpty()) {
                System.out.println("Employee Name cannot be empty. Please enter a valid name.");
            }
        }

        String position = "";
        while (position.isEmpty()) {
            System.out.print("Enter Employee Position: ");
            position = scanner.nextLine();
            if (position.equals("0")) {
                System.out.println("Cancelled. Returning to Manage Employees menu.");
                return;
            } else if (position.isEmpty()) {
                System.out.println("Employee Position cannot be empty. Please enter a valid position.");
            }
        }

        String username = "";
        while (username.isEmpty()) {
            System.out.print("Enter Employee Username: ");
            username = scanner.nextLine();
            if (username.equals("0")) {
                System.out.println("Cancelled. Returning to Manage Employees menu.");
                return;
            } else if (username.isEmpty()) {
                System.out.println("Employee Username cannot be empty. Please enter a valid username.");
            }
        }

        String password = "";
        while (password.isEmpty()) {
            System.out.print("Enter Employee Password: ");
            password = scanner.nextLine();
            if (password.equals("0")) {
                System.out.println("Cancelled. Returning to Manage Employees menu.");
                return;
            } else if (password.isEmpty()) {
                System.out.println("Employee Password cannot be empty. Please enter a valid password.");
            }
        }

        Employee newEmployee = new Employee(id, name, position, username, password);
        employees = Arrays.copyOf(employees, employees.length + 1);
        employees[employees.length - 1] = newEmployee;

        Employee.updateEmployeesJson(Arrays.asList(employees));
        System.out.println("Employee added successfully.");
    }

    private static void removeEmployee(Scanner scanner, Employee[] employees) {
        System.out.println("===================================");
        System.out.println("         REMOVE EMPLOYEE        ");
        System.out.println("===================================");
        displayAllEmployees(employees);
        System.out.println("Enter '0' to cancel and go back.");
        System.out.print("Enter Employee ID to remove: ");
        String id = scanner.nextLine();
        if (id.equals("0")) {
            System.out.println("Cancelled. Returning to Manage Employees menu.");
            return;
        }

        boolean removed = false;
        List<Employee> employeeList = new ArrayList<>(Arrays.asList(employees));
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getId().equals(id)) {
                employeeList.remove(i);
                removed = true;
                break;
            }
        }

        if (removed) {
            employees = employeeList.toArray(new Employee[0]);
            Employee.updateEmployeesJson(Arrays.asList(employees));
            System.out.println("Employee removed successfully.");
        } else {
            System.out.println("Employee not found.");
        }
    }

    private static void editEmployee(Scanner scanner, Employee[] employees) {
        System.out.println("===================================");
        System.out.println("           EDIT EMPLOYEE        ");
        System.out.println("===================================");
        displayAllEmployees(employees);
        System.out.println("Enter '0' to cancel and go back.");

        String id = "";
        while (id.isEmpty()) {
            System.out.print("Enter Employee ID to edit: ");
            id = scanner.nextLine();
            if (id.equals("0")) {
                System.out.println("Cancelled. Returning to Manage Employees menu.");
                return;
            } else if (id.isEmpty()) {
                System.out.println("Employee ID cannot be empty. Please enter a valid ID.");
            }
        }

        for (int i = 0; i < employees.length; i++) {
            Employee emp = employees[i];
            if (emp.getId().equals(id)) {
                System.out.println("Editing Employee: " + emp.getName());

                System.out.print("Enter new name (or press Enter to keep current): ");
                String newName = scanner.nextLine();
                if (newName.equals("0")) {
                    System.out.println("Cancelled. Returning to Manage Employees menu.");
                    return;
                }

                System.out.print("Enter new position (or press Enter to keep current): ");
                String newPosition = scanner.nextLine();
                if (newPosition.equals("0")) {
                    System.out.println("Cancelled. Returning to Manage Employees menu.");
                    return;
                }

                System.out.print("Enter new username (or press Enter to keep current): ");
                String newUsername = scanner.nextLine();
                if (newUsername.equals("0")) {
                    System.out.println("Cancelled. Returning to Manage Employees menu.");
                    return;
                }

                System.out.print("Enter new password (or press Enter to keep current): ");
                String newPassword = scanner.nextLine();
                if (newPassword.equals("0")) {
                    System.out.println("Cancelled. Returning to Manage Employees menu.");
                    return;
                }

                if (!newName.isEmpty()) {
                    emp.setName(newName);
                }
                if (!newPosition.isEmpty()) {
                    emp.setPosition(newPosition);
                }
                if (!newUsername.isEmpty()) {
                    emp.setUsername(newUsername);
                }
                if (!newPassword.isEmpty()) {
                    emp.setPassword(newPassword);
                }

                Employee.updateEmployeesJson(Arrays.asList(employees));
                System.out.println("Employee information updated successfully.");
                return;
            }
        }

        System.out.println("Employee not found.");
    }

    private static void viewEmployees(Employee[] employees) {
        System.out.println("===================================");
        System.out.println("           VIEW EMPLOYEES        ");
        System.out.println("===================================");
        displayAllEmployees(employees);
        System.out.println("Press Enter to return to the Manage Employees menu...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    private static void manageProducts(Scanner scanner, List<Product> products) {
        boolean backToMainMenu = false;

        while (!backToMainMenu) {
            clearScreen();
            System.out.println("===================================");
            System.out.println("          MANAGE PRODUCTS        ");
            System.out.println("===================================");
            System.out.println("1. Add Product");
            System.out.println("2. Remove Product");
            System.out.println("3. Edit Product");
            System.out.println("4. View Products");
            System.out.println("0. Back to Main Menu");

            int choice = -1;
            boolean validInput = false;

            while (!validInput) {
                System.out.print("Choose an option: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice >= 0 && choice <= 4) {
                        validInput = true;
                    } else {
                        System.out.println("Invalid option. Please choose a number between 0 and 4.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.nextLine();
                }
            }

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
        System.out.println("===================================");
        System.out.println("          ADD NEW PRODUCT        ");
        System.out.println("===================================");

        String productId = "";
        while (productId.isEmpty()) {
            System.out.print("Enter Product ID: ");
            productId = scanner.nextLine();
            if (productId.isEmpty()) {
                System.out.println("Product ID cannot be empty. Please enter a valid ID.");
            }
        }

        String name = "";
        while (name.isEmpty()) {
            System.out.print("Enter Product Name: ");
            name = scanner.nextLine();
            if (name.isEmpty()) {
                System.out.println("Product Name cannot be empty. Please enter a valid name.");
            }
        }

        double price = -1;
        while (price < 0) {
            System.out.print("Enter Product Price: ");
            if (scanner.hasNextDouble()) {
                price = scanner.nextDouble();
                if (price < 0) {
                    System.out.println("Price cannot be negative. Please enter a valid price.");
                }
            } else {
                System.out.println("Invalid price. Please enter a valid number.");
                scanner.next();
            }
        }

        int stock = -1;
        while (stock < 0) {
            System.out.print("Enter Product Stock: ");
            if (scanner.hasNextInt()) {
                stock = scanner.nextInt();
                if (stock < 0) {
                    System.out.println("Stock cannot be negative. Please enter a valid stock quantity.");
                }
            } else {
                System.out.println("Invalid stock. Please enter a valid number.");
                scanner.next();
            }
        }
        scanner.nextLine();

        LocalDate importDate = LocalDate.now();

        Product newProduct = new Product(productId, name, price, stock, importDate);
        products.add(newProduct);
        Product.updateProductsJson(products);
        System.out.println("Product added successfully with today's import date: " + importDate);
    }

    private static void removeProduct(Scanner scanner, List<Product> products) {
        System.out.println("===================================");
        System.out.println("          REMOVE PRODUCT        ");
        System.out.println("===================================");
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
        System.out.println("===================================");
        System.out.println("            EDIT PRODUCT        ");
        System.out.println("===================================");
        viewProducts(products);
        System.out.print("Enter Product ID to edit: ");
        String productId = scanner.nextLine();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (product.getProductId().equalsIgnoreCase(productId)) {
                System.out.println("Editing Product: " + product.getName());

                String newName = "";
                while (true) {
                    System.out.print("Enter new name (or press Enter to keep current): ");
                    newName = scanner.nextLine();
                    if (newName.isEmpty()) {
                        break;
                    } else {
                        product.setName(newName);
                        break;
                    }
                }

                String newPrice = "";
                while (true) {
                    System.out.print("Enter new price (or press Enter to keep current): ");
                    newPrice = scanner.nextLine();
                    if (newPrice.isEmpty()) {
                        break;
                    }
                    try {
                        double price = Double.parseDouble(newPrice);
                        if (price < 0) {
                            System.out.println("Price cannot be negative.");
                        } else {
                            product.setPrice(price);
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price. Please enter a valid number.");
                    }
                }

                String newStock = "";
                while (true) {
                    System.out.print("Enter new stock (or press Enter to keep current): ");
                    newStock = scanner.nextLine();
                    if (newStock.isEmpty()) {
                        break;
                    }
                    try {
                        int stock = Integer.parseInt(newStock);
                        if (stock < 0) {
                            System.out.println("Stock cannot be negative.");
                        } else {
                            product.setStock(stock);
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid stock. Please enter a valid number.");
                    }
                }

                String newImportDate = "";
                while (true) {
                    System.out.print("Enter new import date (YYYY-MM-DD) or press Enter to keep current: ");
                    newImportDate = scanner.nextLine();
                    if (newImportDate.isEmpty()) {
                        break;
                    }
                    try {
                        LocalDate importDate = LocalDate.parse(newImportDate, DateTimeFormatter.ISO_DATE);
                        product.setImportDate(importDate);
                        break;
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Please enter in the format YYYY-MM-DD.");
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
        System.out.println("===================================");
        System.out.println("           VIEW PRODUCTS        ");
        System.out.println("===================================");
        if (products.isEmpty()) {
            System.out.println("No products available.");
        } else {
            DecimalFormat df = new DecimalFormat("0.00");

            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                System.out.println("ID: " + product.getProductId()
                        + "| Name: " + product.getName()
                        + "| Price: " + df.format(product.getPrice()) + " THB"
                        + "| Stock: " + product.getStock()
                        + "| Import Date: " + product.getImportDate());
            }
        }
        System.out.println("===================================");
    }

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

            Product selectedProduct = null;
            while (selectedProduct == null) {
                System.out.print("Choose a product to purchase (by Product ID) or type 'done' to finish: ");
                String productId = scanner.nextLine();

                if (productId.equalsIgnoreCase("done")) {
                    continuePurchasing = false;
                    break;
                }

                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    if (product.getProductId().equalsIgnoreCase(productId)) {
                        selectedProduct = product;
                        break;
                    }
                }

                if (selectedProduct == null) {
                    System.out.println("Invalid Product ID. Please try again.");
                }
            }

            if (selectedProduct != null) {
                boolean validQuantity = false;
                while (!validQuantity) {
                    System.out.print("Enter quantity to purchase: ");
                    try {
                        int quantity = Integer.parseInt(scanner.nextLine());

                        if (quantity > selectedProduct.getStock()) {
                            System.out.println("Not enough stock available.");
                        } else if (quantity <= 0) {
                            System.out.println("Please enter a valid quantity.");
                        } else {
                            selectedProduct.decreaseStock(quantity);
                            purchasedProducts.add(selectedProduct);
                            purchasedQuantities.add(quantity);
                            System.out.println("Added to cart: " + quantity + " of " + selectedProduct.getName());
                            validQuantity = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity. Please enter a valid number.");
                    }
                }
            }
        }

        if (!purchasedProducts.isEmpty()) {
            String transactionId = "T" + System.currentTimeMillis();
            Transaction transaction = new Transaction(transactionId, employee, purchasedProducts, purchasedQuantities);
            clearScreen();
            System.out.println("");
            System.out.println(transaction.getBillContent());
            Product.updateProductsJson(products);
            System.out.println("Press Enter to return to the main menu...");
            scanner.nextLine();
        } else {
            for (int i = 0; i < purchasedProducts.size(); i++) {
                Product product = purchasedProducts.get(i);
                int quantity = purchasedQuantities.get(i);
                product.increaseStock(quantity);
            }
            System.out.println("No items were purchased. Stock has been restored.");
        }
    }

    private static void managePersonalInfo(Scanner scanner, Employee employee, List<Employee> employees) {
        boolean backToMainMenu = false;
        while (!backToMainMenu) {
            clearScreen();
            System.out.println("===================================");
            System.out.println("    MANAGE PERSONAL INFOMATION     ");
            System.out.println("===================================");
            System.out.println("Current Information:");
            System.out.println("Name: " + employee.getName());
            System.out.println("Position: " + employee.getPosition());
            System.out.println("Username: " + employee.getUsername());
            System.out.println("===================================");
            System.out.println("1. Edit Name");
            System.out.println("2. Edit Position");
            System.out.println("3. Edit Username");
            System.out.println("4. Edit Password");
            System.out.println("0. Back to Main Menu");

            int choice = -1;
            boolean validInput = false;

            while (!validInput) {
                System.out.print("Choose an option: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice >= 0 && choice <= 4) {
                        validInput = true;
                    } else {
                        System.out.println("Invalid option. Please choose a number between 0 and 4.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.next();
                }
            }

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

            if (updated) {
                Employee.updateEmployeesJson(employees);
            }

            if (!backToMainMenu) {
                System.out.println("\nPress Enter to return to the Manage Personal Information menu...");
                scanner.nextLine();
            }
        }
    }

    public static void generateMonthlyReport() {
        JSONParser parser = new JSONParser();
        Map<String, Double> monthlySales = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        try {
            FileReader reader = new FileReader("transaction.json");
            JSONArray transactionsArray = (JSONArray) parser.parse(reader);

            for (int i = 0; i < transactionsArray.size(); i++) {
                JSONObject transactionObject = (JSONObject) transactionsArray.get(i);
                String dateTime = (String) transactionObject.get("dateTime");
                String transactionMonthYear = dateTime.substring(0, 7);

                Number totalAmountNumber = (Number) transactionObject.get("totalAmount");
                double totalAmount = totalAmountNumber.doubleValue();

                monthlySales.put(transactionMonthYear,
                        monthlySales.getOrDefault(transactionMonthYear, 0.0) + totalAmount);
            }

            System.out.println("\n=== Monthly Sales Report ===");
            for (Map.Entry<String, Double> entry : monthlySales.entrySet()) {
                String monthYear = entry.getKey();
                double totalSales = entry.getValue();
                System.out.println("Year-Month: " + monthYear + " | Total Sales: " + totalSales + " THB");
            }
        } catch (IOException | ParseException e) {
            System.out.println("An error occurred while generating the sales report: " + e.getMessage());
        }

        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
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
