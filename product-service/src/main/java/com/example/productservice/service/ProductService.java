package com.example.productservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
        .descricao(productRequest.getDescricao())
        .build();
        productRepository.save(product);
    }

    public List<ProductResponse> getAllProducts(){
       List<Product> products = productRepository.findAll();
       return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product){
        ProductResponse response = ProductResponse.builder()
        .id(product.getId())
        .nome(product.getNome())
        .descricao(product.getDescricao())
        .preco(product.getPreco())
        .build();
        return response;
    }
}
