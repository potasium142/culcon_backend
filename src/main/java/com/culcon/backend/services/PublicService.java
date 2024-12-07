package com.culcon.backend.services;

import com.culcon.backend.dtos.ProductDTO;
import com.culcon.backend.dtos.blog.BlogComment;
import com.culcon.backend.dtos.blog.BlogItemInList;
import com.culcon.backend.models.docs.Blog;
import com.culcon.backend.models.user.Product;
import com.culcon.backend.models.user.ProductType;

import java.util.HashSet;
import java.util.List;

public interface PublicService {
	ProductDTO fetchProduct(String id);

	List<Product> fetchListOfProducts();

	List<Product> searchProduct(String keyword, ProductType type);

	List<BlogItemInList> fetchListOfBlog();

	List<BlogItemInList> searchBlogByTitle(String title, HashSet<String> tags);

	List<BlogComment> fetchBlogComment(String id);

	Blog fetchBlogDetail(String id);
}
