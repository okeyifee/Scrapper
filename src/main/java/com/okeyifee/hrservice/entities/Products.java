package com.okeyifee.hrservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@EqualsAndHashCode(callSuper = true, exclude = {"feedbacks"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "products")
public class Products extends Base implements Serializable {

    static final long serialVersionUID = 1L;

    @NotBlank(message = "product name should not be empty")
    private String name;

    @NotBlank(message = "brands name should not be empty")
    private String brandName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "price should not be empty")
    private Long price;

    @NotNull(message = "isAvailable should not be empty")
    private Boolean isAvailable;

    @Column(columnDefinition = "TEXT")
    private String type;

    private Integer suitableHairType;

    private String size;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "image should not be empty")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "url should not be empty")
    private String productUrl;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "ingredient should not be empty")
    private String ingredient;

    private Boolean blackOwned;

    private Boolean sustainablySourced;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    private Set<Feedback> feedbacks;

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", brandName='" + brandName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                ", type='" + type + '\'' +
                ", suitableHairType='" + suitableHairType + '\'' +
                ", size='" + size + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", productUrl='" + productUrl + '\'' +
                ", ingredient='" + ingredient + '\'' +
                ", blackOwned=" + blackOwned +
                '}';
    }
}

