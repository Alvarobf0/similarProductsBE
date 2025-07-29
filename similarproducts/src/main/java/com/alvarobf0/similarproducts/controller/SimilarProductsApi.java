package com.alvarobf0.similarproducts.controller;

import static com.alvarobf0.similarproducts.constant.ApiConstant.INTERNAL_SERVER_ERROR_CODE;
import static com.alvarobf0.similarproducts.constant.ApiConstant.INTERNAL_SERVER_ERROR_REASON;
import static com.alvarobf0.similarproducts.constant.ApiConstant.NOT_FOUND_CODE;
import static com.alvarobf0.similarproducts.constant.ApiConstant.NOT_FOUND_REASON;
import static com.alvarobf0.similarproducts.constant.ApiConstant.OK_CODE;
import static com.alvarobf0.similarproducts.constant.ApiConstant.OK_REASON;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.alvarobf0.similarproducts.data.dto.ProductDetailDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "SimilarProducts", description = "Endpoints for Similar Products")
public interface SimilarProductsApi {

    @Operation(summary = "Gets similar products", description = "List of similar products to a given one ordered by similarity.")
    @ApiResponse(responseCode = OK_CODE, description = OK_REASON, content = { @Content(schema = @Schema(implementation = ProductDetailDto.class))})
    @ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_REASON, content = { @Content(schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_REASON, content = { @Content(schema = @Schema(implementation = String.class))})
    @GetMapping(path = "product/{productId}/similar")
    CompletableFuture<ResponseEntity<List<ProductDetailDto>>> fetchSimilarProducts(@PathVariable String productId);
}
