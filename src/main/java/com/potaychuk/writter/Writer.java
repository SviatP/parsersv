package com.potaychuk.writter;

import com.potaychuk.domain.Product;

import java.util.List;

/**
 * Created by Potaychuk Sviatoslav on 23.07.2017.
 */
public interface Writer {
    void writeToFile(List<Product> products);
}
