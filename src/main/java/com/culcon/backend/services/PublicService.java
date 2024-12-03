package com.culcon.backend.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PublicService {
	Map<String, Object> fetchProduct(String id);

	List<HashMap<String, Object>> fetchAllProducts();
}
