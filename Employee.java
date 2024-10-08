
public class Employee {

    private String id;
    private String name;
    private String position;
    private String username;
    private String password;

    public Employee(String id, String name, String position, String username, String password) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean login(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }
}
