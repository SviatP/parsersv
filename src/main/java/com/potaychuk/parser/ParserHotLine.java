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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by Potaychuk Sviatoslav on 17.07.2017.
 */
public class ParserHotLine implements Parser<Product>, HotLineMeta, Callable<List<Product>> {

    private String url =ParserHotLine.HOME_URL + ParserHotLine.KEYWORD;
    private int startPage;
    private int endPage;

    public ParserHotLine() {
    }

    public ParserHotLine(int startPage, int endPage) {
        this.startPage = startPage;
        this.endPage = endPage;
    }

    @Override
    public List<Product> call() throws Exception {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i<=endPage-startPage; i++){
            this.url=this.startPage==0?this.url:this.url+"?p="+this.startPage;
            products.addAll(parsePage());
        }
       return products;
    }

//    @Override
//    public List<Product> parsePage() {
//        Document document = null;
//        try {
//            document = Jsoup.connect(ParserHotLine.HOME_URL + ParserHotLine.KEYWORD).timeout(10 * 1000).get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int pages  = Integer.parseInt(document.getElementsByAttributeValue("title","Последняя")
//                .attr("href").replace("?p=",""))+1;
//
//        return null;
//    }

    public List<Product> parsePage() {
        Document document = null;
        try {
//            document = Jsoup.connect(ParserHotLine.HOME_URL + ParserHotLine.KEYWORD).timeout(10 * 1000).get();
            document = Jsoup.connect(this.url).timeout(10 * 1000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements parentDivs = document.getElementsByAttributeValue("class", "m_r-10");
        Elements parentDivs2 = document.getElementsByAttributeValue("class", "cell gd-box");
        List<Element> simpleProductsElements = parentDivs2.stream()
                .filter(p->!p
                        .getElementsByAttributeValue("class","gd-price-sum inl-bot")
                        .attr("data-eventcategory","Pages Catalog")
                        .text()
                        .contains("Все предложения"))
                .collect(Collectors.toList());

        List<Product> products = getFromSimpleProductsElements(simpleProductsElements);
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

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Potaychuk Sviatoslav\\Downloads\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        for (String s: multipleLinks){
            products.addAll(parseProductList(driver,s));
        }
        driver.close();
//        parseProductList(multipleLinks.get(0));
        return products;
    }

    private List<Product> getFromSimpleProductsElements(List<Element> elements){
        List<Product> products = new ArrayList<>();
        for (Element e : elements){
            Product p = new Product();
            p.setName(e.getElementsByAttributeValue("class","g_statistic").text());
            p.setPrice(Double.parseDouble(e.getElementsByAttributeValue("class", "title-14 orng")
                    .text()
                    .replace(Character.toString((char)160),"").replace("грн","").replace(",",".")));
            p.setOfferShop(e.getElementsByAttributeValue("evlab", "Shop name").text());
            p.setInStock(true);
            products.add(p);
        }
        return products;
    }

    public static void main(String[] args) {

        new ParserHotLine().parsePage();
    }



     private List<Product> parseProductList(WebDriver driver, String keyword){

        String url = ParserHotLine.HOME_URL +keyword;

//        WebDriver driver = new ChromeDriver();
        driver.get(url);
//        driver.get("http://hotline.ua/auto-avtoshampuni/red-penguin-avtoshampun-dlya-ruchnoj-mojki-1l-xb50407/prices/");

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
//        driver.close();
        return products;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
