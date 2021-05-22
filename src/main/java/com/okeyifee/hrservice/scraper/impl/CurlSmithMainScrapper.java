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
public class CurlSmithMainScrapper implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(CurlSmithMainScrapper.class);
    private final List<String> curlSmithProductUrls = new ArrayList<>();

    ProductService productService;

    @Autowired
    public CurlSmithMainScrapper(ProductService productService) {
        this.productService = productService;
    }

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4999.61 Safari/537.36";
    private final String baseUrl = "https://curlsmith.com";

    public void scrape() {
        logger.info("Scrapping CurlSmithMain");
        productProperties();
    }

    public void productUrl() {
        try {
            final Document landingPage = Jsoup.connect(baseUrl).userAgent(USER_AGENT).get();
            Elements elements = landingPage.getElementsByClass("four columns alpha thumbnail even");


            for (Element element : elements) {
                String productsPageUrl = element.getElementsByTag("a").attr("href");
                curlSmithProductUrls.add(baseUrl + productsPageUrl);
            }

            Elements elements1 = landingPage.getElementsByClass("four columns  thumbnail odd");
            for (Element element1 : elements1) {
                String productsPageUrl = element1.getElementsByTag("a").attr("href");
                curlSmithProductUrls.add(baseUrl + productsPageUrl);
            }

            Elements elements2 = landingPage.getElementsByClass("four columns  thumbnail even");
            for (Element element2 : elements2) {
                String productsPageUrl = element2.getElementsByTag("a").attr("href");
                curlSmithProductUrls.add(baseUrl + productsPageUrl);
            }

            Elements elements3 = landingPage.getElementsByClass("four columns omega thumbnail odd");
            for (Element element3 : elements3) {
                String productsPageUrl = element3.getElementsByTag("a").attr("href");
                curlSmithProductUrls.add(baseUrl + productsPageUrl);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    public void productProperties() {
        productUrl();
        String productLink;
        String ingredients;
        String priceString;
        String description;
        String image;
        String productName;
        String brand;
        String size = "";
        String productType;
        int suitableHairType;
        boolean available;
        boolean sustainablySourced;
        boolean blackOwned = false;

        for (String url : curlSmithProductUrls) {
            if (!url.contains("-kit") && !url.contains("-30-day") && !url.contains("-3-step") && !url.contains("3-month")) {
                try {
                    final Document curlSmithProductPage = Jsoup.connect(url).timeout(5000).get();

                    productName = curlSmithProductPage.getElementsByClass("product_name").text();
                    sustainablySourced = util.getSustainability("vegan,cruelty free");
                    productLink = url;
                    priceString = curlSmithProductPage.getElementsByClass("current_price").text();
                    brand = baseUrl.substring(8,17);
                    description = curlSmithProductPage.getElementById("section1").nextElementSibling().text();
                    image = "https://" + curlSmithProductPage.getElementsByClass("fancybox").attr("href");
                    ingredients = curlSmithProductPage.select(".content .ingredient-image .imagetable").text();
                    available = curlSmithProductPage.getElementsByClass("text").text().toLowerCase().equals("add to cart");

                    if (productName.contains("(")) {
                        size = productName.substring(productName.indexOf("(") + 1, productName.indexOf(")"));
                    }

                    String g = curlSmithProductPage.getElementsByClass("mobiletitle").text();
                    productType = util.getProductType(description + g);
                    if (util.getHairType(g) == 0){
                       suitableHairType = util.getHairType("curls");
                    } else {
                       suitableHairType = util.getHairType(g);
                    }

                    ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productLink, ingredients,blackOwned,sustainablySourced);
                    productService.validateProduct(productValidationDTO);
                } catch (IOException ioe) {
                    logger.info("Network error " + ioe.getMessage());
                    logger.error("Network error " + ioe.getMessage());
                }
            }
        }
    }
}