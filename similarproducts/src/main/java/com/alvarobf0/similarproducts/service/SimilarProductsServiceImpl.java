package com.alvarobf0.similarproducts.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.alvarobf0.similarproducts.client.MocksClient;
import com.alvarobf0.similarproducts.data.dto.ProductDetailDto;
import com.alvarobf0.similarproducts.exception.MocksServiceException;
import com.alvarobf0.similarproducts.exception.NotFoundException;

@Service
public class SimilarProductsServiceImpl implements SimilarProductsService {

    private final MocksClient mocksClient;

    public SimilarProductsServiceImpl(MocksClient mocksClient) {
        this.mocksClient = mocksClient;
    }

    @Override
        public CompletableFuture<List<ProductDetailDto>> getSimilarProducts(String productId) {

        List<String> idsSimilarProducts;

        try {
            idsSimilarProducts = mocksClient.getSimilarProductsIds(productId);
        } catch (MocksServiceException | NotFoundException exception) {
            CompletableFuture<List<ProductDetailDto>> failed = new CompletableFuture<>();
            failed.completeExceptionally(exception);
            return failed;
        }

        if (idsSimilarProducts.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<CompletableFuture<ProductDetailDto>> futuresProductDetails = idsSimilarProducts.stream()
            .map(similarId -> mocksClient.getProductDetailsAsync(similarId).exceptionally(exception -> null))
            .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futuresProductDetails.toArray(new CompletableFuture[0]));

        return allFutures.thenApply(f -> futuresProductDetails.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }
}
