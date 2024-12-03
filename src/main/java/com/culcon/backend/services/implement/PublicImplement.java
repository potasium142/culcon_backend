package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.ProductDTO;
import com.culcon.backend.models.docs.ProductDoc;
import com.culcon.backend.models.record.Product;
import com.culcon.backend.models.record.ProductStatus;
import com.culcon.backend.models.record.ProductType;
import com.culcon.backend.repositories.docs.MealKitDocRepo;
import com.culcon.backend.repositories.docs.ProductDocRepo;
import com.culcon.backend.repositories.record.ProductRepo;
import com.culcon.backend.services.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PublicImplement implements PublicService {

	private final ProductDocRepo productDocRepo;
	private final ProductRepo productRepo;
	private final MealKitDocRepo mealKitDocRepo;

	@Override
	public ProductDTO fetchProduct(String id) {
		var productInfo = productRepo
			.findById(id)
			.orElseThrow(NoSuchElementException::new);

		ProductDoc productDocs = getDocs(productInfo);

		return ProductDTO.from(productInfo, productDocs);
	}

	@Override
	public List<ProductDTO> fetchAllProducts() {

		return productRepo.findAll()
			.stream().map(product -> ProductDTO.from(product, getDocs(product))
			).toList();
	}

	@Override
	public List<Product> fetchListOfProducts() {
		return productRepo.findAllByProductStatus(ProductStatus.IN_STOCK);
	}

	private ProductDoc getDocs(Product product) {
		ProductDoc productDocs;

		var isMealKit = product.getProductTypes() == ProductType.MEALKIT;

		if (isMealKit) {
			productDocs = mealKitDocRepo
				.findById(product.getId())
				.orElse(null);
		} else {
			productDocs = productDocRepo
				.findById(product.getId())
				.orElse(null);
		}

		return productDocs;
	}
}
