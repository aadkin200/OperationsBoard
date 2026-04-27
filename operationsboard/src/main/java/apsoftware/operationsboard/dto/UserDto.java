package apsoftware.operationsboard.dto;

import apsoftware.operationsboard.enums.GlobalAccessLevel;

public class UserDto {

    private Long id;
    private String employeeId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
    private GlobalAccessLevel globalAccessLevel;

    public UserDto() {}

    public UserDto(Long id, String employeeId, String username, String firstName, String lastName,
                   String email, Boolean active, GlobalAccessLevel globalAccessLevel) {
        this.id = id;
        this.employeeId = employeeId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.globalAccessLevel = globalAccessLevel;
    }

    public Long getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Boolean getActive() { return active; }
    public GlobalAccessLevel getGlobalAccessLevel() { return globalAccessLevel; }

    public void setId(Long id) { this.id = id; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setActive(Boolean active) { this.active = active; }
    public void setGlobalAccessLevel(GlobalAccessLevel globalAccessLevel) { this.globalAccessLevel = globalAccessLevel; }
}
