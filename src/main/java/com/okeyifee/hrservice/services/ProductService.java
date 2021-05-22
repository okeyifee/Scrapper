package com.okeyifee.hrservice.services;

import com.okeyifee.hrservice.dto.ProductValidationDTO;
import com.okeyifee.hrservice.entities.Products;

import java.util.List;

public interface ProductService {
//    Products getProductByName(String name);
    void saveScrappedProduct(String productName, Products scrappedProduct);
    void validateProduct(ProductValidationDTO productValidationDTO);

    //Methods for product recommendation
//    List<Products> getProductsAfterGeneralFilters(HairProfile hairProfile);
//    List<Products> getValidProductList();
//    List<Products> getDumpList();
//    void setDumpList(List<Products> products);
}
