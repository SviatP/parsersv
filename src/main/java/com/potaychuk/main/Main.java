package com.potaychuk.main;

import com.potaychuk.domain.Product;
import com.potaychuk.parser.ParserHotLine;
import com.potaychuk.writter.ExcelWriter;
import com.potaychuk.writter.Writer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Potaychuk Sviatoslav on 23.07.2017.
 */
public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Document document = null;
        try {
            document = Jsoup.connect(ParserHotLine.HOME_URL + ParserHotLine.KEYWORD).timeout(10 * 1000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        int pages  = Integer.parseInt(document.getElementsByAttributeValue("title","Последняя").attr("href").replace("?p=",""));
        int pages  = 7;

        Callable<List<Product>> callable1 = new ParserHotLine(0,pages/4);
        Callable<List<Product>> callable2 = new ParserHotLine(pages/4+1, pages/2);
        Callable<List<Product>> callable3 = new ParserHotLine(pages/2+1, pages*3/4);
        Callable<List<Product>> callable4 = new ParserHotLine(pages*3/4+1, pages);
        FutureTask<List<Product>> futureTask1 = new FutureTask<List<Product>>(callable1);
        FutureTask<List<Product>> futureTask2 = new FutureTask<List<Product>>(callable2);
        FutureTask<List<Product>> futureTask3 = new FutureTask<List<Product>>(callable3);
        FutureTask<List<Product>> futureTask4 = new FutureTask<List<Product>>(callable4);
        new Thread(futureTask1).start();
        new Thread(futureTask2).start();
        new Thread(futureTask3).start();
        new Thread(futureTask4).start();
        List<Product> products = new ArrayList<>();
        List<Product> list1 = futureTask1.get();
        List<Product> list2 = futureTask2.get();
        List<Product> list3 = futureTask3.get();
        List<Product> list4 = futureTask4.get();
        products.addAll(list1);
        products.addAll(list2);
        products.addAll(list3);
        products.addAll(list4);
        products.forEach(System.out::println);
        Writer writer = new ExcelWriter();
        writer.writeToFile(products);

    }
}
