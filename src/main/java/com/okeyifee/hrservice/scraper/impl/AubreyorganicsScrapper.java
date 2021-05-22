package com.okeyifee.hrservice.scraper.impl;//package com.decagon.webscrappinggroupb.service.scraperimpl;

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
public class AubreyorganicsScrapper implements ScraperService {

     private final Logger logger = LoggerFactory.getLogger(AubreyorganicsScrapper.class);

    ProductService productService;
    Util util = new Util();

    @Autowired
    public AubreyorganicsScrapper(ProductService productService) {
        this.productService = productService;
    }

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4999.61 Safari/537.36";
    private final String baseUrl = "https://aubreyorganics.com/collections";

    public void scrape() {
        logger.info("Scrapping Aubreyorganics");
        shampooProperties();
        conditionerProperties();
        stylingProperties();
    }


    /**
     * return product brand
     */
    String brand = baseUrl.substring(8,14);

    /**
     * Scrap for shampooos
     */
    public void shampooProperties() {
        List<String> productCollectionLinks = new ArrayList<>();
        try {
            String ingredients;
            boolean available =  true;
            boolean blackOwned = false;
            String sampooUrl = baseUrl + "/shampoo";
            Document document = Jsoup.connect(sampooUrl).userAgent(USER_AGENT).get();
            Elements elements = document.getElementsByClass("collection__product__container");

            for (Element element : elements) {
                String link = element.getElementsByClass("collection__product__link").attr("href");
                productCollectionLinks.add(sampooUrl + link);
            }

            for (String link : productCollectionLinks) {
                Document doc = Jsoup.connect(link).get();
                String productName = doc.getElementsByClass("pdp-title").text();
                String image = "https:" + doc.select(" div.swiper-slide >img").attr("src");
                String priceString = doc.getElementById("nutra__fullPrice").text();
                String size = doc.getElementsByClass("pdp-subtitle").text();
                String description = doc.getElementsByClass("product__description").select(">div >p").text();
                ingredients = doc.getElementsByClass("section_content").text();
                String productType = util.getProductType(productName);
                int suitableHairType = util.getHairType("all hair type");


                ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, link, ingredients,blackOwned, getSustainability());
                productService.validateProduct(productValidationDTO);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * Scrap for conditioners
     */
    public void conditionerProperties() {
        List<String> productCollectionLinks = new ArrayList<>();
        try {
            boolean available = true;
            boolean blackOwned = false;
            String sampooUrl = baseUrl + "/conditioners";
            Document document = Jsoup.connect(sampooUrl).userAgent(USER_AGENT).get();
            Elements elements = document.getElementsByClass("collection__product__container");

            for (Element element : elements) {
                String link = element.getElementsByClass("collection__product__link").attr("href");
                productCollectionLinks.add(sampooUrl + link);
            }

            for (String link : productCollectionLinks) {
                Document doc = Jsoup.connect(link).get();
                String productName = doc.getElementsByClass("pdp-title").text();
                String image = "https:" + doc.select(" div.swiper-slide >img").attr("src");
                String priceString = doc.getElementById("nutra__fullPrice").text();
                String size = doc.getElementsByClass("pdp-subtitle").text();
                String description = doc.getElementsByClass("product__description").select(">div >p").text();
                String ingredients = doc.getElementsByClass("section_content").text();
                String productType = util.getProductType(productName);
                int suitableHairType = util.getHairType("all hair type");
                available = !doc.select("div.pdp_out_of_stock > p").text().toLowerCase().equals("this item is currently out of stock");

                ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, link, ingredients,blackOwned, getSustainability());
                productService.validateProduct(productValidationDTO);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * Scrap for styling products
     */
    public void stylingProperties() {
        List<String> productCollectionLinks = new ArrayList<>();
        try {
            boolean available = true;
            boolean blackOwned = false;
            String sampooUrl = baseUrl + "/styling";
            Document document = Jsoup.connect(sampooUrl).userAgent(USER_AGENT).get();
            Elements elements = document.getElementsByClass("collection__product__container");

            for (Element element : elements) {
                String link = element.getElementsByClass("collection__product__link").attr("href");
                productCollectionLinks.add(sampooUrl + link);
            }

            for (String link : productCollectionLinks) {
                Document doc = Jsoup.connect(link).get();
                String productName = doc.getElementsByClass("pdp-title").text();
                String priceString = doc.getElementById("nutra__fullPrice").text();
                String image = "https:" + doc.select(" div.swiper-slide >img").attr("src");
                String size = doc.getElementsByClass("pdp-subtitle").text();
                String ingredients = doc.getElementsByClass("section_content").text();
                String description = doc.getElementsByClass("product__description").select(">div >p").text();
                String productType = util.getProductType(productName);
                int suitableHairType = util.getHairType("all hair type");

                ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, link, ingredients,blackOwned, getSustainability());
                productService.validateProduct(productValidationDTO);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    private Boolean getSustainability() {
        Boolean sustainablySourced = util.getSustainability("cruelty free");
        return sustainablySourced;
    }
}