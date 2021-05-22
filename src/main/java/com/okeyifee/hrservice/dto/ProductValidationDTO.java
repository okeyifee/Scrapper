package com.okeyifee.hrservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductValidationDTO {

    private String name;

    private String brandName;

    private String description;

    private String priceString;

    private long price;

    private Boolean isAvailable;

    private String type;

    private Integer suitableHairType;

    private String size;

    private String imageurl;

    private String producturl;

    private String ingredient;

    private Boolean blackOwned;

    private Boolean sustainablySourced;
}
