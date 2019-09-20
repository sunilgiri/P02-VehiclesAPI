package com.udacity.pricing.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Represents the price of a given vehicle, including currency.
 */

@Entity
public class Price {
    @Id
    @Column(name = "vehicleid")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Long vehicleId;
    private String currency;
    private BigDecimal price;

    public Price() {
    }

    public Price(String currency, BigDecimal price,Long vid) {
        this.vehicleId = vid;
        this.price = price;
        this.currency = currency;
     }

     public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVId(Long vId) {
        this.vehicleId = vId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
