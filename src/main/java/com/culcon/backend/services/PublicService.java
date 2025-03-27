package com.culcon.backend.services;

import com.culcon.backend.dtos.PageDTO;
import com.culcon.backend.dtos.ProductDTO;
import com.culcon.backend.dtos.blog.BlogComment;
import com.culcon.backend.dtos.blog.BlogDetail;
import com.culcon.backend.dtos.blog.BlogItemInList;
import com.culcon.backend.models.Coupon;
import com.culcon.backend.models.Product;
import com.culcon.backend.models.ProductType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;

public interface PublicService {
	ProductDTO fetchProduct(String id);

	PageDTO<?> fetchListOfProducts(Pageable pageable);

	PageDTO<?> fetchListOfProductsByCategory(ProductType category, Pageable pageable);

	List<Product> searchProduct(String keyword, ProductType type);


	PageDTO<?> fetchListOfBlog(Pageable pageable);

	List<BlogItemInList> searchBlogByTitle(String title, HashSet<String> tags);

	List<BlogComment> fetchBlogComment(String id);

	List<BlogComment> fetchReply(String blogId, String commentId);

	BlogDetail fetchBlogDetail(String id, HttpServletRequest req);

	Coupon fetchCoupon(String id);

	List<Coupon> fetchAllValidCoupon();

	List<Coupon> fetchAllCouponForPrice(Float price);
}
