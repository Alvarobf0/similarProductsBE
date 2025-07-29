package com.alvarobf0.similarproducts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alvarobf0.similarproducts.client.MocksClient;
import com.alvarobf0.similarproducts.data.dto.ProductDetailDto;
import com.alvarobf0.similarproducts.exception.MocksServiceException;
import com.alvarobf0.similarproducts.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class SimilarProductsServiceImplTest {


    @InjectMocks
    private SimilarProductsServiceImpl similarProductsService;

    @Mock
    private MocksClient mocksClient;


    @Test
    void givenProductId_whenGetSimilarProducts_thenReturnSimilarProductsDetails() {

        ProductDetailDto productDetailDummy = ProductDetailDto.builder().id("1").build();

        when(mocksClient.getSimilarProductsIds("0")).thenReturn(List.of("1"));
        when(mocksClient.getProductDetailsAsync("1")).thenReturn(CompletableFuture.completedFuture(productDetailDummy));

        CompletableFuture<List<ProductDetailDto>> futureSimilarProductDetails = similarProductsService.getSimilarProducts("0");

        List<ProductDetailDto> actualProductDetails = futureSimilarProductDetails.join();

        assertNotNull(actualProductDetails);
        assertEquals(1, actualProductDetails.size());
        assertEquals("1", actualProductDetails.getFirst().getId());
    }

    @Test
    void givenMocksServiceException_whenGetSimilarProducts_thenThrowException() {

        when(mocksClient.getSimilarProductsIds("0")).thenThrow(new MocksServiceException("Failed to fetch similar product ids."));

        CompletableFuture<List<ProductDetailDto>> futureSimilarProductDetails = similarProductsService.getSimilarProducts("0");

        assertThrows(MocksServiceException.class, () -> {
            try {
                futureSimilarProductDetails.join();
            } catch (CompletionException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    void givenNotFoundException_whenGetSimilarProducts_thenThrowNotFoundException() {

        when(mocksClient.getSimilarProductsIds("0")).thenThrow(new NotFoundException("Similar product ids not found."));

        CompletableFuture<List<ProductDetailDto>> futureSimilarProductDetails = similarProductsService.getSimilarProducts("0");

        assertThrows(NotFoundException.class, () -> {
            try {
                futureSimilarProductDetails.join();
            } catch (CompletionException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    void givenEmptySimilarProductIds_whenGetSimilarProducts_thenReturnSimilarProductsDetails() {

        when(mocksClient.getSimilarProductsIds("0")).thenReturn(Collections.emptyList());

        CompletableFuture<List<ProductDetailDto>> futureSimilarProductDetails = similarProductsService.getSimilarProducts("0");
        List<ProductDetailDto> actualProductDetails = futureSimilarProductDetails.join();

        assertTrue(actualProductDetails.isEmpty());
    }
}