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
public class TheDouxScraperService implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(TheDouxScraperService.class);

    ProductService productService;

    @Autowired
    public TheDouxScraperService(ProductService productService) {
        this.productService = productService;
    }

    List<String> productUrls = new ArrayList<>();
    String baseUrl = "https://thedoux.com";

    public void scrape() {
        logger.info("Scrapping Doux");
        getProducts();
    }

    /**
     * Method to get the urls for the individual products
     */
    public void getProductUrls() {
        try {
            Document theDoux = Jsoup.connect("https://thedoux.com/products").get();
            Elements products = theDoux.select(".product");

            for (Element product : products) {
                productUrls.add(baseUrl + product.select("a").attr("href"));
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
        getProductUrls();
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
                Elements desc = productPage.select(".product-description strong");

                if (!desc.text().contains("Ingredients:")) {
                    continue;
                }

                productName = productPage.select(".page-title").text();
                image = productPage.select("#productSlideshow img").attr("data-src");
                brand = baseUrl.substring(8,15);
                sustainablySourced = util.getSustainability("no");

                available = !productPage.getElementById("productDetails").getElementsByClass("product-mark").text().toLowerCase().equals("sold out");
                priceString = (productPage.select("#productDetails .sqs-money-native").first().text().replaceAll("[^0-9.]", ""));

                for (Element element : desc) {
                    if (element.text().equals("Ingredients:")) {
                        ingredients = element.parent().text().substring(element.parent().text().indexOf("Ingredients: "))
                                .replaceAll("Ingredients: ", "");
                    }
                    if (element.text().contains("WHAT IT DOUX")) {
                        description = element.parent().text().replaceAll("WHAT IT DOUX[:?] ", "");
                        if (element.parent().text().equals(element.text()))
                            description = element.parent().nextElementSibling().text();
                    }
                }

                if (util.getProductType(productName).isEmpty()){
                    productType = util.getProductType(description);
                } else {
                    productType = util.getProductType(productName);
                }

                if (util.getHairType(description) == 0){
                    suitableHairType = util.getHairType("all hair types");
                } else {
                    suitableHairType = util.getHairType(description);
                }

                ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productUrl, ingredients, blackOwned, sustainablySourced);
                productService.validateProduct(productValidationDTO);
            } catch (IOException ioe) {
                logger.info("Network error " + ioe.getMessage());
                logger.error("Network error " + ioe.getMessage());
            }
        }
    }
}
