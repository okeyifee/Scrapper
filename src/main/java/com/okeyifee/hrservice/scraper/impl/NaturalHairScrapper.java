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
public class NaturalHairScrapper implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(NaturalHairScrapper.class);

    ProductService productService;

    @Autowired
    public NaturalHairScrapper(ProductService productService) {
        this.productService = productService;
    }

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4999.61 Safari/537.36";
    private final List<String> productLinks = new ArrayList<>();
    String baseUrl = "https://naturalhair.org/collections";

    public void scrape() {
        logger.info("Scrapping NaturalHair");
        getProductLinks();
    }

    public void productUrls() {
        List<String> linkCollection = new ArrayList<>();

        try {
            Document document = Jsoup.connect(baseUrl).userAgent(USER_AGENT).get();
            Elements elements = document.getElementsByClass("grid__item col-md-4 col-sm-6 col-xxs-6");

            for (Element element : elements) {
                String links = element.getElementsByTag("a").attr("href");
                linkCollection.add("https://naturalhair.org" + links);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }

        for (String collectionList : linkCollection) {
//            System.out.println("ndhbdsjkbgbdfgbfgddfh:     " + collectionList);
            try {
                Document document = Jsoup.connect(collectionList).get();
                Elements elements = document.getElementsByClass("product-layout grid__item grid__item--collection-template col-md-4 col-sm-4 col-xxs-6 col-xs-12");

                for (Element element : elements) {
                    String productPage = element.getElementsByClass("grid-view-item__link image-ajax").attr("href");
                    productLinks.add("https://naturalhair.org" + productPage);
                }
            } catch (IOException ioe) {
                logger.info("Network error " + ioe.getMessage());
                logger.error("Network error " + ioe.getMessage());
            }
        }
    }

    public void getProductLinks() {
        productUrls();

        String productLink;
        String ingredients;
        String priceString;
        String description = null;
        String image;
        String productName;
        String size = null;
        String brand;
        String productType;
        int suitableHairType;
        boolean available;
        boolean sustainablySourced;
        boolean blackOwned = false;

        try {
            for (String links : productLinks) {
                productLink = links;

                Document document = Jsoup.connect(links).get();
                Elements elements = document.getElementsByClass("product-single");
                for (Element element : elements) {


                    productName = element.getElementsByClass("product-single__title").text();
                    priceString = element.getElementById("ProductPrice-product-template").text();
                    image = "https:" + element.getElementsByTag("img").attr("src");
                    ingredients = element.select("#tabs1 p ").last().text();
                    brand = "TALIAH WAAJID";
                    sustainablySourced = util.getSustainability("no");

                    available = !element.select("p.product-single__alb.instock > p").text().toLowerCase().equals("in stock");

                    if (productName.contains("oz")) {
                        size = productName.substring(productName.length() - 4);
                    }

                    Elements descriptions = element.select("p strong");
                    for (Element desc : descriptions) {
                        if (desc.text().contains("What It Does:")) {
                            description = desc.parent().text().replaceAll("What It Does: ", "");
                        }
                    }
                    System.out.println("plottttttttttttttttt: " + element.select("div.block.widget-categories.spaceBlock"));
//                    System.out.println("kkkkkkkk:   " + element.select("div.block.widget-categories.spaceBlock > div > ul > li:nth-child(3) > a"));

                    suitableHairType = util.getHairType(description);
                    productType = util.getProductType(productName);
//                    System.out.println("natural hair: " + suitableHairType + "...." + productType);

            ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productLink, ingredients, blackOwned, sustainablySourced);
            productService.validateProduct(productValidationDTO);
                }
            }
        }
            catch (IOException ioe) {
                logger.info("Network error " + ioe.getMessage());
                logger.error("Network error " + ioe.getMessage());
        }
    }
}
