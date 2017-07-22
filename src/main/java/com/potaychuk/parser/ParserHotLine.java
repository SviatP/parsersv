package com.potaychuk.parser;

import com.potaychuk.domain.Product;
import com.potaychuk.metadata.HotLineMeta;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Potaychuk Sviatoslav on 17.07.2017.
 */
public class ParserHotLine implements Parser<Product>, HotLineMeta {



    public List<Product> parse() {
        Document document = null;
        try {
            document = Jsoup.connect(ParserHotLine.HOME_URL + ParserHotLine.KEYWORD).timeout(10 * 1000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements parentDivs = document.getElementsByAttributeValue("class", "m_r-10");
        Elements parentDivs2 = document.getElementsByAttributeValue("class", "cell gd-box");
        List<Element> simpleProducts = parentDivs2.stream()
                .filter(p->!p
                        .getElementsByAttributeValue("class","gd-price-sum inl-bot")
                        .attr("data-eventcategory","Pages Catalog")
                        .text()
                        .contains("Все предложения"))
                .collect(Collectors.toList());


        Elements names = parentDivs.attr("class","g_statistic");
        Elements multipleOffers = document.getElementsByAttributeValue("class","gd-price-sum inl-bot");
//        Elements names3 = names2.attr("data-eventcategory","Pages Catalog");
//        List<String> texts = names3.stream().map(Element::text).collect(Collectors.toList());
        List<String> subKeywords = names.
                stream().
                map(p->p.child(0).attr("href")).
                collect(Collectors.toList());
        List<String> multipleLinks = multipleOffers.
                stream()
                .filter(p->p.attr("data-eventcategory","Pages Catalog").text().contains("Все предложения"))
                .map(p->p.getElementsByAttribute("href").attr("href")).
                collect(Collectors.toList());
        parseProductList(multipleLinks.get(0));
        return null;
    }

    public static void main(String[] args) {
        new ParserHotLine().parse();
    }



     private List<Product> parseProductList(String keyword){
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Potaychuk Sviatoslav\\Downloads\\chromedriver.exe");
        String url = ParserHotLine.HOME_URL +keyword+"prices/";

        WebDriver driver = new ChromeDriver();
//        driver.get(url);
        driver.get("http://hotline.ua/auto-avtoshampuni/red-penguin-avtoshampun-dlya-ruchnoj-mojki-1l-xb50407/prices/");

        Document document = Jsoup.parse(driver.getPageSource());
        String productName = document.getElementsByAttributeValue("class", "title-24 p_b-5").text();

        Elements divs = document.getElementsByAttributeValue("data-selector", "price-line");
        List<String> isInStock = divs.stream().map(p-> p.getElementsByAttributeValue("class","price txt-right cell5 cell-768 m_b-10-768")
                .attr("class","p_b-5").text()).collect(Collectors.toList());
        List<Product> products = divs.stream().map(Product::new).collect(Collectors.toList());
        for (int i =0; i<products.size(); i++){
            products.get(i).setName(productName);
            products.get(i).setInStock(isInStock.get(i).contains("в наличии"));
        }
        return products;
    }

}
