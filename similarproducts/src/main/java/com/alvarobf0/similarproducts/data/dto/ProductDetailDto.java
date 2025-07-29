package com.alvarobf0.similarproducts.data.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Double price;
    private Boolean availability;


}
