package org.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class Guest implements Serializable {

    @Column(name = "nameOfCustomer")
    private String name;
    //@Transient
    //private Cart cart;

    public Guest(String name) {
        this.name = name;
    }

    public Guest() {
        this.name="Guest";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
