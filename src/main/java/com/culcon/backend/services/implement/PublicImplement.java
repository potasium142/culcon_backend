package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.ProductDTO;
import com.culcon.backend.dtos.blog.BlogComment;
import com.culcon.backend.dtos.blog.BlogDetail;
import com.culcon.backend.dtos.blog.BlogItemInList;
import com.culcon.backend.mongodb.docs.ProductDoc;
import com.culcon.backend.models.Account;
import com.culcon.backend.models.Product;
import com.culcon.backend.models.ProductType;
import com.culcon.backend.mongodb.docs.docs.BlogDocRepo;
import com.culcon.backend.mongodb.docs.docs.MealKitDocRepo;
import com.culcon.backend.mongodb.docs.docs.ProductDocRepo;
import com.culcon.backend.repositories.PostCommentRepo;
import com.culcon.backend.repositories.ProductRepo;
import com.culcon.backend.services.PublicService;
import com.culcon.backend.services.authenticate.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PublicImplement implements PublicService {

	private final ProductDocRepo productDocRepo;
	private final ProductRepo productRepo;
	private final MealKitDocRepo mealKitDocRepo;
	private final BlogDocRepo blogDocRepo;
	private final PostCommentRepo postCommentRepo;
	private final AuthService authService;

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

		if (!tags.isEmpty()) {
			blogs = blogs.stream().filter(
				blog -> blog.getTags().stream().anyMatch(tags::contains)
			).toList();
		}

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
