package com.dilaraalk.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDto implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String imageUrl;
}