package com.culcon.backend.services;

import com.culcon.backend.dtos.ProductDTO;
import com.culcon.backend.models.record.Product;

import java.util.List;

public interface PublicService {
	ProductDTO fetchProduct(String id);

	List<Product> fetchListOfProducts();
}
