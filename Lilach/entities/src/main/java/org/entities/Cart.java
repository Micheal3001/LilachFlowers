package org.entities;

import java.util.LinkedList;
import java.util.List;


public class Cart {
    private List<Product> products = new LinkedList<>();
    private int totalCost;

    public Cart() {
        this.products = new LinkedList<Product>();
        this.totalCost = 0;
    }



    public int getTotalCost() {
        return this.totalCost;
    }

    public List<Product> getProducts() {
        return this.products;
    }

    public void setTotalCost(int newCost) {
        this.totalCost = newCost;
    }

    public void removeProduct(int id) {
        for (int i = 0; i < this.products.size(); i++) {
            if (this.products.get(i).getId() == id) {
                totalCost -= this.products.get(i).getPrice() * this.products.get(i).getAmount();
                products.get(i).setAmount(0);
                products.remove(i);
                break;
            }
        }
    }


    public void insertProduct(Product product) {
        boolean flag_here = false;
        int index = 0;
        for(int i=0; i<products.size(); i++)
        {
            if (product.getId() == products.get(i).getId()) //same product
            {
                flag_here = true;
                index = i;
            }
        }

        if(!flag_here)
        {
            System.out.println(product.getAmount());
            //product.setAmount(1);
            this.products.add(product);
        }
        else
        {
            if(flag_here)
            {
                int pastAmount = products.get(index).getAmount();
                //System.out.println(pastAmount);
                //System.out.println("1");
                products.get(index).setAmount(pastAmount + 1);
            }
        }
        refreshTotalCost();
    }

    public void insertSomeProduct(Product product, int amount) {
        boolean flag_here = false;
        int index = 0;
        for(int i=0; i<products.size(); i++)
            if(product.getId() == products.get(i).getId()) //same product
            {
                flag_here = true;
                index = i;
            }

        if(!flag_here) {
            product.setAmount(amount);
            this.products.add(product);
        }
        else
        {
            int pastAmount = products.get(index).getAmount();

            //System.out.println(pastAmount);
            //System.out.println(amount);

            products.get(index).setAmount(pastAmount+amount);
        }
        refreshTotalCost();
    }

    public void insertCustomMade(CustomMadeProduct product) {
        this.products.add(product);
        refreshTotalCost();
    }

    public void refreshTotalCost()
    {
        totalCost = 0;
        for (int i = 0; i < this.products.size(); i++)
        {
            this.totalCost += products.get(i).getPrice() * products.get(i).getAmount();
        }
    }

    public void emptyProducts() {
        this.products = new LinkedList<Product>();
        this.totalCost = 0;
    }
}