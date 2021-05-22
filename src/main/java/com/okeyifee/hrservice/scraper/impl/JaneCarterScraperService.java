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
public class JaneCarterScraperService implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(JaneCarterScraperService.class);

    ProductService productService;

    @Autowired
    public JaneCarterScraperService(ProductService productService) {
        this.productService = productService;
    }

    List<String> productUrls = new ArrayList<>();
    String baseUrl = "https://janecartersolution.com";

    public void scrape() {
        logger.info("Scrapping JaneCarter");
        getProducts();
    }

    /**
     * Method to get the urls for the individual products
     */
    public void getProductUrls() {
        try {
            Document janeCarter = Jsoup.connect("https://janecartersolution.com/collections/all").get();
            Elements products = janeCarter.select(".product-item");

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
            String priceString = "";
            String size = "";
            String description = null;
            String ingredients;
            String image;
            String brand;
            String productType;
            int suitableHairType;
            boolean available;
            boolean sustainablySourced;
            boolean blackOwned = true;

            try {
                Document productPage = Jsoup.connect(productUrl).get();

                productName = productPage.select(".page-title").text();
                image = "https:" + productPage.select(".photo").attr("href");
                brand = baseUrl.substring(8,26);
                sustainablySourced = util.getSustainability("no");

                Element sizeSection = productPage.getElementById("section5");
                if (sizeSection != null && sizeSection.text().equals("Size")) {
                    size = sizeSection.nextElementSibling().select("p").text();
                    priceString = (productPage.select(".actual-price").first().text().replaceAll("[^0-9.]", ""));

                } else {
                    Elements variants = productPage.select(".variants option");
                    for (Element variant : variants) {
                        size = variant.text().substring(0, variant.text().indexOf(" -"));
                        priceString = (variant.text().substring(variant.text().indexOf(" - ")).replaceAll("[^0-9.]", ""));
                    }
                }

                Element ingredientsSection = productPage.getElementById("section4");
                if (!(ingredientsSection != null && ingredientsSection.text().equals("List of Ingredients"))) {
                    continue;
                }
                ingredients = ingredientsSection.nextElementSibling().select("p").text();
                available = !productPage.getElementsByClass("error").text().toLowerCase().equals("this product is currently sold out");

                String fullDescription = "";
                Element descriptionSection1 = productPage.getElementById("section1");
                Element descriptionSection2 = productPage.getElementById("section2");
                if (descriptionSection1 != null) {
                    fullDescription += descriptionSection1.nextElementSibling().text() + "\n";
                }
                if (descriptionSection2 != null) {
                    fullDescription += descriptionSection2.nextElementSibling().text();
                }
                if (fullDescription.length() > 0) {
                    description = fullDescription;
                }
                if (description != null){
                    switch (util.getHairType(description)){
                        case 0:
                            suitableHairType = util.getHairType("all hair types");
                            break;
                        default:
                            suitableHairType = util.getHairType(description);
                            break;
                    }
                } else {
                    suitableHairType = util.getHairType("all hair types");
                }
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
