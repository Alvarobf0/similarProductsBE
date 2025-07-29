package com.alvarobf0.similarproducts.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alvarobf0.similarproducts.data.dto.ProductDetailDto;
import com.alvarobf0.similarproducts.exception.NotFoundException;
import com.alvarobf0.similarproducts.service.SimilarProductsService;

@RestController
@RequestMapping("")
public class SimilarProductsController implements SimilarProductsApi {

    @Autowired
    private SimilarProductsService similarProductsService;

    @Override
    public CompletableFuture<ResponseEntity<List<ProductDetailDto>>> fetchSimilarProducts(String productId) {
        return similarProductsService.getSimilarProducts(productId)
            .thenApply(similarProducts -> ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(similarProducts)
            ).exceptionally(exception -> {if (exception.getCause() instanceof NotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();});
    }
}
