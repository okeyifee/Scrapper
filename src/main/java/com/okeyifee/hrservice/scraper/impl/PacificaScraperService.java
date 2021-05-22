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
import java.util.Arrays;
import java.util.List;

@Component
public class PacificaScraperService implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(PacificaScraperService.class);

    ProductService productService;

    @Autowired
    public PacificaScraperService(ProductService productService) {
        this.productService = productService;
    }

    List<String> productUrls = new ArrayList<>();
    String baseUrl = "https://www.pacificabeauty.com";

    public void scrape() {
        logger.info("Scrapping pacifica");
        getProducts();
    }

    /**
     * Method to get the urls for the individual products
     */
    public void getProductUrls() {
        try {
            Document pacifica = Jsoup.connect("https://www.pacificabeauty.com/collections/hair").get();
            System.out.println(pacifica.title());
            Elements products = pacifica.select(".ProductItem__Title");

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
            String size;
            String description;
            String ingredients = null;
            String image;
            String brand;
            String productType;
            int suitableHairType;
            boolean available = false;
            boolean sustainablySourced;
            boolean blackOwned = false;


            try {
                Document productPage = Jsoup.connect(productUrl).get();

                productName = productPage.select(".ProductMeta__Title").text();
                brand = baseUrl.substring(12,20);
                size = "";
                sustainablySourced = util.getSustainability("vegan, cruelty free");
                image = "https:" + productPage.select(".Product__Slideshow img").attr("data-original-src");
                priceString = (productPage.select(".ProductMeta__Price").first().text().replaceAll("[^0-9.]", ""));
                description = productPage.select(".ProductMeta__Description .Rte > p").first().text();

                String text1 = productPage.select(".Text--subdued").text();
                String text2 =  "";

                if (text1.toLowerCase().contains("pieces")){
                    String[] str = text1.trim().split("\\s+");
                    List<String> al;
                    al = Arrays.asList(str);

                    for (String check : al) {
                        if (check.equals("pieces")){
                            String pivot = al.get(al.indexOf("pieces") - 1);
                            text2 += pivot;
                            break;
                        }
                    }
                    int availableProducts = Integer.parseInt(text2);
                    available = availableProducts > 0;
                }

                Elements siblingButtons = productPage.select(".Collapsible__Button");
                for (Element button : siblingButtons) {
                    if (button.text().equals("Ingredients")) {
                        ingredients = button.nextElementSibling().text();
                    }
                }

                suitableHairType = util.getHairType("all hair types");
                productType = util.getProductType(productName);

                ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productUrl, ingredients, blackOwned, sustainablySourced);
                productService.validateProduct(productValidationDTO);
            } catch (IOException ioe) {
                logger.info("Network error " + ioe.getMessage());
                logger.error("Network error " + ioe.getMessage());
            }
        }
    }
}