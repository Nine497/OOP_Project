
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Transaction {

    private String transactionId;
    private Employee employee;
    private List<Product> products;
    private List<Integer> quantities;
    private LocalDateTime dateTime;

    public Transaction(String transactionId, Employee employee, List<Product> products, List<Integer> quantities) {
        this.transactionId = transactionId;
        this.employee = employee;
        this.products = products;
        this.quantities = quantities;
        this.dateTime = LocalDateTime.now();

        processTransaction();
    }

    public void calChange(String moneyReceivedStr) {
        try {
            double moneyReceived = Double.parseDouble(moneyReceivedStr);
            double totalAmount = 0.0;

            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                int quantity = quantities.get(i);
                totalAmount += product.getPrice() * quantity;
            }

            double change = moneyReceived - totalAmount;

            if (change < 0) {
                System.out.println("Not enough money. Please provide more.");
            } else {
                System.out.println("===================================");
                System.out.println("           CALCULATE CHANGE        ");
                System.out.println("===================================");
                System.out.println("Money Received: " + moneyReceived + " THB");
                System.out.println("Price total: " + totalAmount + " THB");
                System.out.println("Change: " + change + " THB");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    public double getTotalAmount() {
        double totalAmount = 0.0;
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);
            totalAmount += product.getPrice() * quantity;
        }
        return totalAmount;
    }

    private void processTransaction() {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);
        }
        System.out.println("Transaction processed: " + this);
        saveTransactionToJson();
    }

    private void saveTransactionToJson() {
        JSONObject transactionJson = new JSONObject();
        transactionJson.put("transactionId", transactionId);
        transactionJson.put("employee", employee.getName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);
        transactionJson.put("dateTime", formattedDateTime);

        JSONArray purchasedItems = new JSONArray();
        double totalAmount = 0.0;

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);
            double itemTotal = product.getPrice() * quantity;
            totalAmount += itemTotal;

            JSONObject itemJson = new JSONObject();
            itemJson.put("productId", product.getProductId());
            itemJson.put("name", product.getName());
            itemJson.put("quantity", quantity);
            itemJson.put("pricePerUnit", product.getPrice());
            itemJson.put("totalPrice", itemTotal);

            purchasedItems.add(itemJson);
        }

        transactionJson.put("purchasedItems", purchasedItems);
        transactionJson.put("totalAmount", totalAmount);

        JSONParser parser = new JSONParser();
        JSONArray transactionsArray = new JSONArray();

        try (FileReader reader = new FileReader("transaction.json")) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONArray) {
                transactionsArray = (JSONArray) obj;
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error loading transaction data: " + e.getMessage());
        }

        transactionsArray.add(transactionJson);

        try (FileWriter fileWriter = new FileWriter("transaction.json")) {
            fileWriter.write(transactionsArray.toJSONString());
            fileWriter.flush();
            System.out.println("Transaction saved to transaction.json.");
        } catch (IOException e) {
            System.out.println("Error writing to Transaction.json file: " + e.getMessage());
        }
    }

    public String getBillContent() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        StringBuilder billContent = new StringBuilder();
        System.out.println("===================================");
        System.out.println("        TRANSACTION BILL     ");
        System.out.println("===================================");
        billContent.append("Transaction ID: ").append(transactionId).append("\n");
        billContent.append("Employee: ").append(employee.getName()).append("\n");
        billContent.append("Date and Time: ").append(formattedDateTime).append("\n");
        billContent.append("Purchased Items:\n");

        double totalAmount = 0.0;
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);
            double itemTotal = product.getPrice() * quantity;
            totalAmount += itemTotal;

            billContent.append("- ").append(product.getName())
                    .append(", Quantity: ").append(quantity)
                    .append(", Price per unit: ").append(product.getPrice()).append(" THB")
                    .append(", Total: ").append(itemTotal).append(" THB\n");
        }

        billContent.append("Grand Total: ").append(totalAmount).append(" THB\n");
        System.out.println("===================================");

        return billContent.toString();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Integer> getQuantities() {
        return quantities;
    }

    public static void generateDailyReport(String inputDate) {
        JSONParser parser = new JSONParser();

        try {
            FileReader reader = new FileReader("transaction.json");
            JSONArray transactionsArray = (JSONArray) parser.parse(reader);

            System.out.println("===================================");
            System.out.println("     DAILY TRANSACTION REPORT      ");
            System.out.println("===================================");
            boolean transactionsFound = false;

            for (int i = 0; i < transactionsArray.size(); i++) {
                JSONObject transactionObject = (JSONObject) transactionsArray.get(i);
                String dateTime = (String) transactionObject.get("dateTime");
                String transactionDate = dateTime.substring(0, 10);

                if (transactionDate.equals(inputDate)) {
                    transactionsFound = true;

                    String transactionId = (String) transactionObject.get("transactionId");
                    String employee = (String) transactionObject.get("employee");
                    Number totalAmountNumber = (Number) transactionObject.get("totalAmount");
                    double totalAmount = totalAmountNumber.doubleValue();
                    System.out.println("===================================");
                    System.out.println("Transaction ID: " + transactionId);
                    System.out.println("===================================");
                    System.out.println("Employee: " + employee);
                    System.out.println("Total Amount: " + totalAmount + " THB");
                    System.out.println("DateTime: " + dateTime);
                    System.out.println("-----------------------------------");

                    JSONArray purchasedItems = (JSONArray) transactionObject.get("purchasedItems");
                    for (int j = 0; j < purchasedItems.size(); j++) {
                        JSONObject item = (JSONObject) purchasedItems.get(j);
                        String productId = (String) item.get("productId");
                        String productName = (String) item.get("name");
                        int quantity = ((Long) item.get("quantity")).intValue();
                        double totalPrice = ((Number) item.get("totalPrice")).doubleValue();

                        System.out.println("Product ID: " + productId);
                        System.out.println("Product Name: " + productName);
                        System.out.println("Quantity: " + quantity);
                        System.out.println("Total Price: " + totalPrice + " THB");
                        System.out.println("-----------------------------------");
                    }
                }
            }

            if (!transactionsFound) {
                System.out.println("No transactions found for the selected date: " + inputDate);
            }

        } catch (IOException | ParseException e) {
            System.out.println("An error occurred while generating the daily transaction report: " + e.getMessage());
        }
    }
}
