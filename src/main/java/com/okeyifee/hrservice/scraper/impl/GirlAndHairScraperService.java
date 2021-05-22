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
public class GirlAndHairScraperService implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(GirlAndHairScraperService.class);

    ProductService productService;

    @Autowired
    public GirlAndHairScraperService(ProductService productService) {
        this.productService = productService;
    }

    List<String> productUrls = new ArrayList<>();
    String baseURL = "https://www.girlandhair.com";

    public void scrape() {
        logger.info("Scrapping GirlandHair");
        getProducts();
    }

    /**
     * Method to get the urls for the individual products
     */
    public void getProductUrls() {
        try {
            Document girlAndHair = Jsoup.connect("https://www.girlandhair.com/collections/under-hair-care").get();
            Elements products = girlAndHair.getElementsByClass("product-grid--title");

            for (Element product : products) {
                productUrls.add(baseURL + product.select("a[href]").attr("href"));
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
            String size;
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
                productName = productPage.select(".product-details-product-title").text();
                priceString = (productPage.select(".money").first().text().replaceAll("[^0-9.]", ""));
                brand = baseURL.substring(12,16) + "+" + baseURL.substring(19,23);
                image = "https:" + productPage.select(".product-single__photo").first().attr("src");
                size = productPage.select(".product-description li").last().text();
                sustainablySourced = util.getSustainability("cruelty free");

                available = productPage.getElementById("AddToCartText").text().toLowerCase().equals("add to cart");

                Elements sections = productPage.select("h3");
                for (Element section : sections) {
                    if (section.text().equals("Ingredients")) {
                        ingredients = section.nextElementSibling().getElementsByTag("em").text();
                    }
                    if (section.text().equals("Description")) {
                        description = section.nextElementSibling().text();
                    }
                }

                suitableHairType = util.getHairType(productName);
                productType = util.getProductType(productName);

                ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productUrl, ingredients, blackOwned,sustainablySourced);
                productService.validateProduct(productValidationDTO);
            } catch (IOException ioe) {
                logger.info("Network error " + ioe.getMessage());
                logger.error("Network error " + ioe.getMessage());
            }
        }
    }
}
