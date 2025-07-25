package org.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "complaints")
public class Complaint implements Serializable {    //only for customers
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private Customer customer;
    @Column(name = "dateOfSubmit")
    private Date date;
    private String compText;
    @Column(name = "appStatus")
    private Boolean appStatus = true;        // true= complaint filed successfully- yet to be inspected, false = complaint fulfilled
    private boolean delayedNotified = false;
    private Boolean completedOnTime = false;
    private Topic topic;
    @ManyToOne
    private Store store;
    public Complaint(Customer customer, Date date, String compText, Topic topic, Store store) {
        this.customer = customer;
        this.date = date;
        this.compText = compText;
        this.topic = topic;
        this.store = store;
    }

    public Complaint() {

    }

    public static Complaint.Topic convertToTopic(String topic) {
        return switch (topic) {
            case "Bad service" -> Complaint.Topic.BAD_SERVICE;
            case "Order didn't arrive in time" -> Complaint.Topic.LATE_ARRIVAL;
            case "Defective product/ not what you ordered" -> Complaint.Topic.BAD_PRODUCT;
            case "Payment issue" -> Complaint.Topic.PAYMENT;
            default -> Complaint.Topic.OTHER;
        };
    }

    public static Topic[] getAllTopics() {
        Topic[] topics = new Topic[5];
        topics[0] = Topic.PAYMENT;
        topics[1] = Topic.BAD_SERVICE;
        topics[2] = Topic.BAD_PRODUCT;
        topics[3] = Topic.LATE_ARRIVAL;
        topics[4] = Topic.OTHER;
        return topics;
    }

    public static String topicToString(Complaint.Topic topic) {
        return switch (topic) {
            case BAD_SERVICE -> "Bad Service";
            case LATE_ARRIVAL -> "Order didn't arrive in time";
            case BAD_PRODUCT -> "Defective product / not what you ordered";
            case PAYMENT -> "Payment issue";
            case OTHER -> "Other";
        };
    }

    @Column(name = "delayedNotified")
    public boolean isDelayedNotified() {
        return delayedNotified;
    }

    public void setDelayedNotified(boolean delayedNotified) {
        this.delayedNotified = delayedNotified;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCompText() {
        return this.compText;
    }

    public void setCompText(String compText) {
        this.compText = compText;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public Boolean getStatus() {
        return appStatus;
    }

    public void setStatus(Boolean status) {
        this.appStatus = status;
    }

    public Boolean getCompletedOnTime() {
        return completedOnTime;
    }

    public void setCompletedOnTime(Boolean completedOnTime) {
        this.completedOnTime = completedOnTime;
    }

    public Topic getTopic() {
        return topic;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public enum Topic {BAD_SERVICE, LATE_ARRIVAL, BAD_PRODUCT, PAYMENT, OTHER}
}
