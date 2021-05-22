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
public class HoneysScrapper implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(HoneysScrapper.class);
    ProductService productService;

    @Autowired
    public HoneysScrapper(ProductService productService) {
        this.productService = productService;
    }

    private final List<String> honeyProductUrls = new ArrayList<>();
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4999.61 Safari/537.36";
    private static final String baseUrl = "https://www.honeyshandmade.com/collections/hair-care?page=";

    public void scrape() {
        logger.info("Scrapping Honeys");
        productUrl();
    }

    public void productPageUrl() {
        for (int i = 1; i <= 8; i++) {
            String pageUrl = baseUrl + i;
            honeyProductUrls.add(pageUrl);
        }
    }

    public void productUrl() {
        productPageUrl();
        for (String url : honeyProductUrls) {
            String productLink;
            String ingredients;
            String priceString;
            String description = "";
            String image;
            String productName;
            String size = "";
            String brand;
            String productType;
            int suitableHairType;
            boolean sustainablySourced;
            boolean available = false;
            boolean blackOwned = true;

            try {
                final Document productsPage = Jsoup.connect(url).userAgent(USER_AGENT).get();
                Elements elements = productsPage.getElementsByClass("aspect-product__wrapper");

                for (Element element : elements) {
                    productLink = "https://www.honeyshandmade.com/" + element.attr("href");

                    final Document productPage = Jsoup.connect(productLink).get();
                    productName = productPage.getElementsByClass("product_title entry-title").text();
                    priceString = productPage.getElementById("ProductPrice-product-template").text();
                    brand = "Honey's " + baseUrl.substring(18, 26);
                    sustainablySourced = util.getSustainability("cruelty free, plant based");
                    image = productPage.getElementsByClass("zoom_enabled zoom FeaturedImage-product-template").attr("href");

                    if (productPage.select(".large-6 .product_infos .product-inner-data > div:eq(3)") != null) {
                        description = productPage.select(".large-6 .product_infos .product-inner-data > div:eq(3)").text();
                    }

                    String text1 = productPage.select(".product-tabs .panel ").text().toLowerCase();
                    int lengthOfText1 = text1.length() - 1;
                    int stopIndexOfIngredient;
                    if (text1.contains("ingredients:")) {
                        int startIndexOfIngredient = text1.indexOf("ingredients:");
                        if (text1.contains("instructions:") && (text1.indexOf("instructions:") > text1.indexOf("ingredients:"))) {
                            stopIndexOfIngredient = text1.indexOf("instructions:");
                        } else {
                            stopIndexOfIngredient = lengthOfText1;
                        }
                        ingredients = text1.substring(startIndexOfIngredient, stopIndexOfIngredient);
                    } else if (text1.contains("stuffing")) {
                        int startIndexOfIngredient = text1.indexOf("stuffing");
                        if (text1.contains("how to use")) {
                            stopIndexOfIngredient = text1.indexOf("how to use:");
                        } else {
                            stopIndexOfIngredient = lengthOfText1;
                        }
                        ingredients = text1.substring(startIndexOfIngredient, stopIndexOfIngredient);
                    } else if (text1.contains("quinoa - rich")) {
                        int startIndexOfIngredient = text1.indexOf("quinoa - rich");
                        ingredients = text1.substring(startIndexOfIngredient, lengthOfText1);
                    } else {
                        ingredients = "";
                    }

                    if (ingredients.contains("Ingredients:") || ingredients.contains("Stuffing") || ingredients.contains("INGREDIENTS:")) {
                        available = productPage.getElementsByClass("panel").text().toLowerCase().equals("add to cart");
                    }

                    String productTypeText = "";
                    String suitableHairTypeText = "";
                    if (productPage.select(".large-6 .product_infos .product-inner-data > div:eq(3)") != null) {
                        String productInfo = productPage.select(".product-tabs .panel ").text();
                        productTypeText = productInfo + description;
                        suitableHairTypeText = productInfo + description;
                    } else {
                        productTypeText = description;
                        suitableHairTypeText = description;
                    }

                    if (util.getHairType(suitableHairTypeText) == 0){
                        suitableHairType = util.getHairType("all hair types");
                    } else {
                        suitableHairType = util.getHairType(suitableHairTypeText);
                    }

                    productType = util.getProductType(productTypeText + productName);

                    ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productLink, ingredients, blackOwned, sustainablySourced);
                    productService.validateProduct(productValidationDTO);
                }
            } catch (IOException ioe) {
                logger.info("Network error " + ioe.getMessage());
                logger.error("Network error " + ioe.getMessage());
            }
        }
    }
}
