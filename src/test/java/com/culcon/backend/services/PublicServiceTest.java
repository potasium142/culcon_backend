package com.culcon.backend.services;

import com.culcon.backend.dtos.ProductDTO;
import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.dtos.blog.BlogComment;
import com.culcon.backend.dtos.blog.BlogDetail;
import com.culcon.backend.dtos.blog.BlogItemInList;
import com.culcon.backend.models.*;
import com.culcon.backend.repositories.*;
import com.culcon.backend.services.authenticate.AuthService;
import com.culcon.backend.services.authenticate.JwtService;
import com.culcon.backend.services.authenticate.implement.AuthImplement;
import com.culcon.backend.services.implement.PublicImplement;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class PublicServiceTest {

    @Mock
    JwtService jwtService;
    @Mock
    AccountRepo accountRepo;
    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    private PublicImplement publicService;

    @Mock
    ProductRepo productRepo;
    @Mock
    BlogRepo blogDocRepo;
    @Mock
    PostCommentRepo postCommentRepo;
    @Mock
    AuthService authService;
    @Mock
    CouponRepo couponRepo;
    @Mock
    ProductDocRepo productDocRepo;


    //@Test
    void productService_fetchProduct_Success() {
        // Tạo mock cho các đối tượng cần thiết
        Product productInfo = Mockito.mock(Product.class);
        ProductDoc productDocs = Mockito.mock(ProductDoc.class);
        ProductDTO expectedProductDTO = Mockito.mock(ProductDTO.class);

        // Cài đặt hành vi cho các repository
        when(productRepo.findById("product123")).thenReturn(Optional.of(productInfo));
        when(productDocRepo.findById("product123")).thenReturn(Optional.of(productDocs));

        // Tạo danh sách Product giả định (tham số thứ 3)
        List<Product> productList = List.of(productInfo);

        // Mock phương thức tĩnh ProductDTO.from với 3 tham số
        try (MockedStatic<ProductDTO> mockedStatic = Mockito.mockStatic(ProductDTO.class)) {
            mockedStatic.when(() -> ProductDTO.from(productInfo, productDocs, productList))
                    .thenReturn(expectedProductDTO);

            // Gọi phương thức fetchProduct để kiểm tra kết quả
            ProductDTO result = publicService.fetchProduct("product123");

            // Xác minh các phương thức của repository được gọi đúng
            verify(productRepo).findById("product123");
            verify(productDocRepo).findById("product123");

            // So sánh kết quả trả về với mong đợi
            Assertions.assertEquals(expectedProductDTO, result);
        }
    }


    @Test
    void productService_fetchProduct_ProductNotFound() {
        when(productRepo.findById("product123")).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> publicService.fetchProduct("product123"));

        verify(productRepo).findById("product123");
        verifyNoInteractions(productDocRepo); // ProductDocRepo shouldn't be called if productRepo fails
    }

    @Test
    void productService_fetchProduct_ProductDocNotFound() {
        Product productInfo = Mockito.mock(Product.class);

        when(productRepo.findById("product123")).thenReturn(Optional.of(productInfo));
        when(productDocRepo.findById("product123")).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> publicService.fetchProduct("product123"));

        verify(productRepo).findById("product123");
        verify(productDocRepo).findById("product123");
    }

    @Test
    void publicService_fetchListOfProducts_Success() {
        List<Product> mockProducts = List.of(Mockito.mock(Product.class), Mockito.mock(Product.class));

        when(productRepo.findAll()).thenReturn(mockProducts);


        List<Product> result = publicService.fetchListOfProducts();

        verify(productRepo).findAll();
        Assertions.assertEquals(mockProducts, result);
    }
    @Test
    void publicService_fetchListOfProductsByCategory_Success() {
        ProductType category = ProductType.MEALKIT;
        List<Product> mockProducts = List.of(Mockito.mock(Product.class), Mockito.mock(Product.class));

        when(productRepo.findAllByProductTypes(category)).thenReturn(mockProducts);

        List<Product> result = publicService.fetchListOfProductsByCategory(category);

        verify(productRepo).findAllByProductTypes(category);
        Assertions.assertEquals(mockProducts, result);
    }

    @Test
    void publicService_searchProduct_ByKeywordOnly() {
        String keyword = "meat";
        List<Product> mockProducts = List.of(Mockito.mock(Product.class), Mockito.mock(Product.class));

        when(productRepo.findAllByProductNameContainingIgnoreCase(keyword)).thenReturn(mockProducts);

        List<Product> result = publicService.searchProduct(keyword, null);

        verify(productRepo).findAllByProductNameContainingIgnoreCase(keyword);
        Assertions.assertEquals(mockProducts, result);
    }

    @Test
    void publicService_searchProduct_ByKeywordAndType() {
        String keyword = "meat";
        ProductType type = ProductType.MEALKIT;
        List<Product> mockProducts = List.of(Mockito.mock(Product.class));

        when(productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes(keyword, type)).thenReturn(mockProducts);

        List<Product> result = publicService.searchProduct(keyword, type);

        verify(productRepo).findAllByProductNameContainingIgnoreCaseAndProductTypes(keyword, type);
        Assertions.assertEquals(mockProducts, result);
    }

    @Test
    void publicService_fetchListOfBlog_Success() {
        Blog blog1 = Mockito.mock(Blog.class);
        Blog blog2 = Mockito.mock(Blog.class);
        BlogItemInList blogItem1 = Mockito.mock(BlogItemInList.class);
        BlogItemInList blogItem2 = Mockito.mock(BlogItemInList.class);

        List<Blog> blogList = List.of(blog1, blog2);

        when(blogDocRepo.findAll()).thenReturn(blogList);

        try (MockedStatic<BlogItemInList> mockedStatic = Mockito.mockStatic(BlogItemInList.class)) {
            mockedStatic.when(() -> BlogItemInList.from(blog1)).thenReturn(blogItem1);
            mockedStatic.when(() -> BlogItemInList.from(blog2)).thenReturn(blogItem2);

            List<BlogItemInList> result = publicService.fetchListOfBlog();

            verify(blogDocRepo).findAll();
            Assertions.assertEquals(2, result.size());
            Assertions.assertTrue(result.contains(blogItem1));
            Assertions.assertTrue(result.contains(blogItem2));

            mockedStatic.verify(() -> BlogItemInList.from(blog1), times(1));
            mockedStatic.verify(() -> BlogItemInList.from(blog2), times(1));
        }
    }

    @Test
    void publicService_searchBlogByTitle_Success() {
        // Arrange
        String searchTitle = "Healthy";
        HashSet<String> tags = new HashSet<>();

        Blog blog1 = Mockito.mock(Blog.class);
        Blog blog2 = Mockito.mock(Blog.class);
        BlogItemInList blogItem1 = Mockito.mock(BlogItemInList.class);
        BlogItemInList blogItem2 = Mockito.mock(BlogItemInList.class);

        List<Blog> blogList = List.of(blog1, blog2);

        when(blogDocRepo.findAllByTitleContainingIgnoreCase(searchTitle)).thenReturn(blogList);

        try (MockedStatic<BlogItemInList> mockedStatic = Mockito.mockStatic(BlogItemInList.class)) {
            mockedStatic.when(() -> BlogItemInList.from(blog1)).thenReturn(blogItem1);
            mockedStatic.when(() -> BlogItemInList.from(blog2)).thenReturn(blogItem2);

            // Act
            List<BlogItemInList> result = publicService.searchBlogByTitle(searchTitle, tags);

            // Assert
            verify(blogDocRepo).findAllByTitleContainingIgnoreCase(searchTitle);
            Assertions.assertEquals(2, result.size());
            Assertions.assertTrue(result.contains(blogItem1));
            Assertions.assertTrue(result.contains(blogItem2));

            mockedStatic.verify(() -> BlogItemInList.from(blog1), times(1));
            mockedStatic.verify(() -> BlogItemInList.from(blog2), times(1));
        }
    }
    @Test
    void publicService_searchBlogByTitle_NoResults() {
        String searchTitle = "NonExisting";
        HashSet<String> tags = new HashSet<>();

        when(blogDocRepo.findAllByTitleContainingIgnoreCase(searchTitle)).thenReturn(Collections.emptyList());

        List<BlogItemInList> result = publicService.searchBlogByTitle(searchTitle, tags);

        verify(blogDocRepo).findAllByTitleContainingIgnoreCase(searchTitle);
        Assertions.assertTrue(result.isEmpty());
    }
    @Test
    void publicService_fetchBlogComment_Success() {
        String blogId = "blog123";

        PostComment comment1 = Mockito.mock(PostComment.class);
        PostComment comment2 = Mockito.mock(PostComment.class);
        BlogComment blogComment1 = Mockito.mock(BlogComment.class);
        BlogComment blogComment2 = Mockito.mock(BlogComment.class);

        List<PostComment> commentList = List.of(comment1, comment2);

        when(postCommentRepo.findAllByPostIdAndCommentType(blogId, CommentType.POST)).thenReturn(commentList);

        try (MockedStatic<BlogComment> mockedStatic = Mockito.mockStatic(BlogComment.class)) {
            mockedStatic.when(() -> BlogComment.from(comment1)).thenReturn(blogComment1);
            mockedStatic.when(() -> BlogComment.from(comment2)).thenReturn(blogComment2);

            List<BlogComment> result = publicService.fetchBlogComment(blogId);

            verify(postCommentRepo).findAllByPostIdAndCommentType(blogId, CommentType.POST);
            Assertions.assertEquals(2, result.size());
            Assertions.assertTrue(result.contains(blogComment1));
            Assertions.assertTrue(result.contains(blogComment2));

            mockedStatic.verify(() -> BlogComment.from(comment1), times(1));
            mockedStatic.verify(() -> BlogComment.from(comment2), times(1));
        }
    }
    @Test
    void publicService_fetchBlogComment_NoResults() {
        String blogId = "nonexistent_blog";

        when(postCommentRepo.findAllByPostIdAndCommentType(blogId, CommentType.POST)).thenReturn(Collections.emptyList());

        List<BlogComment> result = publicService.fetchBlogComment(blogId);

        verify(postCommentRepo).findAllByPostIdAndCommentType(blogId, CommentType.POST);
        Assertions.assertTrue(result.isEmpty());
    }
    @Test
    void publicService_fetchReply_Success() {
        String blogId = "blog123";
        String commentId = "comment456";

        PostComment reply1 = Mockito.mock(PostComment.class);
        PostComment reply2 = Mockito.mock(PostComment.class);
        BlogComment blogReply1 = Mockito.mock(BlogComment.class);
        BlogComment blogReply2 = Mockito.mock(BlogComment.class);

        List<PostComment> replyList = List.of(reply1, reply2);

        when(postCommentRepo.findAllByPostIdAndParentComment_Id(blogId, commentId)).thenReturn(replyList);

        try (MockedStatic<BlogComment> mockedStatic = Mockito.mockStatic(BlogComment.class)) {
            mockedStatic.when(() -> BlogComment.from(reply1)).thenReturn(blogReply1);
            mockedStatic.when(() -> BlogComment.from(reply2)).thenReturn(blogReply2);

            List<BlogComment> result = publicService.fetchReply(blogId, commentId);

            verify(postCommentRepo).findAllByPostIdAndParentComment_Id(blogId, commentId);
            Assertions.assertEquals(2, result.size());
            Assertions.assertTrue(result.contains(blogReply1));
            Assertions.assertTrue(result.contains(blogReply2));

            mockedStatic.verify(() -> BlogComment.from(reply1), times(1));
            mockedStatic.verify(() -> BlogComment.from(reply2), times(1));
        }
    }
    @Test
    void publicService_fetchReply_NoResults() {
        String blogId = "blog123";
        String commentId = "nonexistent_comment";

        when(postCommentRepo.findAllByPostIdAndParentComment_Id(blogId, commentId)).thenReturn(Collections.emptyList());

        List<BlogComment> result = publicService.fetchReply(blogId, commentId);

        verify(postCommentRepo).findAllByPostIdAndParentComment_Id(blogId, commentId);
        Assertions.assertTrue(result.isEmpty());
    }
    @Test
    void publicService_fetchBlogDetail_Success() {
        String blogId = "blog123";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Blog blog = Mockito.mock(Blog.class);
        Account account = Mockito.mock(Account.class);
        BlogDetail expectedBlogDetail = Mockito.mock(BlogDetail.class);

        when(authService.getUserInformation(request)).thenReturn(account);
        when(blogDocRepo.findById(blogId)).thenReturn(Optional.of(blog));
        when(account.getBookmarkedPost()).thenReturn(Set.of(blogId));

        BlogDetail result = publicService.fetchBlogDetail(blogId, request);

        verify(authService).getUserInformation(request);
        verify(blogDocRepo).findById(blogId);
        verify(account).getBookmarkedPost();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(blog, result.blog());
        Assertions.assertTrue(result.bookmark());
    }
    @Test
    void publicService_fetchBlogDetail_BlogNotFound() {
        String blogId = "nonexistent";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        when(blogDocRepo.findById(blogId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> publicService.fetchBlogDetail(blogId, request));

        verify(blogDocRepo).findById(blogId);
    }
    @Test
    void publicService_fetchBlogDetail_UserNotLoggedIn() {
        String blogId = "blog123";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Blog blog = Mockito.mock(Blog.class);

        when(authService.getUserInformation(request)).thenThrow(new RuntimeException());
        when(blogDocRepo.findById(blogId)).thenReturn(Optional.of(blog));

        BlogDetail result = publicService.fetchBlogDetail(blogId, request);

        verify(authService).getUserInformation(request);
        verify(blogDocRepo).findById(blogId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(blog, result.blog());
        Assertions.assertNull(result.bookmark());
    }
    @Test
    void publicService_fetchCoupon_Success() {
        String couponId = "coupon123";
        Coupon coupon = Mockito.mock(Coupon.class);

        when(couponRepo.findById(couponId)).thenReturn(Optional.of(coupon));
        when(coupon.getUsageLeft()).thenReturn(5);
        when(coupon.getExpireTime()).thenReturn(LocalDate.now().plusDays(1));

        Coupon result = publicService.fetchCoupon(couponId);

        verify(couponRepo).findById(couponId);
        verify(coupon).getUsageLeft();
        verify(coupon).getExpireTime();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(coupon, result);
    }
    @Test
    void publicService_fetchCoupon_NotFound() {
        String couponId = "invalid_coupon";

        when(couponRepo.findById(couponId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> publicService.fetchCoupon(couponId));

        verify(couponRepo).findById(couponId);
    }
    @Test
    void publicService_fetchCoupon_UsageLimitReached() {
        String couponId = "coupon123";
        Coupon coupon = Mockito.mock(Coupon.class);

        when(couponRepo.findById(couponId)).thenReturn(Optional.of(coupon));
        when(coupon.getUsageLeft()).thenReturn(0);

        Assertions.assertThrows(RuntimeException.class, () -> publicService.fetchCoupon(couponId));

        verify(couponRepo).findById(couponId);
        verify(coupon).getUsageLeft();
    }
    @Test
    void publicService_fetchCoupon_Expired() {
        String couponId = "coupon123";
        Coupon coupon = Mockito.mock(Coupon.class);

        when(couponRepo.findById(couponId)).thenReturn(Optional.of(coupon));
        when(coupon.getUsageLeft()).thenReturn(5);
        when(coupon.getExpireTime()).thenReturn(LocalDate.now().minusDays(1));

        Assertions.assertThrows(RuntimeException.class, () -> publicService.fetchCoupon(couponId));

        verify(couponRepo).findById(couponId);
        verify(coupon).getUsageLeft();
        verify(coupon).getExpireTime();
    }
    @Test
    void publicService_fetchAllValidCoupon_Success() {
        // Arrange
        Coupon validCoupon1 = Mockito.mock(Coupon.class);
        Coupon validCoupon2 = Mockito.mock(Coupon.class);
        Coupon expiredCoupon = Mockito.mock(Coupon.class);
        Coupon usedUpCoupon = Mockito.mock(Coupon.class);

        when(validCoupon1.getUsageLeft()).thenReturn(5);
        when(validCoupon1.getExpireTime()).thenReturn(LocalDate.now().plusDays(10));

        when(validCoupon2.getUsageLeft()).thenReturn(2);
        when(validCoupon2.getExpireTime()).thenReturn(LocalDate.now().plusDays(5));

        when(expiredCoupon.getUsageLeft()).thenReturn(3);
        when(expiredCoupon.getExpireTime()).thenReturn(LocalDate.now().minusDays(1));

        when(usedUpCoupon.getUsageLeft()).thenReturn(0);

        List<Coupon> allCoupons = List.of(validCoupon1, validCoupon2, expiredCoupon, usedUpCoupon);
        when(couponRepo.findAll()).thenReturn(allCoupons);

        List<Coupon> result = publicService.fetchAllValidCoupon();

        verify(couponRepo).findAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(validCoupon1));
        Assertions.assertTrue(result.contains(validCoupon2));
    }


    @Test
    void publicService_fetchAllValidCoupon_NoValidCoupons() {
        Coupon expiredCoupon = Mockito.mock(Coupon.class);
        Coupon usedUpCoupon = Mockito.mock(Coupon.class);

        when(expiredCoupon.getUsageLeft()).thenReturn(3);
        when(expiredCoupon.getExpireTime()).thenReturn(LocalDate.now().minusDays(1));

        when(usedUpCoupon.getUsageLeft()).thenReturn(0);


        List<Coupon> allCoupons = List.of(expiredCoupon, usedUpCoupon);

        when(couponRepo.findAll()).thenReturn(allCoupons);

        List<Coupon> result = publicService.fetchAllValidCoupon();

        verify(couponRepo).findAll();

        Assertions.assertTrue(result.isEmpty());
    }

}
