package com.globant.project.application.ports.in.services;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.globant.project.domain.dto.ProductDTO;
import com.globant.project.domain.entities.ProductEntity;

/**
 * ProductServices
 */
public interface ProductService {

    ProductDTO createProduct(ProductDTO productDto);

    void updateProduct(String uuid, ProductDTO productDto);

    void deleteProduct(String uuid);

    ProductDTO getProduct(String uuid);

    List<ProductDTO> getProducts();

    List<ProductDTO> getProductsByFantasyName(String fantasyName);

    Set<List<ProductEntity>> getProductsAvailablesByCategory();

    ProductEntity getProductEntity(UUID uuid);

    String formatFantasyName(String fantasyName);

}
