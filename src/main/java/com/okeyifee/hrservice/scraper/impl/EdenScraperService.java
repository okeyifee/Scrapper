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
import java.util.ArrayList;
import java.util.List;

@Component
public class EdenScraperService implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(EdenScraperService.class);

    ProductService productService;

    @Autowired
    public EdenScraperService(ProductService productService) {
        this.productService = productService;
    }

    List<String> productUrls = new ArrayList<>();
    String baseURL = "https://edenbodyworks.com";

    public void scrape() {
        logger.info("Scrapping Eden");
        getProducts();
    }

    /**
     * Method to get the urls for the individual products
     */
    public void getProductUrls(String url) {
        try {
            Document eden = Jsoup.connect(url).get();
            Elements products = eden.select(".product-link");

            for (Element product : products) {
                productUrls.add("https://edenbodyworks.com" + product.attr("href"));
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * Method to get product properties for each product
     */
    public void getProducts() {
        getProductUrls("https://edenbodyworks.com/collections/all");
        getProductUrls("https://edenbodyworks.com/collections/all?page=2");

        for (String productUrl : productUrls) {
            String productName;
            String priceString;
            String size = "";
            String description = "";
            String ingredients = "";
            String image;
            String brand;
            String productType;
            int suitableHairType;
            boolean available;
            boolean sustainablySourced;
            boolean blackOwned = true;

            try {
                Document productPage = Jsoup.connect(productUrl).get();
                String benefits = "";
                String recommendedFor = "";

                sustainablySourced = util.getSustainability("cruelty free");
                productName = productPage.select(".detail .title").text();
                priceString = (productPage.select(".price").first().text().replaceAll("[^0-9.]", ""));
                brand = "EDEN" + baseURL.substring(12, 21);
                image = "https:" + productPage.select(".rimage-wrapper noscript img").first().attr("src");

                Elements buttons = productPage.select(".custom-field--title");
                for (Element button : buttons) {
                    switch (button.text()) {
                        case "Ingredients":
                            ingredients = button.nextElementSibling().text();
                            break;
                        case "Benefits":
                            benefits = button.nextElementSibling().text();
                            break;
                        case "Recommended For":
                            recommendedFor = button.nextElementSibling().text();
                            break;
                    }
                }

                if ((benefits + recommendedFor).length() > 0) {
                    description = benefits + " " + recommendedFor;
                }

                if (util.getHairType(description) == 0){
                    suitableHairType = util.getHairType("all hair types");
                } else {
                    suitableHairType = util.getHairType(description);
                }


                if (util.getProductType(productName).isEmpty()){
                    productType = util.getProductType(productPage.getElementsByClass("custom-field--value").text());
                } else {
                    productType = util.getProductType(productName);
                }



//                System.out.println("checks:   " + productPage.getElementsByClass("custom-field--value").text());

                available = !productPage.getElementsByClass("soldout").text().toLowerCase().equals("sold out");
                ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productUrl, ingredients, blackOwned, sustainablySourced);
                productService.validateProduct(productValidationDTO);
            } catch (IOException ioe) {
                logger.info("Network error " + ioe.getMessage());
                logger.error("Network error " + ioe.getMessage());
            }
        }
    }
}
