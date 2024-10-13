
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    private void processTransaction() {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);
            product.decreaseStock(quantity);
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

        try (FileWriter fileWriter = new FileWriter("transaction.json", true)) {
            fileWriter.write(transactionJson.toJSONString());
            fileWriter.write(System.lineSeparator());
            fileWriter.flush();
            System.out.println("Transaction saved to Transaction.json.");
        } catch (IOException e) {
            System.out.println("Error writing to Transaction.json file: " + e.getMessage());
        }
    }

    public String getBillContent() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        StringBuilder billContent = new StringBuilder();
        billContent.append("=== Transaction Bill ===\n");
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
        billContent.append("==========================\n");

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
}
