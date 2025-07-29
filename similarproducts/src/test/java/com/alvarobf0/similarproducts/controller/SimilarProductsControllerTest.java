package com.alvarobf0.similarproducts.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.alvarobf0.similarproducts.data.dto.ProductDetailDto;
import com.alvarobf0.similarproducts.exception.NotFoundException;
import com.alvarobf0.similarproducts.service.SimilarProductsService;

@ExtendWith(MockitoExtension.class)
class SimilarProductsControllerTest {

    private MockMvc mockMvc;

    private final String SIMILAR_PRODUCTS_0_URL = "/product/0/similar";

    @InjectMocks
    private SimilarProductsController similarProductsController;

    @Mock
    private SimilarProductsService similarProductsService;

    @BeforeEach
    void setUp() {
        initMockMvc(similarProductsController);
        assertNotNull(mockMvc);
        assertNotNull(similarProductsController);
        assertNotNull(similarProductsService);
    }

    @Test
    void givenOkProductId_whenFetchSimilarProducts_thenStatusIsOk() throws Exception {
        List<ProductDetailDto> mockResponse = List.of(new ProductDetailDto("0", "name", 0d, true));
        when(similarProductsService.getSimilarProducts("0"))
            .thenReturn(CompletableFuture.completedFuture(mockResponse));

        MvcResult mvcResult = mockMvc.perform(get(SIMILAR_PRODUCTS_0_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(similarProductsService, times(1)).getSimilarProducts("0");
    }

    @Test
    void givenNotFoundProductId_whenFetchSimilarProducts_thenExceptionIsThrown() throws Exception {
        CompletableFuture<List<ProductDetailDto>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new NotFoundException("Similar product ids not found."));

        when(similarProductsService.getSimilarProducts("0")).thenReturn(failedFuture);

        MvcResult mvcResult = mockMvc.perform(get(SIMILAR_PRODUCTS_0_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNotFound());

        verify(similarProductsService, times(1)).getSimilarProducts("0");
    }

    @Test
    void givenInternalErrorProductId_whenFetchSimilarProducts_thenExceptionIsThrown() throws Exception {
        CompletableFuture<List<ProductDetailDto>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Failed to fetch similar product ids."));

        when(similarProductsService.getSimilarProducts("0")).thenReturn(failedFuture);

        MvcResult mvcResult = mockMvc.perform(get(SIMILAR_PRODUCTS_0_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isInternalServerError());

        verify(similarProductsService, times(1)).getSimilarProducts("0");
    }

    private void initMockMvc(SimilarProductsController controller) {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
}