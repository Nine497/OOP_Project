
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.text.DecimalFormat;

public class Product {

    private String productId;
    private String name;
    private double price;
    private int stock;
    private LocalDate importDate;
    private Category category;

    public Product(String productId, String name, double price, int stock, LocalDate importDate, Category category) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.importDate = importDate;
        this.category = category;
    }

    public LocalDate getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDate importDate) {
        this.importDate = importDate;
    }

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void decreaseStock(int quantity) {
        if (stock >= quantity) {
            stock -= quantity;
        } else {
            System.out.println("Not enough stock for product: " + name);
        }
    }

    public void increaseStock(int quantity) {
        if (quantity > 0) {
            stock += quantity;
        } else {
            System.out.println("Invalid quantity to add: " + quantity);
        }
    }

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

    public static void updateProductsJson(List<Product> products) {
        JSONArray productsJsonArray = new JSONArray();
        DecimalFormat df = new DecimalFormat("0.00");

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            JSONObject productObject = new JSONObject();
            productObject.put("productId", product.getProductId());
            productObject.put("name", product.getName());

            productObject.put("price", df.format(product.getPrice()));
            productObject.put("stock", product.getStock());
            productObject.put("importDate", product.getImportDate().toString());

            productObject.put("categoryId", product.getCategory().getCategoryId());
            productObject.put("category", product.getCategory().getCategoryName());

            productsJsonArray.add(productObject);
        }

        try (FileWriter file = new FileWriter("Products.json")) {
            file.write(productsJsonArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.out.println("An error occurred while updating the products JSON file: " + e.getMessage());
        }
    }

}
