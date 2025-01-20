package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.ProductDTO;
import com.culcon.backend.dtos.blog.BlogComment;
import com.culcon.backend.dtos.blog.BlogDetail;
import com.culcon.backend.dtos.blog.BlogItemInList;
import com.culcon.backend.models.*;
import com.culcon.backend.repositories.*;
import com.culcon.backend.services.PublicService;
import com.culcon.backend.services.authenticate.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PublicImplement implements PublicService {

	private final ProductRepo productRepo;
	private final BlogRepo blogDocRepo;
	private final PostCommentRepo postCommentRepo;
	private final AuthService authService;
	private final CouponRepo couponRepo;
	private final ProductDocRepo productDocRepo;

	@Override
	public ProductDTO fetchProduct(String id) {
		var productInfo = productRepo
			.findById(id)
			.orElseThrow(NoSuchElementException::new);

		ProductDoc productDocs = productDocRepo.findById(id).orElseThrow(NoSuchElementException::new);


		return ProductDTO.from(productInfo, productDocs);
	}

	@Override
	public List<Product> fetchListOfProducts() {
		return productRepo.findAll();
	}

	@Override
	public List<Product> fetchListOfProductsByCategory(ProductType category) {
		return productRepo.findAllByProductTypes(category);
	}

	@Override
	public List<Product> searchProduct(String keyword, ProductType type) {
		if (type == null)
			return productRepo.findAllByProductNameContainingIgnoreCase(keyword);
		else
			return productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes(keyword, type);
	}

	@Override
	public List<BlogItemInList> fetchListOfBlog() {
		return blogDocRepo.findAll().stream().map(BlogItemInList::from).toList();
	}

	@Override
	public List<BlogItemInList> searchBlogByTitle(String title, HashSet<String> tags) {
		var blogs = blogDocRepo.findAllByTitleContainingIgnoreCase(title);


		return blogs.stream().map(BlogItemInList::from).toList();
	}

	@Override
	public List<BlogComment> fetchBlogComment(String id) {
		return postCommentRepo
			.findAllByPostInteractionId_PostId(id).stream()
			.map(BlogComment::from).toList();
	}

	@Override
	public BlogDetail fetchBlogDetail(String id, HttpServletRequest req) {
		Account account = null;
		try {
			account = authService.getUserInformation(req);
		} catch (RuntimeException ignored) {
		}

		var blog = blogDocRepo.findById(id).orElseThrow(
			() -> new NoSuchElementException("Blog not found")
		);

		Boolean bookmark = false;

		if (account == null)
			bookmark = null;
		else {
			bookmark = account.getBookmarkedPost().contains(id);
		}


		return BlogDetail.builder()
			.blog(blog)
			.bookmark(bookmark)
			.build();
	}


	@Override
	public Coupon fetchCoupon(String id) {

		var coupon = couponRepo.findById(id).orElseThrow(
			() -> new NoSuchElementException("Coupon Not Found")
		);

		if (coupon.getUsageLeft() <= 0)
			throw new RuntimeException("Coupon ran out of usages");

		if (coupon.getExpireTime().isBefore(LocalDate.now()))
			throw new RuntimeException("Coupon expired");

		return coupon;

	}

	@Override
	public List<Coupon> fetchAllValidCoupon() {
		return couponRepo.findAll()
			.stream().filter(
				coupon -> coupon.getUsageLeft() > 0 && !coupon.getExpireTime().isBefore(LocalDate.now())
			).toList();
	}
}
