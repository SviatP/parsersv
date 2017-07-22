package com.potaychuk.parser;

import java.util.List;

/**
 * Created by Potaychuk Sviatoslav on 17.07.2017.
 */
public interface Parser<T> {
    List<T> parse();
}
