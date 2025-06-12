package org.client;

import javafx.scene.control.DatePicker;
import org.entities.CustomMadeProduct;
import org.entities.Order;
import org.entities.PreMadeProduct;
import org.entities.Product;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractReport extends Controller{

    public Map<String,Integer> getMap(LinkedList<Order> orders) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        for(Product product : Client.products)
            map.put(((PreMadeProduct) product).getName(), 0);

        for(Order order : orders) {
            for(PreMadeProduct product : order.getPreMadeProducts())
                map.put(product.getName(), map.get(product.getName()) + product.getAmount());

            for(CustomMadeProduct customProduct : order.getCustomMadeProducts())
                for(PreMadeProduct baseProduct : customProduct.getProducts())
                    map.put(baseProduct.getName(), map.get(baseProduct.getName()) + baseProduct.getAmount());
        }
        return map;
    }

    static int numOfDays(Date d1, Date d2) {
        long difference_In_Time = d2.getTime() - d1.getTime();
        return  ((int) TimeUnit.MILLISECONDS.toDays(difference_In_Time) % 365) + 1;
    }

    static int numOfDays(LocalDate d1, LocalDate d2) {
        return numOfDays(localDateToDate(d1), localDateToDate(d2));
    }

    static int numOfDays(DatePicker d1, DatePicker d2) {
        return numOfDays(localDateToDate(d1.getValue()), localDateToDate(d2.getValue()));
    }

    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {

        endDate = addLocalDate(endDate, 1);
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());
    }

    public static LocalDate dateToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date localDateToDate(LocalDate date) { //get the picked localDate and convert it to Date
        Instant instant = Instant.from(date.atStartOfDay(ZoneId.systemDefault())); //convert LocalDate to Date
        Date pickedDate = Date.from(instant);
        return pickedDate;
    }

    public boolean dateAreEqual(LocalDate date1, LocalDate date2) {
        return date1.atStartOfDay().isEqual(date2.atStartOfDay());
    }

    public static Date addDays(Date date, int daysToAdd)
    {
        Date d;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, daysToAdd); //minus number would decrement the days
        d = cal.getTime();
        return cal.getTime();
    }

    public static LocalDate addLocalDate(DatePicker toDate, int daysToAdd) {
        return dateToLocalDate(addDays(localDateToDate(toDate.getValue()), daysToAdd));
    }

    public static LocalDate addLocalDate(LocalDate toDate, int daysToAdd) {
        return dateToLocalDate(addDays(localDateToDate(toDate), daysToAdd));
    }
}
