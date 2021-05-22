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

@Component
public class GreenCollectionScraperService implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(GreenCollectionScraperService.class);

    ProductService productService;

    @Autowired
    public GreenCollectionScraperService(ProductService productService) {
        this.productService = productService;
    }

    final String webUrl = "https://curls.biz/";
    List<Element> productLinks = new CopyOnWriteArrayList<>();
    List<String> collections = new CopyOnWriteArrayList<>();

    public void scrape() {
        logger.info("Scrapping CurlS");
        getProductLinks();
    }

    public void getProductLinks() {
        try {
            Document doc = Jsoup.connect(webUrl).get();

            Elements links = doc.select("#menu-collections > li.menu-item.menu-item-type-post_type > a");
            for (Element link : links) {
                collections.add(link.attr("href"));
            }

            for (String link : collections) {
                doc = Jsoup.connect(link).get();
                links = doc.select("h3.product-title > a");
                for (Element element : links) {
                    productLinks.add(element);
                    getProduct(element);
                }
            }

        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    public void getProduct(Element link) throws IOException {
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
            Document doc = Jsoup.connect(link.attr("href")).get();

            productName = doc.select("h1.product_title.entry-title").first().text();
            productLink = link.attr("href");
            sustainablySourced =util.getSustainability("vegan");
            priceString = doc.select("span.woocommerce-Price-amount.amount > bdi").first().text();
            brand = webUrl.substring(8, 13);
            image = doc.select("figure.woocommerce-product-gallery__wrapper > div > a > img").attr("src");
            ingredients = doc.select("div#tab-ingredient_tab").text();
            description = doc.select("div.post-content.woocommerce-product-details__short-description > p:first-child").text();

            available = doc.select("div.summary-container > div.avada-availability > p.stock.in-stock ").text().toLowerCase().equals("in stock");

            String text = doc.select("h1.product_title.entry-title").first().text().toLowerCase();
            String[] str = text.trim().split("\\s+");
            List<String> stringArr;
            stringArr = Arrays.asList(str);

            if (text.contains("oz")) {
                size = getString(size, stringArr);
            } else {
                size = doc.select("div.post-content.woocommerce-product-details__short-description > p > strong").text();
            }


            String productTypeText = doc.getElementsByClass("posted_in").text() + " " + doc.getElementsByClass("tagged_as").text();
            String suitableHairTypeText = doc.select("div.post-content.woocommerce-product-details__short-description > p > a > .alignnone ").attr("alt");
            suitableHairType = util.getHairType(suitableHairTypeText);
            productType = util.getProductType(productTypeText);

            ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productLink, ingredients, blackOwned, sustainablySourced);
            productService.validateProduct(productValidationDTO);
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    static String getString(String size, List<String> al) {
        StringBuilder sizeBuilder = new StringBuilder(size);
        for (String name : al) {
            if (name.contains("oz") && name.length() == 2) {
                int hold = al.indexOf(name);
                String pivot = al.get(hold - 1);
                sizeBuilder.append(pivot);
                sizeBuilder.append(al.get(hold));
            } else if (name.contains("oz") && name.length() > 2){
                sizeBuilder.append(name);
            }
        }
        size = sizeBuilder.toString();
        return size;
    }
}


