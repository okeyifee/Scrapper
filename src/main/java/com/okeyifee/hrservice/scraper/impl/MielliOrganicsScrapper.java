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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MielliOrganicsScrapper implements ScraperService {

    Util util = new Util();
    private final Logger logger = LoggerFactory.getLogger(MielliOrganicsScrapper.class);

    ProductService productService;

    @Autowired
    public MielliOrganicsScrapper(ProductService productService) {
        this.productService = productService;
    }

    String webUrl = "https://mielleorganics.com/collections/all";
    String baseURL = "https://mielleorganics.com";
    List<Element> productLinks = new CopyOnWriteArrayList<>();

    public void scrape() {
        logger.info("Scrapping MielliOrganics");
        getProductLinks();
    }

    public void getProductLinks() {
        try {

            while (true) {
                Document doc = Jsoup.connect(webUrl).get();
                Elements elements = doc.select("div.four.columns > div.product-wrap > div.relative.product_image > a");
                productLinks.addAll(elements);
                Element linkToNextPage = doc.select("div.js-load-more.load-more > a").first();
                if (linkToNextPage == null)
                    break;
                webUrl = baseURL + linkToNextPage.attr("href");
            }

            for (Element link : productLinks) {
                getProduct(link);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    public void getProduct(Element link) {
        String productLink;
        String ingredients;
        String priceString;
        String description;
        String image;
        String productName;
        String size = "";
        String brand;
        String productType = "";
        int suitableHairType;
        boolean available;
        boolean sustainablySourced;
        boolean blackOwned = true;

        try {
            // convert page to generated HTML and convert to document
            productLink = baseURL + link.attr("href");
            Document doc = Jsoup.connect(productLink).get();

            productName = doc.select("h2.product_name").text();
            priceString = doc.select("span.current_price > span > span > span.money").text();
            description = getDescription(doc);
            image = ("https:" + doc.select("div.image__container > img").attr("src")).split("\\?")[0];
            ingredients = getIngredient(doc);
            brand = baseURL.substring(8,14).toUpperCase();
            sustainablySourced = util.getSustainability("no");

            String hold = doc.select("div.description.bottom > P > strong").text();
            if (hold.contains("|")) {
                String[] str = hold.trim().split("\\s+");
                List<String> al;
                al = Arrays.asList(str);

                    for (String name : al) {
                        if (!name.contains("|")) {
                            size += name;
                        } else {
                            break;
                        }
                    }
            }

            available = !doc.select("span.sold_out").text().toLowerCase().equals("currently out of stock");

            suitableHairType = util.getHairType("all hair type");
            if (util.getProductType(productName).isEmpty()){
                productType = util.getProductType(getType(doc));
            } else {
                productType = util.getProductType(productName);
            }

            ProductValidationDTO productValidationDTO = new ProductValidationDTO(productName, brand, description, priceString, 0L, available, productType, suitableHairType, size, image, productLink, ingredients, blackOwned, sustainablySourced);
            productService.validateProduct(productValidationDTO);
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * Method to return product description
     */
    public String getDescription(Document doc) {
        String description = "";
        if (doc.select("div.description.bottom > h3").first() != null)
            description = doc.select("div.description.bottom > h3").first().text();

        if (description.isBlank() && doc.select("div.description.bottom > h3").first() != null)
            description = doc.select("div.description.bottom h3").first().nextElementSibling().text();

        if (!description.isBlank()) {
            return description;
        }
        Elements links = doc.select("div.description.bottom > ul.tabs > li > a");
        for (Element a : links) {
            if ("product".equals(a.text().toLowerCase()) || "product".equals(a.text().toLowerCase())) {
                String id = a.attr("href").replaceAll("#", "");
                description = doc.getElementById(id).text();
            }
        }
        return description;
    }

    /**
     * Method to return product ingredient
     */
    public String getIngredient(Document doc) throws IOException {

        Elements elements = doc.select("div.description.bottom > h3");
        for (Element h3 : elements) {
            if ("ingredients".equals(h3.text().toLowerCase()) || "ingredient".equals(h3.text().toLowerCase())) {
                return h3.nextElementSibling().text();
            }
        }
        Elements links = doc.select("div.description.bottom > ul.tabs > li > a");
        for (Element a : links) {
            if ("ingredients".equals(a.text().toLowerCase()) || "ingredient".equals(a.text().toLowerCase())) {
                String id = a.attr("href").replaceAll("#", "");
                return doc.getElementById(id).text();
            }
        }
        return "";
    }

    public String getType(Document doc) {
        String type = "";
        if (doc.select("div.description.bottom > h3") != null)
            type = doc.select("div.description.bottom > h3").next().text();

        if (type.isBlank() && doc.select("div.description.bottom > h3") != null)
            type = doc.select("div.description.bottom h3").next().text();

        if (!type.isBlank()) {
            return type;
        }
        Elements links = doc.select("div.description.bottom > ul.tabs > li > a");
        for (Element a : links) {
            if ("product".equals(a.text().toLowerCase()) || "product".equals(a.text().toLowerCase())) {
                String id = a.attr("href").replaceAll("#", "");
                type = doc.getElementById(id).text();
            }
        }
        return type;
    }
}
