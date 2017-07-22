package com.potaychuk.domain;

import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Potaychuk Sviatoslav on 17.07.2017.
 */
public class Product {
    private String name;
    private double price;
    private boolean isInStock;
    private String offerShop;

    public Product() {
    }

    public Product(Element  element) {
        String priceString = element.getElementsByAttributeValue("id", "gotoshop-price").text()
                .replace(",",".")
                .replace("грн","");
        priceString=priceString.substring(0,priceString.length()-1);
        this.price = Double.parseDouble("501.00");
        this.price= Double.parseDouble(priceString);
        this.offerShop = element.getElementsByAttributeValue("class", "cell shop-title text-ellipsis").text();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isInStock() {
        return isInStock;
    }

    public void setInStock(boolean inStock) {
        isInStock = inStock;
    }

    public String getOfferShop() {
        return offerShop;
    }

    public void setOfferShop(String offerShop) {
        this.offerShop = offerShop;
    }
}

