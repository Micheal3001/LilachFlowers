package org.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "stores")
public class Store implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;         // id generated for each store

    private String name;

    @OneToOne
    private Employee storeManager;

    @OneToMany
    private List<Employee> employees = new LinkedList<Employee>();

    private String address;

    @OneToMany
    private List<Complaint> complaints = new LinkedList<Complaint>();

    @OneToMany
    //@Column(name="stores_orders")
    private List<Order> orders;

    public Store(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Store() {

    }

    public Employee getStoreManager() {
        return storeManager;
    }

    public void setStoreManager(Employee storeManager) {
        this.storeManager = storeManager;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void addEmployees(Employee employee) {
        employees.add(employee);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
}
