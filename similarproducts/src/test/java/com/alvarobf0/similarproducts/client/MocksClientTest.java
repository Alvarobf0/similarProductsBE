package com.alvarobf0.similarproducts.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.alvarobf0.similarproducts.data.dto.ProductDetailDto;
import com.alvarobf0.similarproducts.exception.MocksServiceException;
import com.alvarobf0.similarproducts.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class MocksClientTest {

    private static final String SIMILAR_PRODUCTS_IDS_0_URL = "http://localhost:3001/product/0/similarids";
    private static final String PRODUCT_DETAILS_0_URL = "http://localhost:3001/product/0";

    @InjectMocks
    private MocksClient mocksClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mocksClient, "baseUrl", "http://localhost:3001");
    }

    @Test
    void givenCorrectGetSimilarProductsIdsCall_whenGetSimilarProductsIds_thenReturnSimilarProductsIds() {
        List<String> expectedIds = List.of("1", "2", "3");

        when(restTemplate.exchange(SIMILAR_PRODUCTS_IDS_0_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>(){})).thenReturn(new ResponseEntity<>(List.of("1", "2", "3"), HttpStatus.OK));

        List<String> resultIds = mocksClient.getSimilarProductsIds("0");

        assertEquals(expectedIds, resultIds);
    }

    @Test
    void givenNotFoundSimilarProductsIds_whenGetSimilarProductsIds_thenExceptionIsThrown() {
        when(restTemplate.exchange(SIMILAR_PRODUCTS_IDS_0_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>(){})).thenReturn(new ResponseEntity<>(List.of("1", "2", "3"), HttpStatus.NOT_FOUND));

        assertThrows(NotFoundException.class, () -> mocksClient.getSimilarProductsIds("0"));
    }

    @Test
    void givenMocksServiceError_whenGetSimilarProductsIds_thenExceptionIsThrown() {
        when(restTemplate.exchange(SIMILAR_PRODUCTS_IDS_0_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>(){})).thenReturn(new ResponseEntity<>(List.of("1", "2", "3"), HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(MocksServiceException.class, () -> mocksClient.getSimilarProductsIds("0"));
    }

    @Test
    void givenProductId_whenGetProductDetailsAsync_thenProductDetailsIsReturned()
        throws ExecutionException, InterruptedException {
        ProductDetailDto expectedDto = new ProductDetailDto();
        expectedDto.setId("0");
        expectedDto.setName("name");
        expectedDto.setPrice(0d);
        expectedDto.setAvailability(true);

        when(restTemplate.getForEntity(PRODUCT_DETAILS_0_URL, ProductDetailDto.class)).thenReturn(new ResponseEntity<>(expectedDto, HttpStatus.OK));

        CompletableFuture<ProductDetailDto> future = mocksClient.getProductDetailsAsync("0");
        ProductDetailDto result = future.get();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("0");
    }

    @Test
    void givenMocksServiceFailure_whenGetProductDetailsAsync_thenExceptionIsThrown()
        throws MocksServiceException {
        when(restTemplate.getForEntity(PRODUCT_DETAILS_0_URL, ProductDetailDto.class)).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            mocksClient.getProductDetailsAsync("0").get();
        });

        assertThat(executionException.getCause()).isInstanceOf(MocksServiceException.class);
    }
}