package com.dilaraalk.product.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
		Product product = new Product();
		product.setName(productRequestDto.getName());
		product.setPrice(productRequestDto.getPrice());
		product.setStock(productRequestDto.getStock());
		
		Product savedProduct = productRepository.save(product);
		
		//Entity dto dönüşümü 
		ProductResponseDto responseDto = new ProductResponseDto();
		responseDto.setId(savedProduct.getId());
		responseDto.setName(savedProduct.getName());
		responseDto.setPrice(savedProduct.getPrice());
		responseDto.setStock(savedProduct.getStock());
		
		return responseDto;
	}

	@Override
	public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {
		
		
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Id'si " + id + " olan ürün bulunamadı!"));
		
		
		product.setName(productRequestDto.getName());
		product.setPrice(productRequestDto.getPrice());
		product.setStock(productRequestDto.getStock());
				
		Product updatedProduct = productRepository.save(product);
				
		ProductResponseDto responseDto = new ProductResponseDto();
		responseDto.setId(updatedProduct.getId());
		responseDto.setName(updatedProduct.getName());
	    responseDto.setPrice(updatedProduct.getPrice());
		responseDto.setStock(updatedProduct.getStock());
				
	    return responseDto;
		
	}

	@Override
	public void deleteProduct(Long id) {
		
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Id'si " + id + " olan ürün bulunamadı!"));
		
		productRepository.delete(product);
	}

	@Override
	public ProductResponseDto getProductById(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Id'si " + id + " olan ürün bulunamadı!"));
		
		//Product dto dönüşümü
		ProductResponseDto responseDto = new ProductResponseDto();
		responseDto.setId(product.getId());
		responseDto.setName(product.getName());
		responseDto.setPrice(product.getPrice());
		responseDto.setStock(product.getStock());
	

		return responseDto;
	}

	@Override
	public List<ProductResponseDto> getAllProducts() {
		
		List<Product> productList = productRepository.findAll();
		
		if (productList.isEmpty()) {
			throw new RuntimeException("Hiç ürün bulunamadı!");
		}
		
		List<ProductResponseDto> responseList = productList.stream()
				.map(product -> {
					ProductResponseDto responseDto = new ProductResponseDto();
					responseDto.setId(product.getId());
					responseDto.setName(product.getName());
					responseDto.setPrice(product.getPrice());
					responseDto.setStock(product.getStock());
					return responseDto;
				})
				.collect(Collectors.toList());
		
		
		return responseList;
	}


	@Override
	public Page<ProductResponseDto> getAllProductsPaginated(Pageable pageable) {
		 return productRepository.findAll(pageable)
		            .map(product -> {
		                ProductResponseDto dto = new ProductResponseDto();
		                dto.setId(product.getId());
		                dto.setName(product.getName());
		                dto.setPrice(product.getPrice());
		                dto.setStock(product.getStock());
		                return dto;
		            });
	}


}
