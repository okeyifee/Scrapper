package com.okeyifee.hrservice.services.HrServiceImpl;

import com.okeyifee.hrservice.dto.ProductValidationDTO;
import com.okeyifee.hrservice.entities.Products;
import com.okeyifee.hrservice.repositories.ProductRepository;
import com.okeyifee.hrservice.scraper.Util;
import com.okeyifee.hrservice.services.ProductService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
//    AllergyProductFilter allergyProductFilter;
//    ProductCostFilter productCostFilter;
//    HairTypeProductFilter hairTypeProductFilter;
//    HairPorosityFilter hairPorosityFilter;
    ModelMapper mapper;
    Util util;

    private List<Products> allergyFilteredList;
    private List<Products> budgetFilteredList;
    private List<Products> hairTypeFilteredList;
    private List<Products> porosityFilteredList;
    private List<Products> validProductList;
    private List<Products> dumpList;

    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);


    @Override
    public void saveScrappedProduct(String productName, Products scrappedProduct) {

        try {
            Products product = productRepository.findByName(productName);
            Long newLongPrice = scrappedProduct.getPrice();
            if (product == null) {
                if (newLongPrice == null || newLongPrice == 0L) {
                    return;
                }
                productRepository.save(scrappedProduct);
                logger.info("successfully saved product: " + productName);
            } else if (product != null) {
                scrappedProduct.setId(product.getId());
                scrappedProduct.setCreatedAt(product.getCreatedAt());
                if (newLongPrice == null || newLongPrice == 0L) {
                    scrappedProduct.setIsAvailable(false);
                }
                productRepository.save(scrappedProduct);
                logger.info("successfully updated product: " + productName + " id: " + product.getId());
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void validateProduct(ProductValidationDTO productValidationDTO) {
        String ingredient = productValidationDTO.getIngredient();
        String price = productValidationDTO.getPriceString();
        Long longPrice;

        if ((ingredient != null) && !(ingredient.isEmpty())) {
            if (price == null || price.isEmpty() || price.isBlank()) {
                longPrice = 0L;
            } else {
                double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                longPrice = (long) priceNum;
            }
            if (util.isProductCollections(productValidationDTO.getName())){
                return;
            }
            productValidationDTO.setPrice(longPrice);
            Products scrapedProduct = mapper.map(productValidationDTO, Products.class);
            saveScrappedProduct(scrapedProduct.getName(), scrapedProduct);
        }
    }


    /**
     * This returns a list of all the products that are available and not disliked by the user.
     * @param currentBrands
     * @param productSatisfaction
     * @param dislikedBrands
     * @return
     */
    public List<Products> getValidProducts(List<String> currentBrands, Integer productSatisfaction, List<String> dislikedBrands) {
        List<Products> answer;

        answer = productRepository.getAllAvailableProducts();
        if (dislikedBrands != null && !dislikedBrands.isEmpty()) {
            answer = answer.stream().filter((p) -> {
                for (String disLikedBrand : dislikedBrands) {
                    if (p.getBrandName().toLowerCase().contains(disLikedBrand.toLowerCase()));
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }

        if (productSatisfaction <= 3 && currentBrands != null && !currentBrands.isEmpty()) {
            answer = answer.stream().filter((p) -> {
                for (String currentBrand : currentBrands) {
                    if (p.getBrandName().toLowerCase().contains(currentBrand.toLowerCase()));
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }
        return answer;
    }
}
