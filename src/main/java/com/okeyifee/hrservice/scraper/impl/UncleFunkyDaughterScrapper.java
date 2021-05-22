package com.okeyifee.hrservice.scraper.impl;

import com.okeyifee.hrservice.dto.ProductValidationDTO;
import com.okeyifee.hrservice.scraper.ScraperService;
import com.okeyifee.hrservice.scraper.Util;
import com.okeyifee.hrservice.services.ProductService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class UncleFunkyDaughterScrapper implements ScraperService {

    Util util = new Util();

    private final Logger logger = LoggerFactory.getLogger(UncleFunkyDaughterScrapper.class);

    ProductService productService;

    @Autowired
    public UncleFunkyDaughterScrapper(ProductService productService) {
        this.productService = productService;
    }

    final String webUrl = "https://unclefunkysdaughter.com/hair-care.html";
    String baseURL = "https://unclefunkysdaughter.com";
    List<Element> productLinks = new CopyOnWriteArrayList<>();

    public void scrape() {
        logger.info("Scrapping UncleFunky'sDaughter");
        getProductLinks();
    }

    public void getProductLinks() {
        try {
            Document doc = Jsoup.connect(webUrl).get();
            Elements links = doc.select(".product.photo.product-item-photo > a:first-child");
            for (Element link : links) {
                getProduct(link);
                productLinks.add(link);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    public void getProduct(Element link) throws IOException {
        String productLink;
        String ingredients;
        String priceString;
        String description;
        String image;
        String productName;
        String size = "";
        String brand;
        String productType;
        int suitableHairType;
        boolean available;
        boolean sustainablySourced;
        boolean blackOwned = true;

        try {
            // convert page to generated HTML and convert to document
            productLink = link.attr("href");
            Document doc = Jsoup.connect(productLink).get();

            productName = doc.select("h1.page-title > span").text();
            brand = baseURL.substring(8,27);
            sustainablySourced = util.getSustainability("no");
            image = link.child(0).attr("data-src");
            priceString = doc.select("div.product-info-price > div.price-box.price-final_price > span.price-container.price-final_price.tax.weee > span.price-wrapper > span.price").text();
            description = doc.select("div.product-description").text();
            ingredients = doc.select("div.ingredient > div > table tbody > tr > td").text();
            available = doc.select("div.product-info-stock-sku > div.stock.available > span.label").next().text().toLowerCase().equals("in stock");

            String temp2 = doc.select("h1.page-title > span").text().toLowerCase();
            String[] str = temp2.trim().split("\\s+");
            String[] str2 = description.trim().split("\\s+");
            List<String> al;
            List<String> al2;
            al = Arrays.asList(str);
            al2 = Arrays.asList(str2);

            if (temp2.contains("oz")) {
                size = getString(size, al);
            } else {
                size = getString(description, size, al2);
            }

            String temp = doc.select("div.value").text();
            if (util.getHairType(productName + description + temp) == 0){
                suitableHairType = util.getHairType("kinky, wavy, curly");
            } else {
                suitableHairType = util.getHairType(productName + description + temp);
            }

            productType = util.getProductType(productName + description + temp);

            ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productLink, ingredients, blackOwned, sustainablySourced);
            productService.validateProduct(productValidationDTO);

        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * returns product size if it's contained in the product description
     */
    static String getString(String description, String size, List<String> al2) {
        if (description.contains("oz")) {
            size = getString(size, al2);
        }
        return size;
    }

    /**
     * returns product size if it's contained in the product name
     */
    private static String getString(String size, List<String> al2) {
        for (String name : al2) {
            if (name.contains("oz") && name.length() == 2) {
                int hold = al2.indexOf(name);
                String pivot = al2.get(hold - 1);
                size += pivot;
                size += al2.get(hold);
                break;
            }
            if (name.contains("oz") && name.length() > 2) {
                size += name;
                break;
            }
        }
        return size;
    }
}

