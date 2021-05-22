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
public class MauimoistureScrapper implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(MauimoistureScrapper.class);

    ProductService productService;

    @Autowired
    public MauimoistureScrapper(ProductService productService) {
        this.productService = productService;
    }

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4999.61 Safari/537.36";
    String baseUrl = "https://www.mauimoisture.com/shop/?swoof=1&pa_product-type=conditioner";

    /**
     * return product brand
     */
    String brand = baseUrl.substring(12, 16) + " " + baseUrl.substring(16,24);
    boolean sustainablySourced = util.getSustainability("vegan");

    public void scrape() {
        logger.info("Scrapping mauimoisture");
        mauipageUrlConditioner();
        mauipageUrlShampoo();
        mauipageUrlStyle();
        mauipageUrlTreatProtect();
    }

    /**
     * Scrap for shampoo
     */
    public void mauipageUrlShampoo() {

        try {
            final List<String> mauiPoductLink = new ArrayList<>();
            Document document = Jsoup.connect("https://www.mauimoisture.com/shop/?swoof=1&pa_product-type=shampoo").userAgent(USER_AGENT).get();
            Elements elements = document.select("div.woocommerce");

            for (Element element : elements.select("li")) {
                String urls = element.getElementsByClass("woocommerce-LoopProduct-link").attr("href");
                mauiPoductLink.add(urls);
            }

            for (String link : mauiPoductLink) {
                Document document1 = Jsoup.connect(link).get();
                Elements elements1 = document1.getElementsByClass("row");

                for (Element elem : elements1) {
                    String priceString = "";
                    String brand = "";
                    boolean available;
                    String productType;
                    int suitableHairType;
                    String size = "";
                    boolean blackOwned = false;

                    String productName = elem.getElementsByClass("product-item__category").text();
                    String description = elem.getElementById("collapseOne").text();
                    String ingredients = elem.getElementById("collapseThree").text();
                    String image = elem.getElementsByClass("zoom first").attr("href");

                    available = elem.getElementsByClass("buy-online-retailer-block").text().toLowerCase().equals("in stock");

                    if (image.contains("oz.jpg")) {
                        size = image.substring(image.length() - 8, image.length() - 4);
                    }

                    suitableHairType = util.getHairType(description);
                    productType = util.getProductType(elem.getElementsByClass("product-item__title").text());

                    ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, link, ingredients, blackOwned,sustainablySourced);
                    productService.validateProduct(productValidationDTO);
                }
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * Scrap for conditioners
     */
    public void mauipageUrlConditioner() {
        try {
            final List<String> mauiPoductLink = new ArrayList<>();
            Document document = Jsoup.connect(baseUrl).userAgent(USER_AGENT).get();
            Elements elements = document.select("div.woocommerce");

            for (Element element : elements.select("li")) {
                String urls = element.getElementsByClass("woocommerce-LoopProduct-link").attr("href");
                mauiPoductLink.add(urls);
            }

            for (String link : mauiPoductLink) {
                Document document1 = Jsoup.connect(link).get();
                Elements elements1 = document1.getElementsByClass("row");

                for (Element elem : elements1) {
                    String priceString = null;
                    boolean available = false;
                    String size = null;
                    String productType;
                    int suitableHairType;
                    boolean blackOwned = false;

                    String productName = elem.getElementsByClass("product-item__category").text();
                    String description = elem.getElementById("collapseOne").text();
                    String ingredients = elem.getElementById("collapseThree").text();
                    String image = elem.getElementsByClass("zoom first").attr("href");

                    if (image.contains("oz.jpg")) {
                       size = image.substring(image.length() - 8, image.length() - 4);
                    }

                    suitableHairType = util.getHairType(description);
                    productType = util.getProductType(elem.getElementsByClass("product-item__title").text());

                    ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, link, ingredients, blackOwned, sustainablySourced);
                    productService.validateProduct(productValidationDTO);
                }
            }

        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * Scrap for styling products
     */
    public void mauipageUrlStyle() {
        try {
            final List<String> mauiPoductLink = new ArrayList<>();
            Document document = Jsoup.connect("https://www.mauimoisture.com/shop/type/styling/?swoof=1&pa_product-type=styling&really_curr_tax=114-product_tag").userAgent(USER_AGENT).get();
            Elements elements = document.select("div.woocommerce");

            for (Element element : elements.select("li")) {
                String urls = element.getElementsByClass("woocommerce-LoopProduct-link").attr("href");
                mauiPoductLink.add(urls);
            }

            for (String link : mauiPoductLink) {
                Document document1 = Jsoup.connect(link).get();
                Elements elements1 = document1.getElementsByClass("row");

                for (Element elem : elements1) {
                    String priceString = null;
                    boolean available = false;
                    String size = null;
                    String productType;
                    int suitableHairType;
                    boolean blackOwned = false;

                    String productName = elem.getElementsByClass("product-item__category").text();
                    String description = elem.getElementById("collapseOne").text();
                    String ingredients = elem.getElementById("collapseThree").text();
                    String image = elem.getElementsByClass("zoom first").attr("href");

                    if (image.contains("oz.jpg")) {
                        size = image.substring(image.length() - 8, image.length() - 4);
                    }

                    suitableHairType = util.getHairType(description);
                    productType = util.getProductType(elem.getElementsByClass("product-item__title").text());

                    ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, link, ingredients, blackOwned, sustainablySourced);
                    productService.validateProduct(productValidationDTO);
                }
            }

        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * Scrap for Treat and protect products
     */
    public void mauipageUrlTreatProtect() {
        try {
            final List<String> mauiPoductLink = new ArrayList<>();
            Document document = Jsoup.connect("https://www.mauimoisture.com/shop/type/treatments/?swoof=1&pa_product-type=treatments&really_curr_tax=118-product_tag").userAgent(USER_AGENT).get();
            Elements elements = document.select("div.woocommerce");

            for (Element element : elements.select("li")) {
                String urls = element.getElementsByClass("woocommerce-LoopProduct-link").attr("href");
                mauiPoductLink.add(urls);
            }

            for (String link : mauiPoductLink) {
                Document document1 = Jsoup.connect(link).get();
                Elements elements1 = document1.getElementsByClass("row");

                for (Element elem : elements1) {
                    String priceString = "";
                    boolean available = false;
                    String size = null;
                    String productType;
                    int suitableHairType;
                    boolean blackOwned = false;

                    String productName = elem.getElementsByClass("product-item__category").text();
                    String description = elem.getElementById("collapseOne").text();
                    String ingredients = elem.getElementById("collapseThree").text();
                    String image = elem.getElementsByClass("zoom first").attr("href");

                    if (image.contains("oz.jpg")) {
                        size = image.substring(image.length() - 8, image.length() - 4);
                    }

                    suitableHairType = util.getHairType(description);
                    productType = util.getProductType(elem.getElementsByClass("product-item__title").text());

                    ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, link, ingredients, blackOwned, sustainablySourced);
                    productService.validateProduct(productValidationDTO);
                    }
                }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }
}