package com.culcon.backend.services;

import com.culcon.backend.dtos.CartItemDTO;
import com.culcon.backend.dtos.CloudinaryImageDTO;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.dtos.blog.BlogComment;
import com.culcon.backend.dtos.blog.BlogItemInList;
import com.culcon.backend.models.user.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {

	List<Account> getAccounts();

	Account getAccountByEmail(String email) throws AccountNotFoundException;

	Account updateCustomer(CustomerInfoUpdateRequest newData, HttpServletRequest request);

	AuthenticationResponse updateCustomerPassword(CustomerPasswordRequest newData, HttpServletRequest request);

	void updateCustomerPasswordOTP(String otp, String id, String newPassword);

	List<CartItemDTO> fetchCustomerCart(HttpServletRequest request);

	CartItemDTO addProductToCart(String productId, Integer amount, HttpServletRequest request);

	Map<String, Object> setProductAmountInCart(String productId, Integer amount, HttpServletRequest request);

	Boolean removeProductFromCart(String productId, HttpServletRequest request);

	CloudinaryImageDTO updateUserProfilePicture(MultipartFile file, HttpServletRequest request) throws IOException;

	BlogComment commentOnBlog(String blogId, String comment, HttpServletRequest request);

	Boolean bookmarkBlog(String blogId, HttpServletRequest request, Boolean bookmark);

	List<BlogItemInList> getBookmarkedBlog(HttpServletRequest request);
}
