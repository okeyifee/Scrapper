package com.okeyifee.hrservice.scraper.impl;//package com.decagon.webscrappinggroupb.service.scraperimpl;

import com.okeyifee.hrservice.dto.ProductValidationDTO;
import com.okeyifee.hrservice.scraper.ScraperService;
import com.okeyifee.hrservice.scraper.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.okeyifee.hrservice.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class BriogeohairScrapper implements ScraperService {

    Util util = new Util();

    private final Logger logger = LoggerFactory.getLogger(BriogeohairScrapper.class);

    ProductService productService;

    @Autowired
    public BriogeohairScrapper(ProductService productService) {
        this.productService = productService;
    }

    String webUrl = "https://briogeohair.com/collections/all-products";
    String baseURL = "https://briogeohair.com";
    List<Element> productLinks = new CopyOnWriteArrayList<>();

    public void scrape() {
        logger.info("Scrapping Briogeohair");
        getProductLinks();
    }

    public void getProductLinks() {
        try {
            Document doc = Jsoup.connect(webUrl).get();
            Elements elements = doc.select("a.newcol-product");
            productLinks.addAll(elements);
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
        String size;
        String productType;
        int suitableHairType;
        Boolean sustainablySourced;
        boolean blackOwned = true;

        try {
            // convert page to generated HTML and convert to document
            productLink = baseURL + link.attr("href");
            Document doc = Jsoup.connect(productLink).get();

            sustainablySourced = util.getSustainability("cruelty free");
            productName = doc.select("h1.pdp-details-title").first().text();
            priceString = doc.select("h5.pdp-details-price > span").attr("flow-default");
            brand = baseURL.substring(8,15);
            image = ("https:" + link.select("img.newcol-product-img-first").attr("src")).split("\\?")[0];

            String gotten_description = doc.select("div.product-info-content.product-info-rte").get(0).text();
            String text = gotten_description.substring(gotten_description.indexOf("What"));
            description = text.substring(text.indexOf(":")).replace(':',' ').trim();

            String temp = description.toLowerCase();
            if (temp.contains("8 oz") || temp.contains("8oz")){
                size = "8oz";
            } else if (temp.contains("16 oz") || temp.contains("16oz")){
                size = "16oz";
            } else {
                size = "";
            }

            if (util.getHairType(productName + description) == 0){
                suitableHairType = util.getHairType("all hair type");
            } else {
                suitableHairType = util.getHairType(productName + description);
            }
            productType = util.getProductType(productName + description);

            if (doc.select("div.product-info-content.product-info-rte").size() > 0) {
                String checkIngredient = doc.select(" span.product-info-tab-inner").text();
                if (checkIngredient.toLowerCase().contains("ingredients")){
                    ingredients = doc.select("div.product-info-content.product-info-rte").get(1).text();
                }
            } else {
                ingredients = " ";
            }

            ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, true, productType, suitableHairType, size, image, productLink, ingredients,blackOwned,sustainablySourced);
            productService.validateProduct(productValidationDTO);
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }
}

