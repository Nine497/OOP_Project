
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Product {

    private String productId;
    private String name;
    private double price;
    private int stock;

    public Product(String productId, String name, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    // Getters
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // Method to decrease stock by a specified quantity
    public void decreaseStock(int quantity) {
        if (stock >= quantity) {
            stock -= quantity;
        } else {
            System.out.println("Not enough stock for product: " + name);
        }
    }

    // Method to increase stock by a specified quantity
    public void increaseStock(int quantity) {
        if (quantity > 0) {
            stock += quantity;
        } else {
            System.out.println("Invalid quantity to add: " + quantity);
        }
    }

    // Method to edit product details
    public void editProduct(String newName, Double newPrice, Integer newStock) {
        if (newName != null && !newName.isEmpty()) {
            setName(newName);
        }
        if (newPrice != null && newPrice > 0) {
            setPrice(newPrice);
        }
        if (newStock != null && newStock >= 0) {
            setStock(newStock);
        }
    }

    // Static method to update the product list in the JSON file
    public static void updateProductsJson(List<Product> products) {
        JSONArray productsJsonArray = new JSONArray();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            JSONObject productObject = new JSONObject();
            productObject.put("productId", product.getProductId());
            productObject.put("name", product.getName());
            productObject.put("price", product.getPrice());
            productObject.put("stock", product.getStock());
            productsJsonArray.add(productObject);
        }

        try (FileWriter file = new FileWriter("Products.json")) {
            file.write(productsJsonArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.out.println("An error occurred while updating the products JSON file: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Product ID: " + productId + ", Name: " + name + ", Price: " + price + " THB, Stock: " + stock;
    }
}
