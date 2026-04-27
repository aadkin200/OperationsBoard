package apsoftware.operationsboard.dto;

public class UserCreateRequest {

    private String employeeId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    public UserCreateRequest() {}

    public String getEmployeeId() { return employeeId; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }

    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
}
