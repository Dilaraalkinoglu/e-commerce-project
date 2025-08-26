package com.dilaraalk.admin.controller;

import com.dilaraalk.product.dto.ProductRequestDto;
import com.dilaraalk.product.dto.ProductResponseDto;
import com.dilaraalk.product.service.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

class AdminProductControllerTest {

    private IProductService productService;
    private AdminProductController controller;

    @BeforeEach
    void setUp() {
        productService = mock(IProductService.class);
        controller = new AdminProductController(productService);
    }

    @Test
    void create_shouldCallService() {
        ProductRequestDto dto = new ProductRequestDto();
        ProductResponseDto responseDto = new ProductResponseDto();
        when(productService.createProduct(dto)).thenReturn(responseDto);

        ResponseEntity<ProductResponseDto> response = controller.create(dto);

        assertEquals(responseDto, response.getBody());
        verify(productService).createProduct(dto);
    }

    @Test
    void getAllPaginated_shouldReturnPage() {
        Page<ProductResponseDto> page = new PageImpl<>(List.of(new ProductResponseDto()));
        when(productService.getAllProductsPaginated(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ProductResponseDto>> response = controller.getAllPaginated(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.getBody().getContent().size());
        verify(productService).getAllProductsPaginated(any(Pageable.class));
    }
}
