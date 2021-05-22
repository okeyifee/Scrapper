package com.okeyifee.hrservice.scraper;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Util {

    public String getProductType(String extract) {

        extract = extract.toLowerCase();
        String productType;

        if (extract.contains("styling gel")) {
            productType = "styling-gel";
        } else if (extract.contains("styling cream") || extract.contains("styling smoother") || extract.contains("styling") || extract.contains("mousse") ||extract.contains("style cream")) {
            productType = "styling-cream";
        } else if (extract.contains("deep condition") && extract.contains("mask")) {
            productType = "mask";
        }  else if (extract.contains("protective styles")){
            productType = "protective styles";
        }else if (extract.contains("deep conditioner")) {
            productType = "deep-conditioner";
        } else if (extract.contains("shampoo")) {
            productType = "shampoo";
        } else if (extract.contains("co wash") || extract.contains("cowash") || extract.contains("co-wash")) {
            productType = "co-wash";
        } else if (extract.contains("hair mask") || extract.contains("masque") || extract.contains("mask")) {
            productType = "hair-mask";
        } else if (extract.contains("growth oil") || extract.contains("growth")) {
            productType = "growth-oil";
        } else if (extract.contains("leave in condition") || extract.contains("leave-in condition") || extract.contains("leave-in revitalizer") || extract.contains("leave-in curl revitalizer") || extract.contains("leave-in") || extract.contains("hydrator") || extract.contains("twist out foam")) {
            productType = "leave-in-conditioner";
        } else if (extract.contains("curl cream") || extract.contains("curl lotion") || extract.contains("curl defining cream") || extract.contains("curls cream") || extract.contains("curl enhancing cream")) {
            productType = "curl-cream";
        } else if (extract.contains("gel")) {
            productType = "gel";
        } else if (extract.contains("edge control") || extract.contains("control edge")) {
            productType = "edge-control";
        } else if (extract.contains("edge cream")) {
            productType = "edge-cream";
        } else if (extract.contains("edge oil")) {
            productType = "edge-oil";
        } else if (extract.contains("oil")) {
            productType = "oil";
        } else if (extract.contains("conditioner") || extract.contains("detangle") || extract.contains("de-tangle") || extract.contains("clarifying rinse") || extract.contains("detangling") || extract.contains("conditioning") || extract.contains("conditions")) {
            productType = "conditioner";
        } else if (extract.contains("hair cream")) {
            productType = "hair-cream";
        } else if (extract.contains("balm")) {
            productType = "balm";
        } else if (extract.contains("hair milk")) {
            productType = "hair-milk";
        } else if (extract.contains("hair rinse")) {
            productType = "hair-rinse";
        } else if (extract.contains("reactivator")) {
            productType = "reactivator";
        } else if (extract.contains("moisturizer") || extract.contains("moisturizing cleanser") || extract.contains("moisturizing") || extract.contains("sealing syrup") || extract.contains("restorative butter") || extract.contains("provide moisture") || extract.contains("provide moisture")) {
            productType = "moisturizer";
        } else if (extract.contains("serum") || extract.contains("curl elixir")) {
            productType = "serum";
        } else if (extract.contains("spray")) {
            productType = "hair-spray";
        } else if (extract.contains("powder")) {
            productType = "hair-powder";
        } else if (extract.contains("cleanser")) {
            productType = "cleanser";
        } else if (extract.contains("scalp scrub")) {
            productType = "exfoliant";
        } else if (extract.contains("beauty gummies")) {
            productType = "hair supplements";
        } else if (extract.contains("mist")) {
            productType = "hair mist";
        } else {
            productType = "";
        }
        return productType;
    }



    public Integer getHairType(String extract2) {
        extract2 = extract2.toLowerCase();

        int suitableHairType;

        if (extract2.contains("straight")) {
            suitableHairType = 1;
        } else if (extract2.contains("wavy") || extract2.contains("curl") || extract2.contains("curly") || extract2.contains("some volume") || extract2.contains("large wave") || extract2.contains("tousle") || extract2.contains("twist-elongation")) {
            suitableHairType = 2;
        } else if (extract2.contains("tight") || extract2.contains("corkscrew ringlet") || extract2.contains("springy ringlet") || extract2.contains("large loose curl")) {
            suitableHairType = 3;
        } else if (extract2.contains("zig zag") || extract2.contains("tightest coil") || extract2.contains("tight coil") || extract2.contains("s pattern")) {
            suitableHairType = 4;
        } else if (extract2.contains("all hair type") || extract2.contains("any hair type")) {
            suitableHairType = 1;
        } else {
            suitableHairType = 0;
        }

        return suitableHairType;
    }

    // Regex Matcher
    public boolean isContain(String extract, String subItem) {
        String pattern = "\\b" + subItem + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(extract);
        return m.find();
    }

    public Boolean getSustainability(String answer) {
        List<String> str = new ArrayList<>(Arrays.asList(answer.split(",")));
        Boolean sustainablySourced = false;

        for (String s : str) {
            switch (s.trim().toLowerCase()) {
                case "vegan, cruelty free":
                case "cruelty free, plant based":
                case "vegan":
                case "plant based":
                case "cruelty free":
                    sustainablySourced = true;
                    break;
                default:
                    sustainablySourced = false;
                    break;
            }
        }
        return sustainablySourced;
    }

    /**
     * The method checks if a product is a collection and removes it from
     * product list
     */


    public boolean isProductCollections(String name) {
        return name.toLowerCase().matches(".*(ki(t|ts)|collectio(n|ns)|pack|trio|-trio|body lotion|gift card|body perfume|body spray|body cream|hand sanitizer|stretch mark cream).*");
    }
}