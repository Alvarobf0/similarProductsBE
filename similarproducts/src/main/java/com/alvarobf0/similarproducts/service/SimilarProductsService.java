package com.alvarobf0.similarproducts.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.alvarobf0.similarproducts.data.dto.ProductDetailDto;

public interface SimilarProductsService {

    CompletableFuture<List<ProductDetailDto>> getSimilarProducts(String id);
}
