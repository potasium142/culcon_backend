package com.culcon.backend.services;

import com.culcon.backend.dtos.ProductDTO;
import com.culcon.backend.dtos.blog.BlogComment;
import com.culcon.backend.dtos.blog.BlogDetail;
import com.culcon.backend.dtos.blog.BlogItemInList;
import com.culcon.backend.models.Coupon;
import com.culcon.backend.models.Product;
import com.culcon.backend.models.ProductType;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.List;

public interface PublicService {
	ProductDTO fetchProduct(String id);

	List<Product> fetchListOfProducts();

	List<Product> fetchListOfProductsByCategory(ProductType category);

	List<Product> searchProduct(String keyword, ProductType type);

	List<BlogItemInList> fetchListOfBlog();

	List<BlogItemInList> searchBlogByTitle(String title, HashSet<String> tags);

	List<BlogComment> fetchBlogComment(String id);

	List<BlogComment> fetchReply(String blogId, String commentId);

	BlogDetail fetchBlogDetail(String id, HttpServletRequest req);

	Coupon fetchCoupon(String id);

	List<Coupon> fetchAllValidCoupon();

	List<Coupon> fetchAllCouponForPrice(Float price);
}
