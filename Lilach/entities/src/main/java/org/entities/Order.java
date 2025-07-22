package org.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Transient;


@Entity
@Table(name = "orders")
public class Order implements Serializable {     //Product class entity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;         // id generated for each product

    @Transient
    protected List<PreMadeProduct> preMadeProducts;

    @Transient
    protected List<CustomMadeProduct> customMadeProducts;

    @ManyToOne
    protected Customer orderedBy;

    @ManyToOne
    //@JoinColumn(name="stores_orders")
    protected Store store;

    protected int price;
    protected String orderTime;
    protected Date orderDate;
    protected Date deliveryDate;
    protected String deliveryHour;
    public enum Status {PENDING, ARRIVED, CANCELED}
    protected Status isDelivered;
    public enum Delivery {SELF_SHIPPING, SHIPPING_GIFT, TAKEAWAY}

    protected Delivery delivery;

    //shipping order information:
    protected String receiverPhone = null;
    protected String receiverName = null;
    protected String address = null;
    protected String greetingCard = null;

    //Order Generation constructor
    public Order(LinkedList<PreMadeProduct> preMadeProducts, LinkedList<CustomMadeProduct> customMadeProducts,
                 Customer orderedBy, int price, Date deliveryDate, Store store, String deliveryHour,
                 String address, String greetingCard, Date orderDate) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleformat = new SimpleDateFormat("dd/MMMM/yyyy hh:mm:s");
        this.orderTime = simpleformat.format(orderDate.getTime());
        this.delivery = Delivery.SELF_SHIPPING;
        this.orderDate=orderDate;
        this.preMadeProducts = preMadeProducts;
        this.customMadeProducts = customMadeProducts;
        this.orderedBy = orderedBy;
        this.price = price;
        this.deliveryDate = deliveryDate;
        this.store = store;
        this.deliveryHour = deliveryHour;
        this.isDelivered = Status.PENDING;
        this.address = address;
        this.greetingCard = greetingCard;
    }

    //SELF_SHIPPING constructor
    public Order(LinkedList<PreMadeProduct> preMadeProducts, LinkedList<CustomMadeProduct> customMadeProducts,
                 Customer orderedBy, int price, Date deliveryDate, Store store, String deliveryHour,
                 String address, String greetingCard) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleformat = new SimpleDateFormat("dd/MMMM/yyyy hh:mm:s");
        this.orderTime = simpleformat.format(cal.getTime());
        this.orderDate = localDateToDate(LocalDate.now());
        this.delivery = Delivery.SELF_SHIPPING;

        this.preMadeProducts = preMadeProducts;
        this.customMadeProducts = customMadeProducts;
        this.orderedBy = orderedBy;
        this.price = price;
        this.deliveryDate = deliveryDate;
        this.store = store;
        this.deliveryHour = deliveryHour;
        this.isDelivered = Status.PENDING;
        this.address = address;
        this.greetingCard = greetingCard;
    }

    //SHIPPING_GIFT constructor
    public Order(LinkedList<PreMadeProduct> preMadeProducts, LinkedList<CustomMadeProduct> customMadeProducts,
                 Customer orderedBy, int price, Date deliveryDate, Store store, String deliveryHour, String receiverPhone,
                 String receiverName, String address, String greetingCard) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleformat = new SimpleDateFormat("dd/MMMM/yyyy hh:mm:s");
        this.orderTime = simpleformat.format(cal.getTime());
        this.orderDate = localDateToDate(LocalDate.now());
        this.delivery = Delivery.SHIPPING_GIFT;

        this.preMadeProducts = preMadeProducts;
        this.customMadeProducts = customMadeProducts;
        this.orderedBy = orderedBy;
        this.price = price;
        this.deliveryDate = deliveryDate;
        this.store = store;
        this.deliveryHour = deliveryHour;
        this.isDelivered = Status.PENDING;
        this.receiverPhone = receiverPhone;
        this.receiverName = receiverName;
        this.address = address;
        this.greetingCard = greetingCard;
    }


    //TAKEAWAY constructor
    public Order(LinkedList<PreMadeProduct> preMadeProducts, LinkedList<CustomMadeProduct> customMadeProducts,
                 Customer orderedBy, int price, Store store, Date deliveryDate, String deliveryHour, String greetingCard) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleformat = new SimpleDateFormat("dd/MMMM/yyyy hh:mm:s");
        this.orderTime = simpleformat.format(cal.getTime());
        this.orderDate = localDateToDate(LocalDate.now());

        this.delivery = Delivery.TAKEAWAY;
        this.preMadeProducts = preMadeProducts;
        this.customMadeProducts = customMadeProducts;
        this.orderedBy = orderedBy;
        this.price = price;
        this.deliveryDate = deliveryDate;
        this.deliveryHour = deliveryHour;
        this.isDelivered = Status.PENDING;
        this.store = store;
        this.greetingCard = greetingCard;
    }

    public Order() {}

    public static Date localDateToDate(LocalDate date) { //get the picked localDate and convert it to Date
        Instant instant = Instant.from(date.atStartOfDay(ZoneId.systemDefault())); //convert LocalDate to Date
        Date pickedDate = Date.from(instant);
        return pickedDate;
    }
    //getters and setters:
    public Customer getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(Customer orderedBy) {
        this.orderedBy = orderedBy;
    }

    public List<CustomMadeProduct> getCustomMadeProducts() {
        return this.customMadeProducts;
    }

    public void setCustomMadeProducts(LinkedList<CustomMadeProduct> customMadeProducts) {
        this.customMadeProducts = customMadeProducts;
    }

    public void setPreMadeProducts(LinkedList<PreMadeProduct> preMadeProducts) {
        this.preMadeProducts = preMadeProducts;
    }

    public List<PreMadeProduct> getPreMadeProducts() {
        return this.preMadeProducts;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Status isDelivered() {
        return isDelivered;
    }

    public void setDelivered(Status delivered) {
        isDelivered = delivered;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    public String getDeliveryHour() {
        return deliveryHour;
    }

    public void setDeliveryHour(String deliveryHour) {
        this.deliveryHour = deliveryHour;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Delivery getDelivery()
    {
        return this.delivery;
    }

    public Store getStore()
    {
        return this.store;
    }

}
