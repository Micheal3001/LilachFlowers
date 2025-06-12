package org.entities;


import javax.persistence.*;

@Entity
@Table(name = "premadeProducts")
public class PreMadeProduct extends Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;         // id generated for each product
    @Column(name = "product_name")
    private String name;
    private int priceBeforeDiscount;
    private String mainColor;

    public enum ProductType {CATALOG, CUSTOM_CATALOG}
    private boolean isOrdered;
    private ProductType productType;
    private String description;
    private int discount;

    //copyCtor for catalog product
    public PreMadeProduct(PreMadeProduct p) { //constructor
        super(p.getByteImage(), p.getPrice(),p.getAmount());
        this.priceBeforeDiscount = p.getPriceBeforeDiscount();
        this.name = p.getName();
        this.productType = p.getType() ;
        if(p.getType() == ProductType.CATALOG)
            this.description = p.getDescription();
        else
            if(p.getMainColor() != null)
                this.mainColor = p.getMainColor();
        this.isOrdered = p.isOrdered();
        this.discount = p.getDiscount();
    }

    //Ctor for catalog product
    public PreMadeProduct(String name, String path, int priceBeforeDiscount, String description, int discount, boolean isOrdered) { //constructor
        super(path, (int) (priceBeforeDiscount*(1-discount*0.01)));
        this.priceBeforeDiscount = priceBeforeDiscount;
        this.name = name;
        this.productType = ProductType.CATALOG;
        this.description = description;
        this.discount=discount;
        this.isOrdered = isOrdered;
    }

    //Ctor for catalog product
    public PreMadeProduct(String name, byte[] image, int priceBeforeDiscount, String description, int discount,boolean isOrdered) {
        super(image, (int) (priceBeforeDiscount*(1-discount*0.01)));
        this.priceBeforeDiscount = priceBeforeDiscount;
        this.name = name;
        this.productType = ProductType.CATALOG;
        this.description = description;
        this.discount=discount;
        this.isOrdered = isOrdered;

    }


    //Ctor for custom-made catalog product

    public PreMadeProduct(String name, String path, int priceBeforeDiscount, int discount,boolean isOrdered, String mainColor) { //constructor
        super(path, (int) (priceBeforeDiscount*(1-discount*0.01)));
        this.priceBeforeDiscount = priceBeforeDiscount;
        this.name = name;
        this.mainColor = mainColor;
        this.productType = ProductType.CUSTOM_CATALOG;
        this.discount=discount;
        this.isOrdered = isOrdered;

    }

    //Ctor for custom-made catalog product
    public PreMadeProduct(String name, byte[] image,int priceBeforeDiscount, int discount,boolean isOrdered, String mainColor) {
        super(image, (int) (priceBeforeDiscount*(1-discount*0.01)));
        this.priceBeforeDiscount = priceBeforeDiscount;
        this.name = name;
        this.mainColor = mainColor;
        this.productType = ProductType.CUSTOM_CATALOG;
        this.discount=discount;
        this.isOrdered = isOrdered;
    }

    public PreMadeProduct() {

    }

    public void setMainColor(String mainColor) {
        this.mainColor = mainColor;
    }

    public int getPriceBeforeDiscount() {
        return priceBeforeDiscount;
    }

    public void setPriceBeforeDiscount(int priceBeforeDiscount) {
        this.priceBeforeDiscount = priceBeforeDiscount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductType getType() {
        return this.productType;
    }

    public String getMainColor() {
        return this.mainColor;
    }


    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }


    @Override
    public int getId() { return id; }

    public boolean isOrdered() {
        return isOrdered;
    }


    public void setOrdered(boolean ordered) {
        isOrdered = ordered;
    }

}
