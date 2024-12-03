package com.culcon.backend.services.implement;

import com.culcon.backend.models.docs.ProductDoc;
import com.culcon.backend.models.record.Product;
import com.culcon.backend.models.record.ProductType;
import com.culcon.backend.repositories.docs.MealKitDocRepo;
import com.culcon.backend.repositories.docs.ProductDocRepo;
import com.culcon.backend.repositories.record.ProductRepo;
import com.culcon.backend.services.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PublicImplement implements PublicService {

	private final ProductDocRepo productDocRepo;
	private final ProductRepo productRepo;
	private final MealKitDocRepo mealKitDocRepo;

	@Override
	public Map<String, Object> fetchProduct(String id) {
		var productInfo = productRepo
			.findById(id)
			.orElseThrow(NoSuchElementException::new);

		ProductDoc productDocs = getDocs(productInfo);

		var map = new HashMap<String, Object>();

		map.put("product", productInfo);
		map.put("product_doc", productDocs);

		return map;
	}

	@Override
	public List<HashMap<String, Object>> fetchAllProducts() {

		return productRepo.findAll()
			.stream().map(product -> {
				var info = new HashMap<String, Object>();

				info.put("product", product);
				info.put("product_doc", getDocs(product));

				return info;
			}).toList();
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
