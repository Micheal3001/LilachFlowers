package org.entities;

import javafx.scene.image.Image;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;

@MappedSuperclass
public abstract class Product implements Serializable {

    @Column(name = "image", length = 65555)
    private byte[] image;

    private int price;

    private int amount;

    public Product(String path, int price, int amount) {      //constructor

        this.price = price;
        this.amount = amount;

        File file = new File(path);         //converts string pth into bytecode image
        this.image = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            //convert file into array of bytes
            fileInputStream.read(this.image);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Product(byte[] image, int price, int amount) {
        this.price = price;
        this.image = image;
        this.amount = amount;
    }

    public Product(String path, int price) {      //constructor

        this.price = price;
        this.amount = 0;

        File file = new File(path);         //converts string pth into bytecode image
        this.image = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            //convert file into array of bytes
            fileInputStream.read(this.image);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Product(byte[] image, int price) {
        this.price = price;
        this.image = image;
    }

    public Product(int price) {
        //this.name = name;
        //image=image;
        this.price = price;
        //this.priceBeforeDiscount = priceBeforeDiscount;
        //this.image = image;
    }

    public Product() {}

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public Image getImage() {
        return new Image(new ByteArrayInputStream(this.image));
    }

    public byte[] getByteImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    abstract public int getId();
}

