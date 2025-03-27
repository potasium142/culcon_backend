package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.PageDTO;
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
import org.springframework.data.domain.Pageable;
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
	private final MealkitIngredientsRepo mealkitIngredientsRepo;

	@Override
	public ProductDTO fetchProduct(String id) {
		var productInfo = productRepo
			.findById(id)
			.orElseThrow(NoSuchElementException::new);

		ProductDoc productDocs = productDocRepo.findById(id).orElseThrow(NoSuchElementException::new);


		var ingredients = mealkitIngredientsRepo
			.findAllById_Mealkit_Id(id).stream()
			.map(mk -> mk.getId().getIngredient())
			.toList();


		return ProductDTO.from(productInfo, productDocs, ingredients);
	}

	@Override
	public PageDTO<?> fetchListOfProducts(Pageable pageable) {
		var content = productRepo.findAll(pageable);
		return PageDTO.of(content);
	}

	@Override
	public PageDTO fetchListOfProductsByCategory(ProductType category, Pageable pageable) {
		var content = productRepo.findAllByProductTypes(category, pageable);
		return PageDTO.of(content);
	}

	@Override
	public List<Product> searchProduct(String keyword, ProductType type) {
		if (type == null)
			return productRepo.findAllByProductNameContainingIgnoreCase(keyword);
		else
			return productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes(keyword, type);
	}

	@Override
	public PageDTO<?> fetchListOfBlog(Pageable pageable) {
		var pageContent = blogDocRepo.findAll(pageable);

		var content = pageContent.map(BlogItemInList::from).toList();

		return PageDTO.of(content, pageContent);
	}

	@Override
	public List<BlogItemInList> searchBlogByTitle(String title, HashSet<String> tags) {
		var blogs = blogDocRepo.findAllByTitleContainingIgnoreCase(title);


		return blogs.stream().map(BlogItemInList::from).toList();
	}

	@Override
	public List<BlogComment> fetchBlogComment(String id) {
		return postCommentRepo
			.findAllByPostIdAndCommentType(id, CommentType.POST).stream()
			.map(BlogComment::from).toList();
	}

	@Override
	public List<BlogComment> fetchReply(String blogId, String commentId) {
		return postCommentRepo
			.findAllByPostIdAndParentComment_Id(blogId, commentId).stream()
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

		Boolean bookmark;

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

	@Override
	public List<Coupon> fetchAllCouponForPrice(Float price) {
		return couponRepo.findAllByMinimumPriceLessThan(price);
	}
}
