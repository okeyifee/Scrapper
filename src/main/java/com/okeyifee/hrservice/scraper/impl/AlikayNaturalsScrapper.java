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
public class AlikayNaturalsScrapper implements ScraperService {

    private final Logger logger = LoggerFactory.getLogger(AlikayNaturalsScrapper.class);

    ProductService productService;
    Util util = new Util();

    @Autowired
    public AlikayNaturalsScrapper(ProductService productService) {
        this.productService = productService;
    }

    String webUrl = "https://alikaynaturals.com/collections/hair?page=1&view=all";
    String baseURL = "https://alikaynaturals.com";
    List<Element> productLinks = new CopyOnWriteArrayList<>();

    public void scrape() {
        logger.info("Scrapping AlikayNaturals");
        getProductLinks();
    }

    public void getProductLinks() {
        try {
            while (true) {
                Document doc = Jsoup.connect(webUrl).get();

                Elements elements = doc.select("div.product-top > div.product-image > a");
                productLinks.addAll(elements);
                Element linkToNextPage = doc.select("div.infinite-scrolling > a").first();
                if (linkToNextPage == null)
                    break;
                webUrl = baseURL + linkToNextPage.attr("href");
            }
            for (Element link : productLinks) {
                getProduct(link);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    public void getProduct(Element link) throws IOException {
        String productLink;
        String ingredients = null;
        String priceString;
        String brand;
        String description;
        String image;
        String productName;
        String size = "";
        String productType;
        int suitableHairType;
        boolean available = false;
        boolean blackOwned = true;
        Boolean sustainablySourced;
        String[] compareInt = {"1","2","3","4","5","6","7","8","9"};

        try {
            // convert page to generated HTML and convert to document
            productLink = baseURL + link.attr("href");
            Document doc = Jsoup.connect(productLink).get();

            productName = doc.select("header.product-title > h1").first().text();
            if (util.isProductCollections(productName)){
                return;
            }

            sustainablySourced = util.getSustainability("no");
            description = doc.getElementById("tabs-1").text();
            suitableHairType = util.getHairType("all hair type");
            productType = util.getProductType(productName + description);

            String temp = doc.select("header.product-title > h1").first().text().toLowerCase();
            String[] str = temp.trim().split("\\s+");
            List<String> al;
            al = Arrays.asList(str);

            priceString = doc.select("div.total-price > span > span.money").text();
            brand = baseURL.substring(8,22);
            image = ("https:" + doc.getElementById("product-featured-image").attr("src")).split("\\?")[0];

            if (doc.getElementById("product-add-to-cart").attr("value").toLowerCase().equals("add to cart")){
                available = true;
            } else if (doc.getElementById("product-add-to-cart").attr("disabled value").toLowerCase().equals("unavailable")){
                available = false;
            }

            if (doc.select("#tabs > ul > li:nth-child(2)") != null
                    && doc.select("#tabs > ul > li:nth-child(2) > a").text().toLowerCase().equals("ingredients")) {
                ingredients = doc.getElementById("tabs-2").select("p:first-child").text();
            } else if (doc.select("#tabs > ul > li:nth-child(3)") != null
                    && doc.select("#tabs > ul > li:nth-child(3) > a").text().toLowerCase().equals("ingredients")) {
                ingredients = doc.getElementById("tabs-3").select("p:first-child").text();
            }

             if (temp.contains("size")){
                 int hold = al.indexOf("size");
                 size += (al.get(hold - 1) + " " + al.get(hold));
            } else if (temp.contains("oz")){
                 for (String name : al) {
                     if (name.contains("oz") && name.length() == 2){
                         int hold = al.indexOf(name);
                         String pivot = al.get(hold - 1);
                         if (Arrays.asList(compareInt).contains(pivot)){
                             size += pivot;
                             size += " ";
                             size += al.get(hold);
                             break;
                         }
                    } else if(name.contains("oz") && name.length() > 2) {
                         size += name;
                         break;
                     }
                 }
             } else if (temp.contains("gallon")){
                 int hold = al.indexOf("gallon");
                 size += al.get(hold);
             } else {
                 if (doc.select("div.swatch-element.available > label") != null){
                    size = doc.select("div.swatch-element.available > label").text().split(" ")[0];
                }
             }

            ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productLink, ingredients,blackOwned, sustainablySourced);
            productService.validateProduct(productValidationDTO);

        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }
}
