package org.entities;
import javax.persistence.*;

import static org.entities.Employee.Role.*;

@Entity
@Table(name = "employees")
public class Employee extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public enum Role { STORE_EMPLOYEE, CUSTOMER_SERVICE, STORE_MANAGER, CEO, ADMIN}
    @Column(name = "employee_role")
    private Role role;
    public Employee(String userID, String name, String userName, String password, String email, String phone, Role role, Store store) {
        super(userID, name, userName, password,  email, phone, store);
        this.role = role;
    }

    public Employee(String userID, String name, String userName, String password, String email, String phone, Role role, Store store, boolean frozen) {
        super(userID, name, userName, password,  email, phone, store, frozen);
        this.role = role;
    }

    public Employee() {
        super();
    }

    @Override
    public int getId() {
        return id;
    }
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    public String getRoleToString() {
        return switch (this.role) {
            case STORE_EMPLOYEE -> "Store Employee";
            case CUSTOMER_SERVICE -> "Customer Service";
            case CEO -> "CEO";
            case STORE_MANAGER -> "Store Manager";
            default -> "Admin";
        };
    }

    public Role getStringToRole(String role) {
        return switch (role) {
            case "Store Employee" -> STORE_EMPLOYEE;
            case "Customer Service" -> CUSTOMER_SERVICE;
            case "CEO" -> CEO;
            case "Store Manager" -> STORE_MANAGER;
            default -> ADMIN;
        };
    }

    public static String[] getAllRoles() {
        String[] roles = new String[4];
        roles[0] = "Store Employee";
        roles[1] = "Customer Service";
        roles[2] = "Store Manager";
        roles[3] = "CEO";
        return roles;
    }
}
