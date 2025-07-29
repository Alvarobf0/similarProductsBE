package com.alvarobf0.similarproducts.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alvarobf0.similarproducts.data.dto.ProductDetailDto;
import com.alvarobf0.similarproducts.exception.MocksServiceException;
import com.alvarobf0.similarproducts.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MocksClient {

    private static final String PATH_SLASH = "/";
    private static final String SIMILAR_IDS_PATH = "/similarids";
    private static final String PRODUCT_PATH = "/product";

    private final RestTemplate restTemplate;

    @Value("${mocks.base-url}")
    private String baseUrl;

    public MocksClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<String> getSimilarProductsIds(String productId) {
        List<String> similarProductIds = new ArrayList<>();

        ResponseEntity<List<String>> response = restTemplate.exchange(baseUrl + PRODUCT_PATH + PATH_SLASH + productId + SIMILAR_IDS_PATH, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {});

        if (response.getStatusCode().is2xxSuccessful()) {
            if (response.getBody() != null) {
                similarProductIds.addAll(response.getBody());
            }
        } else if (response.getStatusCode() == HttpStatusCode.valueOf(404)) {
            log.error("Error getting similar products ids for product with id: {}", productId);
            throw new NotFoundException("Similar product ids not found.");
        } else {
            log.error("Error getting similar products ids for product with id: {}", productId);
            throw new MocksServiceException("Failed to fetch similar product ids.");
        }

        return similarProductIds;
    }

    @Async
    @Cacheable(value = "productDetails", key = "#productId")
    public CompletableFuture<ProductDetailDto> getProductDetailsAsync(String productId) {
        ProductDetailDto productDetails = new ProductDetailDto();

        ResponseEntity<ProductDetailDto> response = restTemplate.getForEntity(baseUrl + PRODUCT_PATH + PATH_SLASH + productId, ProductDetailDto.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            if (response.getBody() != null) {
                productDetails = response.getBody();
            }
        } else {
            log.error("Error getting product details of product with id: {}", productId);
            CompletableFuture<ProductDetailDto> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new MocksServiceException("Failed to fetch details of product with id: " + productId));
            return failedFuture;
        }

        return CompletableFuture.completedFuture(productDetails);
    }
}
