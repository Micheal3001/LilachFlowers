package org.server;

import org.entities.Customer;
import org.entities.Order;
import org.email.SendMail;


import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleMailing {

    public static void orderMail(Order order){

        Customer customer = order.getOrderedBy();
        Date current = new Date();
        Server.orderArrived(order, Order.Status.ARRIVED);
        String mail = "Order Arrival";
        String subject;
        if(order.getDelivery()== Order.Delivery.SHIPPING_GIFT){
            subject = "Hello there " + customer.getName() + ",\nyour gift has been received";
        }else{
            subject = "Hello there " + customer.getName() + ",\nyour order has arrived";
        }
        SendMail.main(new String[]{customer.getEmail(), mail, subject});
        System.out.println("Mail sent");
    }



    public static void main(String[] args){

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextRun = now.withMinute(0).withSecond(0);

        if(now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusHours(1);

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();
        System.out.println(now);
        System.out.println(nextRun);
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        System.out.println(initialDelay);
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<Order> orders;
                Date date = new Date();
                date.setSeconds(0);
                date.setMinutes(0);
                try {
                    orders = App.getAllOrders();
                    for(Order order:orders){
                        if(order.getDeliveryDate().getTime()<date.getTime() && order.isDelivered() == Order.Status.PENDING){
                            orderMail(order);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, initialDelay, 3600, TimeUnit.SECONDS);






    }
}
