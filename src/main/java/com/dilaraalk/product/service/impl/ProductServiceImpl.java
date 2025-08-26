package com.dilaraalk.product.service.impl;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dilaraalk.category.entity.Category;
import com.dilaraalk.category.repository.CategoryRepository;
import com.dilaraalk.common.util.SlugUtil;
import com.dilaraalk.product.dto.ProductImageDto;
import com.dilaraalk.product.dto.ProductRequestDto;
import com.dilaraalk.product.dto.ProductResponseDto;
import com.dilaraalk.product.dto.ProductSearchRequest;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.entity.ProductImage;
import com.dilaraalk.product.repository.ProductRepository;
import com.dilaraalk.product.service.IProductService;
import com.dilaraalk.product.specification.ProductSpecification;


@Service
public class ProductServiceImpl implements IProductService{
	
	private final ProductRepository productRepository;
	
	private final CategoryRepository categoryRepository;
	
	public ProductServiceImpl(ProductRepository productRepository,
			CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}
	

	@Override
	public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
	    Product product = toEntity(productRequestDto);
	   
        // Slug üret ve benzersizliğini kontrol et
        String slug = SlugUtil.generateUniqueSlug(productRequestDto.getName(), productRepository::existsBySlug);
        product.setSlug(slug);
	    
	    
	    // DTO'dan gelen kategori ID'lerini alıp var mı diye kontrol et
	    if (productRequestDto.getCategoryIds() != null && !productRequestDto.getCategoryIds().isEmpty()) {
	        product.setCategories(new HashSet<>(
	            categoryRepository.findAllById(productRequestDto.getCategoryIds())
	        ));
	    }

		//Dto'dan alıp entity'e dönüştürüp veritabanına kaydettik
		Product savedProduct = productRepository.save(product);
		return toDto(savedProduct);
	}

	@Override
	public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {
		
		
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("Id'si " + id + " olan ürün bulunamadı"));
		
		
		product.setName(productRequestDto.getName());
		product.setPrice(productRequestDto.getPrice());
		product.setStock(productRequestDto.getStock());
		
        // Slug’ı yeniden üret
        String slug = SlugUtil.generateUniqueSlug(productRequestDto.getName(), productRepository::existsBySlug);
        product.setSlug(slug);
				
	    // Kategori ilişkisini güncelle
		if (productRequestDto.getCategoryIds() != null) {
		    product.setCategories(
		        new HashSet<>(categoryRepository.findAllById(productRequestDto.getCategoryIds()))
		    );
		}

		
	    return toDto(productRepository.save(product));
		
	}

	@Override
	public void deleteProduct(Long id) {
		
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("Id'si " + id + " olan ürün bulunamadı"));
		
		productRepository.delete(product);
	}

	@Override
	public ProductResponseDto getProductById(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("Id'si " + id + " olan ürün bulunamadı"));

		return toDto(product);
	}

	@Override
	public List<ProductResponseDto> getAllProducts() {
		
		List<Product> productList = productRepository.findAll();
		
		if (productList.isEmpty()) {
			throw new IllegalStateException("Hiç ürün bulunamadı");
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
		

        if (product.getCategories() != null) {
            dto.setCategories(
                product.getCategories()
                       .stream()
                       .map(Category::getName)
                       .collect(Collectors.toList())
            );
        } else {
            dto.setCategories(List.of());
        }
        
        if (product.getImages() != null) {
            dto.setImages(
                product.getImages()
                       .stream()
                       .map(img -> new ProductImageDto(img.getId(), img.getImageUrl()))
                       .collect(Collectors.toList())
            );
        } else {
            dto.setImages(List.of());
        }

        
		return dto;
	}
	
	//Dto entity dönüşümü 
	private Product toEntity(ProductRequestDto dto) {
	    Product product = new Product();
	    product.setName(dto.getName());
	    product.setPrice(dto.getPrice());
	    product.setStock(dto.getStock());

	    if (dto.getCategoryIds() != null) {
	        product.setCategories(
	            new HashSet<>(categoryRepository.findAllById(dto.getCategoryIds()))
	        );
	    } else {
	        product.setCategories(new HashSet<>());
	    }
	    return product;
	}


	@Override
	public ProductResponseDto uploadProductImages(Long productId, MultipartFile[] files) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalStateException("Id'si " + productId + " olan ürün bulunamadı"));
		
		String uploadDir = "uploads/";
		new File(uploadDir).mkdirs(); //klasör yoksa oluşturur
		
		for(MultipartFile file : files) {
			try {
				String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		        Path filePath = Paths.get(uploadDir, fileName);
		        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		        ProductImage image = ProductImage.builder()
		                .imageUrl("/uploads/" + fileName)
		                .build();

		        product.addImage(image); 
			} catch (IOException e) {
				throw new RuntimeException("Dosya yüklenirken hata oluştu: " + file.getOriginalFilename(),e);
			}
		}
		
		Product saved = productRepository.save(product);
		return toDto(saved);
	}


	@Override
	public Page<ProductResponseDto> searchProducts(ProductSearchRequest request) {
		
		//pageable 
		Sort sort = Sort.by(
				request.getDirection().equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
				request.getSortBy()
				);
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
		
		// specification ile filtreleme
		Specification<Product> spec = ProductSpecification.getProducts(request);
		
		return productRepository.findAll(spec,pageable)
				.map(this::toDto);
	}
	
	


}
