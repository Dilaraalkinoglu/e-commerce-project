package com.dilaraalk.product.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dilaraalk.common.exception.ProductListEmptyException;
import com.dilaraalk.common.exception.ProductNotFoundException;
import com.dilaraalk.product.dto.ProductRequestDto;
import com.dilaraalk.product.dto.ProductResponseDto;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.repository.ProductRepository;
import com.dilaraalk.product.service.IProductService;


@Service
public class ProductServiceImpl implements IProductService{
	
	
	private final ProductRepository productRepository;
	
	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	

	@Override
	public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
		//Dto'dan alıp entity'e dönüştürüp veritabanına kaydettik
		Product savedProduct = productRepository.save(toEntity(productRequestDto));
		return toDto(savedProduct);
	}

	@Override
	public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {
		
		
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException(id));
		
		
		product.setName(productRequestDto.getName());
		product.setPrice(productRequestDto.getPrice());
		product.setStock(productRequestDto.getStock());
				
	    return toDto(productRepository.save(product));
		
	}

	@Override
	public void deleteProduct(Long id) {
		
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException(id));
		
		productRepository.delete(product);
	}

	@Override
	public ProductResponseDto getProductById(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException(id));

		return toDto(product);
	}

	@Override
	public List<ProductResponseDto> getAllProducts() {
		
		List<Product> productList = productRepository.findAll();
		
		if (productList.isEmpty()) {
			throw new ProductListEmptyException();
		}
		
		return productList.stream().map(this::toDto).collect(Collectors.toList());
	}


	@Override
	public Page<ProductResponseDto> getAllProductsPaginated(Pageable pageable) {
		 return productRepository.findAll(pageable).map(this::toDto);
	}
	
	//Entity dto dönüşümü 
	private ProductResponseDto toDto(Product product){
		ProductResponseDto dto = new ProductResponseDto();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setPrice(product.getPrice());
		dto.setStock(product.getStock());
		return dto;
	}
	
	//Dto entity dönüşümü 
	private Product toEntity(ProductRequestDto dto) {
		Product product = new Product();
		product.setName(dto.getName());
		product.setPrice(dto.getPrice());
		product.setStock(dto.getStock());
		return product;
	}
	
	


}
