
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Employee {

    private static final String JSON_FILE_PATH = "Employees.json";
    private String id;
    private String name;
    private String position;
    private String username;
    private String password;

    // Constructor
    public Employee(String id, String name, String position, String username, String password) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Method for logging in
    public boolean login(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }

    // Method ใน Employee สำหรับอัปเดตข้อมูล JSON
    public static void updateEmployeesJson(List<Employee> employees) {
        JSONArray employeesJsonArray = new JSONArray();

        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            JSONObject employeeObject = new JSONObject();
            employeeObject.put("id", emp.getId());
            employeeObject.put("name", emp.getName());
            employeeObject.put("position", emp.getPosition());
            employeeObject.put("username", emp.getUsername());
            employeeObject.put("password", emp.getPassword());
            employeesJsonArray.add(employeeObject);
        }

        try (FileWriter file = new FileWriter("Employees.json")) {
            file.write(employeesJsonArray.toJSONString());
            file.flush();
            System.out.println("Employee data updated in JSON file.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating the employees JSON file: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Employee ID: " + id + ", Name: " + name + ", Position: " + position + ", Username: " + username;
    }
}
