package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.ProductDTO;
import com.culcon.backend.models.docs.ProductDoc;
import com.culcon.backend.models.user.Product;
import com.culcon.backend.models.user.ProductType;
import com.culcon.backend.repositories.docs.MealKitDocRepo;
import com.culcon.backend.repositories.docs.ProductDocRepo;
import com.culcon.backend.repositories.user.ProductPriceRepo;
import com.culcon.backend.repositories.user.ProductRepo;
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
	private final ProductPriceRepo productPriceRepo;

	@Override
	public ProductDTO fetchProduct(String id) {
		var productInfo = productRepo
			.findById(id)
			.orElseThrow(NoSuchElementException::new);

		ProductDoc productDocs = getDocs(productInfo);

		return ProductDTO.from(productInfo, productDocs);
	}

	@Override
	public List<Product> fetchListOfProducts() {
		return productRepo.findAll();
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
