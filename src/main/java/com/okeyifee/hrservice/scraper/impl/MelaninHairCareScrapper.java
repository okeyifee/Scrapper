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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MelaninHairCareScrapper implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(MelaninHairCareScrapper.class);

    ProductService productService;

    @Autowired
    public MelaninHairCareScrapper(ProductService productService) {
        this.productService = productService;
    }

    String webUrl = "https://melaninhaircare.com/collections/frontpage";
    String baseURL = "https://melaninhaircare.com";
    List<Element> productLinks = new CopyOnWriteArrayList<>();
    Pattern pattern = Pattern.compile(".*(shampoo|cream|oil|conditioner).*", Pattern.CASE_INSENSITIVE);

    public void scrape() {
        logger.info("Scrapping MelaninHairCare");
        getProductLinks();
    }

    public void getProductLinks() {
        try {
            Document doc = Jsoup.connect(webUrl).get();
            Elements elements = doc.select("div.product");

            for (Element element : elements) {
                if (element.select("div.ci > div.so.icn").isEmpty() && filterNonHairProduct(element)) {
                    productLinks.add(element.select("div.ci > a").first());
                }
            }
            for (Element link : productLinks) {
                getProduct(link);
            }
        } catch (Exception ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    private boolean filterNonHairProduct(Element element) {
        String productName = element.select(".product-details a > h3").text();
        Matcher matcher = pattern.matcher(productName);
        return matcher.matches();
    }

    public void getProduct(Element link) throws Exception {
        String productLink;
        String ingredients;
        String priceString;
        String description;
        String image;
        String productName;
        String size = "";
        String brand;
        String productType;
        int suitableHairType;
        boolean available;
        boolean sustainablySourced;
        boolean blackOwned = true;

        try {
            // convert page to generated HTML and convert to document
            productLink = baseURL + link.attr("href");
            Document doc = Jsoup.connect(productLink).get();

            productName = doc.getElementById("product-description").child(0).text();
            priceString = doc.getElementById("product-price").child(0).text();
            description = doc.select("div.rte").first().child(2).text();
            image = ("https:" + doc.getElementById("product-main-image").select("img").attr("src")).split("\\?")[0];
            ingredients = getIngredient(doc);
            brand = baseURL.substring(8,15);
            sustainablySourced = util.getSustainability("no");

            String temp2 = doc.getElementsByClass("qualityIngredients").text().toLowerCase();
            String[] str = temp2.trim().split("\\s+");
            List<String> al;
            al = Arrays.asList(str);

            if (temp2.contains("oz")) {
                size = getString(size, al);
            } else {
                String sample = doc.getElementsByClass("rte").get(0).text().toLowerCase();
                String[] str2 = sample.trim().split("\\s+");
                List<String> al2;
                al2 = Arrays.asList(str2);
                size = getString(size, al2);
            }

            available = !doc.select("span.product-price").text().toLowerCase().equals("sold out");

            suitableHairType = util.getHairType(description);
            productType = util.getProductType(productName);

            ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productLink, ingredients, blackOwned, sustainablySourced);
            productService.validateProduct(productValidationDTO);
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * Method to return size
     */
    private String getString(String size, List<String> al2) {
        size = GreenCollectionScraperService.getString(size, al2);
        return size;
    }

    /**
     * Method to return product ingredient
     */
    public String getIngredient(Document doc) throws IOException {
        return doc.select("table > tbody > tr > td").text();
    }
}
