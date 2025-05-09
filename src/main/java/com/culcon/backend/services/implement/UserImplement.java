package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.CartItemDTO;
import com.culcon.backend.dtos.CloudinaryImageDTO;
import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.dtos.blog.BlogComment;
import com.culcon.backend.dtos.blog.BlogItemInList;
import com.culcon.backend.dtos.blog.UserCommentList;
import com.culcon.backend.exceptions.custom.OTPException;
import com.culcon.backend.models.*;
import com.culcon.backend.repositories.*;
import com.culcon.backend.services.CloudinaryService;
import com.culcon.backend.services.UserService;
import com.culcon.backend.services.authenticate.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserImplement implements UserService {

	private final AccountRepo userRepository;
	private final AuthService authService;
	private final PasswordEncoder passwordEncoder;
	private final AccountOTPRepo accountOTPRepo;
	private final AccountRepo accountRepo;
	private final CloudinaryService cloudinaryService;
	private final ProductRepo productRepo;
	private final PostCommentRepo postCommentRepo;
	private final BlogRepo blogDocRepo;
	private final BookmarkRepo bookmarkRepo;

	@Override
	public List<Account> getAccounts() {
		return accountRepo.findAll();
	}

	@Override
	public Account getAccountByEmail(String email) throws AccountNotFoundException {
		return userRepository.findAccountByEmail(email.trim())
			.orElseThrow(() -> new AccountNotFoundException("Account not found"));
	}


	@Override
	public Account updateCustomer(
		CustomerInfoUpdateRequest newUserData,
		HttpServletRequest request) {
		var user = authService.getUserInformation(request);

		user.setProfileName(newUserData.profileName());
		user.setUsername(newUserData.username());
		user.setAddress(newUserData.address());
		user.setPhone(newUserData.phone());
		user.setProfileDescription(newUserData.description());

		return userRepository.save(user);
	}

	@Override
	public void updateCustomerEmail(String accountID, String email, String otp, HttpServletRequest request) {
		var account = authService.getUserInformation(request);

		if (account.getEmail().equalsIgnoreCase(email.trim())) {
			throw new IllegalArgumentException("The new email address must be different from the current email address.");
		}

		var accountOTP = accountOTPRepo.findAccountOTPByOtpAndAccountIdAndEmail(otp, accountID, email)
			.orElseThrow(() -> new OTPException("OTP not found"));

		var sqlTimestamp = Timestamp.valueOf(LocalDateTime.now());
		var isTokenExpire = accountOTP.getOtpExpiration().before(sqlTimestamp);

		if (isTokenExpire) {
			throw new OTPException("OTP expired");
		}


		account.setEmail(email.trim());

		accountRepo.save(account);

		accountOTPRepo.delete(accountOTP);

	}


	@Override
	public AuthenticationResponse updateCustomerPassword(CustomerPasswordRequest newUserData, HttpServletRequest request) {
		var user = authService.getUserInformation(request);

		if (!passwordEncoder.matches(newUserData.oldPassword(), user.getPassword())) {
			throw new NoSuchElementException("Old password does not match");
		}

		user.setPassword(passwordEncoder.encode(newUserData.password()));
		user = userRepository.save(user);

		var reauthenticateRequest = AuthenticationRequest.builder()
			.password(newUserData.password())
			.username(user.getUsername())
			.build();

		return authService.authenticate(reauthenticateRequest);

	}

	@Override
	public void updateCustomerPasswordOTP(String otp, String id, String newPassword) {
		var accountOTP = accountOTPRepo.findByOtpAndAccountId(otp, id)
			.orElseThrow(() -> new OTPException("OTP not found"));

		var sqlTimestamp = Timestamp.valueOf(LocalDateTime.now());
		var isTokenExpire = accountOTP.getOtpExpiration().before(sqlTimestamp);

		if (isTokenExpire) {
			throw new OTPException("OTP expired");
		}

		var account = accountOTP.getAccount();

		account.setPassword(passwordEncoder.encode(newPassword));

		accountRepo.save(account);

		accountOTPRepo.delete(accountOTP);
	}

	@Override
	public List<CartItemDTO> fetchCustomerCart(HttpServletRequest request) {
		// stinky ass function
		return authService.getUserInformation(request)
			.getCart()
			.entrySet().stream()
			.map(CartItemDTO::of)
			.toList();
	}

	@Override
	public CartItemDTO addProductToCart(String productId, Integer amount, HttpServletRequest request) {
		var product = productRepo.findById(productId)
			.orElseThrow(() -> new NoSuchElementException("Product not found incart"));

		var account = authService.getUserInformation(request);

		var itemAmount = account.getCart().getOrDefault(product, 0) + amount;

		if (itemAmount > product.getAvailableQuantity())
			throw new RuntimeException("Insufficient quantity");

		if (itemAmount <= 0) {
			account.getCart().remove(product);
		} else {
			account.getCart().put(product, itemAmount);
		}

		accountRepo.save(account);

		return CartItemDTO.builder()
			.product(product)
			.amount(itemAmount).build();
	}

	@Override
	public Map<String, Object> setProductAmountInCart(String productId, Integer amount, HttpServletRequest request) {
		var account = authService.getUserInformation(request);

		var product = account.getCart()
			.keySet()
			.stream()
			.filter(prod -> prod.getId().equals(productId))
			.findFirst()
			.orElseThrow(() -> new NoSuchElementException("Product not found in cart"));


		if (amount > product.getAvailableQuantity())
			throw new RuntimeException("Insufficient quantity");

		if (amount <= 0) {
			account.getCart().remove(product);
		} else {
			account.getCart().put(product, amount);
		}

		accountRepo.save(account);

		var map = new HashMap<String, Object>();

		map.put("productId", productId);
		map.put("amount", amount);

		return map;
	}

	@Override
	public Boolean removeProductFromCart(String productId, HttpServletRequest request) {
		var account = authService.getUserInformation(request);

		var product = account.getCart()
			.keySet()
			.stream()
			.filter(prod -> prod.getId().equals(productId))
			.findFirst()
			.orElseThrow(() -> new NoSuchElementException("Product not found in cart"));

		account.getCart().remove(product);

		account = accountRepo.save(account);

		return !account.getCart().containsKey(product);
	}

	@Override
	public CloudinaryImageDTO updateUserProfilePicture(MultipartFile file, HttpServletRequest request) throws IOException {
		var account = authService.getUserInformation(request);

		var info = Map.of(
			"asset_folder", "user_pfp",
			"overwritten", "true",
			"display_name", "pfp_" + account.getId(),
			"public_id", "pfp_" + account.getId()
		);

		var uploadInfo = cloudinaryService.uploadImage(file, info);

		account.setProfilePictureUri((String) uploadInfo.get("url"));

		return CloudinaryImageDTO.from(uploadInfo);
	}

	@Override
	public BlogComment commentOnBlog(String blogId, String comment, HttpServletRequest request) {
		var account = authService.getUserInformation(request);


		var blog = blogDocRepo.findById(blogId).orElseThrow(() -> new NoSuchElementException("Blog not found"));

		var commentEntity = PostComment.builder()
			.commentType(CommentType.POST)
			.post(blog)
			.account(account)
			.comment(comment)
			.build();

		var commentOnBlog = postCommentRepo.save(commentEntity);
		return BlogComment.from(commentOnBlog);
	}

	@Override
	public BlogComment replyComment(String blogId, String commentId, String comment, HttpServletRequest request) {
		var account = authService.getUserInformation(request);

		if (!blogDocRepo.existsById(blogId)) {
			throw new NoSuchElementException("Blog not found");
		}

		var blog = blogDocRepo.findById(blogId).orElseThrow(() -> new NoSuchElementException("Blog not found"));
		var parentCommentId = postCommentRepo.findById(commentId).orElseThrow(() -> new NoSuchElementException("Comment not found"));

		var commentEntity = PostComment.builder()
			.commentType(CommentType.REPLY)
			.post(blog)
			.parentComment(parentCommentId)
			.account(account)
			.comment(comment)
			.build();

		var commentOnBlog = postCommentRepo.save(commentEntity);
		return BlogComment.from(commentOnBlog);
	}

	@Override
	public List<UserCommentList> getAllComments(HttpServletRequest request) {
		var account = authService.getUserInformation(request);
		return postCommentRepo.findAllByAccount(account).stream()
			.map(UserCommentList::from).toList();
	}

	@Override
	public Boolean bookmarkBlog(String blogId, HttpServletRequest request, Boolean bookmark) {
		var account = authService.getUserInformation(request);

		var blog = blogDocRepo.findById(blogId)
			.orElseThrow(() -> new NoSuchElementException("Blog not found"));

		var bookmarkId = BookmarkId.builder()
			.account(account)
			.blog(blog)
			.build();

		var bookmarkEntity = Bookmark.builder().id(bookmarkId).build();
		if (bookmark) {
			return bookmarkRepo.save(bookmarkEntity).equals(bookmarkEntity);
		} else {
			bookmarkRepo.delete(bookmarkEntity);
			return true;
		}
	}

	@Override
	public List<BlogItemInList> getBookmarkedBlog(HttpServletRequest request) {
		var account = authService.getUserInformation(request);
		return bookmarkRepo.findAllById_Account_Id(account.getId()).stream()
			.map(b -> b.getId().getBlog())
			.map(BlogItemInList::from)
			.toList();
	}

	@Override
	public Boolean deleteComment(String commentId, HttpServletRequest request) {
		var account = authService.getUserInformation(request);
		var comment = postCommentRepo.findByIdAndAccount(commentId, account)
			.orElseThrow(() -> new NoSuchElementException("Comment not found"));
		if (comment.getStatus() == CommentStatus.DELETED) {
			throw new IllegalArgumentException("Comment is deleted");
		}
		comment.setStatus(CommentStatus.DELETED);
		comment = postCommentRepo.save(comment);
		return comment.getStatus() == CommentStatus.DELETED;
	}

	@Override
	public Boolean reportComment(String commentId, HttpServletRequest request) {
		var comment = postCommentRepo.findById(commentId)
			.orElseThrow(() -> new NoSuchElementException("Comment not found"));

		comment.setStatus(CommentStatus.REPORTED);
		comment = postCommentRepo.save(comment);
		return comment.getStatus() == CommentStatus.REPORTED;
	}
}
