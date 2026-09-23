/*
package com.ohouse.web.controller.seller;

import java.io.PrintWriter;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.plaf.synth.Region;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ohouse.member.service.DuplicateEmailException;
import com.ohouse.seller.service.SellerLoginFailException;
import com.ohouse.seller.service.SellerLoginService;
import com.ohouse.seller.service.SellerNotActiveException;
import com.ohouse.seller.service.SellerSignupRequest;
import com.ohouse.seller.service.SellerSignupService;
import com.ohouse.seller.service.SellerSignupStatusService;
import com.ohouse.shopping.category.dao.CategoryDAOImple;
import com.ohouse.shopping.category.dto.CategoryDTO;
import com.ohouse.shopping.category.service.CategoryService;
import com.ohouse.web.domain.seller.ProductDTO;
import com.ohouse.web.domain.seller.ProductFormDTO;
import com.ohouse.web.domain.seller.ProductOptionDTO;
import com.ohouse.web.domain.seller.SellerAuthDTO;
import com.ohouse.web.domain.seller.SellerOrderDTO;
import com.ohouse.web.service.seller.SellerOrderService;
import com.ohouse.web.service.seller.SellerService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Controller
@RequestMapping("/seller")
public class SellerController {

    private final SellerSignupService sellerSignupService = new SellerSignupService();
    private final SellerService sellerService = new SellerService();
    private final SellerOrderService orderService = new SellerOrderService();
    private final SellerLoginService loginService = new SellerLoginService();
    private final SellerSignupStatusService statusService = new SellerSignupStatusService();

    // R2 연동 상수
    private static final String R2_ENDPOINT = "https://c118a7efdddd35d3edac1db3a63ed76d.r2.cloudflarestorage.com";
    private static final String R2_ACCESS_KEY = "8f8a91958a3c06d4ce11ba80f5d60e2f";
    private static final String R2_SECRET_KEY = "5ab97a22e5baa3fe630165a9e770f0eada870d8672aaea80d3258ccbc2440667";
    private static final String R2_BUCKET = "productimage";
    private static final String R2_PUBLIC_URL = "https://pub-3490b121289f419194b634a98c9d4ba5.r2.dev";

    // 1. 브랜드명 중복 체크 (AJAX)
    @GetMapping("/brandNameCheck.htm")
    @ResponseBody
    public String brandNameCheck(@RequestParam(value = "brandName", required = false) String brandName, HttpServletResponse res) throws Exception {
        String trimmed = trim(brandName);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        if (trimmed == null || trimmed.isBlank()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "{\"count\":0,\"code\":\"BRAND_NAME_REQUIRED\"}";
        }
        try {
            return sellerSignupService.brandNameCheck(trimmed);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{\"count\":0,\"code\":\"SERVER_ERROR\"}";
        }
    }

    // 2. 사업자번호 중복 체크 (AJAX)
    @GetMapping("/businessNumberCheck.htm")
    @ResponseBody
    public String businessNumberCheck(@RequestParam(value = "businessNumber", required = false) String businessNumber, HttpServletResponse res) throws Exception {
        String trimmed = trim(businessNumber);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        if (trimmed == null || !trimmed.matches("\\d{10}")) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "{\"count\":0,\"code\":\"INVALID_BUSINESS_NUMBER\"}";
        }
        try {
            return sellerSignupService.buisinessNumberCheck(trimmed);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{\"count\":0,\"code\":\"SERVER_ERROR\"}";
        }
    }

    // 3. 이메일 중복 체크 (AJAX)
    @GetMapping("/emailCheck.htm")
    @ResponseBody
    public String emailCheck(@RequestParam(value = "email", required = false) String email, HttpServletResponse res) throws Exception {
        String trimmed = trim(email);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        if (trimmed == null || trimmed.isBlank()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "{\"count\":0,\"code\":\"EMAIL_REQUIRED\"}";
        }
        try {
            return sellerSignupService.emailCheck(trimmed);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{\"count\":0,\"code\":\"SERVER_ERROR\"}";
        }
    }

    // 4. 통신판매업번호 체크 (AJAX)
    @GetMapping("/mailOrderNumberCheck.htm")
    @ResponseBody
    public String mailOrderNumberCheck(@RequestParam(value = "mailOrderNumber", required = false) String mailOrderNumber, HttpServletResponse res) throws Exception {
        String trimmed = trim(mailOrderNumber);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        if (trimmed == null || trimmed.isBlank()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "{\"count\":0,\"code\":\"MAIL_ORDER_NUMBER_REQUIRED\"}";
        }
        try {
            return sellerSignupService.mailOrderNumberCheck(trimmed);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{\"count\":0,\"code\":\"SERVER_ERROR\"}";
        }
    }

    // 5. 상품 등록 폼
    @GetMapping("/addForm.htm")
    public String addForm(Model model) throws Exception {
        CategoryService categoryService = new CategoryService(new CategoryDAOImple());
        List<CategoryDTO> categoryList = categoryService.getAllLeafCategories();
        model.addAttribute("categoryList", categoryList);
        return "seller/seller_add";
    }

    // 6. 상품 등록 처리 (R2 이미지 업로드 포함)
    @PostMapping("/addPro.htm")
    public String addPro(MultipartHttpServletRequest request, Model model) throws Exception {
        request.setCharacterEncoding("UTF-8");

        List<String> imageUrls = new ArrayList<>();
        List<String> imageTypes = new ArrayList<>();
        List<Integer> sortOrders = new ArrayList<>();

        S3Configuration s3Configuration = S3Configuration.builder().chunkedEncodingEnabled(false).build();
        try (S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create(R2_ENDPOINT))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(R2_ACCESS_KEY, R2_SECRET_KEY)))
                .serviceConfiguration(s3Configuration)
                .build()) {

            int sortOrder = 1;
            List<MultipartFile> imageFiles = request.getFiles("productImages");

            for (MultipartFile file : imageFiles) {
                if (file != null && !file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String savedFileName = UUID.randomUUID() + "_" + originalFileName;
                    String objectKey = "products/" + savedFileName;

                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(R2_BUCKET)
                            .key(objectKey)
                            .contentType(file.getContentType())
                            .build();

                    s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

                    String imageUrl = R2_PUBLIC_URL + "/" + objectKey;
                    String imageType = (sortOrder == 1) ? "THUMBNAIL" : "DETAIL";

                    imageUrls.add(imageUrl);
                    imageTypes.add(imageType);
                    sortOrders.add(sortOrder);
                    sortOrder++;
                }
            }
        }

        ProductFormDTO formDTO = ProductFormDTO.builder()
                .categoryId(Integer.parseInt(request.getParameter("categoryId")))
                .brandName(request.getParameter("brandName"))
                .productName(request.getParameter("productName"))
                .description(request.getParameter("description"))
                .originalPrice(Integer.parseInt(request.getParameter("originalPrice")))
                .discountRate(Integer.parseInt(request.getParameter("discountRate")))
                .price(Integer.parseInt(request.getParameter("price")))
                .optionNames(request.getParameterValues("optionNames"))
                .optionValues(request.getParameterValues("optionValues"))
                .skuNames(request.getParameterValues("skuNames"))
                .skuPrices(request.getParameterValues("skuPrices"))
                .skuStocks(request.getParameterValues("skuStocks"))
                .extraNames(request.getParameterValues("extraNames"))
                .extraPrices(request.getParameterValues("extraPrices"))
                .extraStocks(request.getParameterValues("extraStocks"))
                .imageUrls(imageUrls)
                .imageTypes(imageTypes)
                .sortOrders(sortOrders)
                .build();

        int productId = sellerService.registerProduct(formDTO);

        if (productId > 0) {
            return "redirect:/product/productDetail.htm?product_id=" + productId;
        } else {
            model.addAttribute("errorMessage", "상품 등록에 실패했습니다.");
            CategoryService categoryService = new CategoryService(new CategoryDAOImple());
            model.addAttribute("categoryList", categoryService.getAllLeafCategories());
            return "seller/seller_add";
        }
    }

    // 7. 상품 삭제
    @GetMapping("/deletePro.htm")
    public String deletePro(@RequestParam("productId") int productId, HttpServletResponse response) throws Exception {
        boolean result = sellerService.deleteProduct(productId);
        if (result) {
            return "redirect:/seller/productList.htm";
        } else {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.print("<script>alert('삭제 실패!'); history.back();</script>");
            out.flush();
            return null;
        }
    }

    // 8. 상품 수정 폼
    @GetMapping("/editForm.htm")
    public String editForm(@RequestParam("productId") int productId, Model model) throws Exception {
        CategoryService categoryService = new CategoryService(new CategoryDAOImple());

        ProductDTO product = sellerService.getProductById(productId);
        List<Map<String, String>> optionItems = sellerService.getOptionItemsForEdit(productId);
        List<ProductOptionDTO> allOptions = sellerService.getOptionsByProductId(productId);
        
        List<ProductOptionDTO> skuList = new ArrayList<>();
        List<ProductOptionDTO> extraList = new ArrayList<>();

        if (allOptions != null) {
            for (ProductOptionDTO opt : allOptions) {
                if (opt.getSku() != null && opt.getSku().startsWith("[추가상품]")) {
                    extraList.add(opt);
                } else {
                    skuList.add(opt);
                }
            }
        }

        List<CategoryDTO> categoryList = categoryService.getAllLeafCategories();

        model.addAttribute("product", product);
        model.addAttribute("optionItems", optionItems);
        model.addAttribute("skuList", skuList);
        model.addAttribute("extraList", extraList);
        model.addAttribute("categoryList", categoryList);

        return "seller/seller_edit_form";
    }

    // 9. 상품 수정 처리
    @PostMapping("/editPro.htm")
    public String editPro(MultipartHttpServletRequest request) throws Exception {
        request.setCharacterEncoding("UTF-8");

        List<String> imageUrls = new ArrayList<>();
        List<String> imageTypes = new ArrayList<>();
        List<Integer> sortOrders = new ArrayList<>();

        S3Configuration s3Configuration = S3Configuration.builder().chunkedEncodingEnabled(false).build();
        try (S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create(R2_ENDPOINT))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(R2_ACCESS_KEY, R2_SECRET_KEY)))
                .serviceConfiguration(s3Configuration)
                .build()) {

            int sortOrder = 1;
            List<MultipartFile> imageFiles = request.getFiles("productImages");

            for (MultipartFile file : imageFiles) {
                if (file != null && !file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                    String objectKey = "products/" + savedFileName;

                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(R2_BUCKET)
                            .key(objectKey)
                            .contentType(file.getContentType())
                            .build();

                    s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

                    String imageUrl = R2_PUBLIC_URL + "/" + objectKey;
                    String imageType = (sortOrder == 1) ? "THUMBNAIL" : "DETAIL";

                    imageUrls.add(imageUrl);
                    imageTypes.add(imageType);
                    sortOrders.add(sortOrder);
                    sortOrder++;
                }
            }
        }

        ProductFormDTO formDTO = ProductFormDTO.builder()
                .productId(Integer.parseInt(request.getParameter("productId")))
                .categoryId(Integer.parseInt(request.getParameter("categoryId")))
                .brandName(request.getParameter("brandName"))
                .productName(request.getParameter("productName"))
                .description(request.getParameter("description"))
                .originalPrice(Integer.parseInt(request.getParameter("originalPrice")))
                .discountRate(Integer.parseInt(request.getParameter("discountRate")))
                .price(Integer.parseInt(request.getParameter("price")))
                .optionNames(request.getParameterValues("optionNames"))
                .optionValues(request.getParameterValues("optionValues"))
                .skuNames(request.getParameterValues("skuNames"))
                .skuPrices(request.getParameterValues("skuPrices"))
                .skuStocks(request.getParameterValues("skuStocks"))
                .extraNames(request.getParameterValues("extraNames"))
                .extraPrices(request.getParameterValues("extraPrices"))
                .extraStocks(request.getParameterValues("extraStocks"))
                .imageUrls(imageUrls)
                .imageTypes(imageTypes)
                .sortOrders(sortOrders)
                .build();

        boolean isSuccess = sellerService.updateProduct(formDTO);

        if (isSuccess) {
            return "redirect:/product/productDetail.htm?product_id=" + formDTO.getProductId();
        } else {
            return "redirect:/seller/editForm.htm?productId=" + formDTO.getProductId() + "&error=1";
        }
    }

    // 10. 판매자 대시보드
    @GetMapping("/dashboard.htm")
    public String dashboard(HttpSession session, Model model) {
        SellerAuthDTO sellerAuth = (SellerAuthDTO) session.getAttribute("sellerAuth");
        if (sellerAuth == null) {
            return "redirect:/seller/login.htm";
        }

        String myBrandName = sellerAuth.getBrandName();
        Map<String, Object> stats = new HashMap<>();
        Map<String, Integer> productStats = sellerService.getDashboardStats(myBrandName);
        if (productStats != null) {
            stats.putAll(productStats);
        }

        Map<String, Object> orderStats = orderService.getDashboardOrderStats(myBrandName);
        if (orderStats != null) {
            stats.putAll(orderStats);
        }

        model.addAttribute("stats", stats);
        return "seller/dashboard";
    }

    // 11. 판매자 상품 목록
    @GetMapping("/productList.htm")
    public String productList(HttpSession session, Model model) {
        SellerAuthDTO sellerAuth = (SellerAuthDTO) session.getAttribute("sellerAuth");
        if (sellerAuth == null) {
            return "redirect:/seller/login.htm";
        }

        List<ProductDTO> productList = sellerService.getProductListByBrandName(sellerAuth.getBrandName());
        model.addAttribute("productList", productList);
        return "seller/seller_product_list";
    }

    // 12. 판매자 회원가입 (GET/POST)
    @GetMapping("/signup.htm")
    public String signupForm() {
        return "seller/sellerSignup";
    }

    @PostMapping("/signup.htm")
    public String signupSubmit(SellerSignupRequest sellerSignupRequest, HttpServletRequest req, Model model) {
        Map<String, Boolean> errors = new HashMap<>();
        model.addAttribute("errors", errors);

        sellerSignupRequest.validate(errors);
        if (!errors.isEmpty()) {
            return "seller/sellerSignup";
        }

        try {
            sellerSignupService.signup(sellerSignupRequest);
            return "seller/sellerSignupStatus";
        } catch (DuplicateEmailException e) {
            errors.put("duplicateEmail", Boolean.TRUE);
            return "seller/sellerSignup";
        }
    }

    // 13. 판매자 로그인 (GET/POST)
    @GetMapping("/login.htm")
    public String loginForm(HttpSession session) {
        if (session != null && session.getAttribute("sellerAuth") != null) {
            return "redirect:/main.htm";
        }
        return "seller/sellerLogin";
    }

    @PostMapping("/login.htm")
    public String loginSubmit(@RequestParam("email") String emailParam,
                              @RequestParam("password") String password,
                              @RequestParam("businessNumber") String businessNumberParam,
                              HttpServletRequest req, Model model) throws Exception {

        String email = trim(emailParam);
        String businessNumber = normalizeBusinessNumber(businessNumberParam);

        Map<String, Boolean> errors = new HashMap<>();
        model.addAttribute("errors", errors);
        model.addAttribute("email", email);
        model.addAttribute("businessNumber", businessNumber);

        if (email == null || email.isEmpty()) errors.put("email", Boolean.TRUE);
        if (password == null || password.isEmpty()) errors.put("password", Boolean.TRUE);
        if (businessNumber == null || !businessNumber.matches("\\d{10}")) errors.put("businessNumber", Boolean.TRUE);

        if (!errors.isEmpty()) {
            return "seller/sellerLogin";
        }

        try {
            SellerAuthDTO sellerAuth = loginService.login(email, password, businessNumber);

            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) oldSession.invalidate();

            HttpSession session = req.getSession(true);
            session.setAttribute("sellerAuth", sellerAuth);

            return "redirect:/main.htm";

        } catch (SellerNotActiveException e) {
            errors.put("notActive", Boolean.TRUE);
            return "seller/sellerLogin";
        } catch (SellerLoginFailException e) {
            errors.put("emailOrPwNotMatch", Boolean.TRUE);
            return "seller/sellerLogin";
        }
    }

    // 14. 판매자 입점 상태 조회 페이지 (GET) 및 비동기 처리 (POST)
    @GetMapping("/sellerSignupStatus.htm")
    public String sellerSignupStatusForm() {
        return "seller/sellerSignupStatus";
    }

    @PostMapping("/sellerSignupStatus.htm")
    @ResponseBody
    public void sellerSignupStatusSubmit(@RequestParam(value = "email", required = false) String emailParam,
                                         @RequestParam(value = "password", required = false) String password,
                                         @RequestParam(value = "businessNumber", required = false) String businessNumberParam,
                                         HttpServletResponse res) {
        res.setContentType("application/json; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");

        try {
            String email = emailParam != null ? emailParam.trim() : null;
            String businessNumber = normalizeBusinessNumber(businessNumberParam);

            if (email == null || email.isBlank() || password == null || password.isBlank() || businessNumber == null || !businessNumber.matches("\\d{10}")) {
                writeJson(res, false, null, "입력값을 다시 확인해 주세요.");
                return;
            }

            String status = statusService.checkSignupStatus(email, password, businessNumber);
            writeJson(res, true, status, "입점 상태 조회가 완료되었습니다.");

        } catch (SellerAuthenticationException e) {
            writeJson(res, false, null, "이메일, 비밀번호 또는 사업자등록번호가 일치하지 않습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(res, false, null, "입점 상태를 조회하는 중 서버 오류가 발생했습니다.");
        }
    }

    // 15. 판매자 상태 체크 (AJAX)
    @GetMapping("/sellerStatusCheck.htm")
    @ResponseBody
    public String sellerStatusCheck(@RequestParam(value = "email", required = false) String emailParam, HttpServletResponse res) {
        String email = trim(emailParam);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        if (email == null || email.isBlank()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return """
                    {
                        "success": false,
                        "status": null,
                        "code": "INVALID_EMAIL"
                    }
                    """;
        }

        try {
            String status = loginService.statusCheck(email);
            if (status == null) {
                return """
                        {
                            "success": true,
                            "status": null,
                            "code": "NOT_FOUND"
                        }
                        """;
            } else {
                return String.format("""
                        {
                            "success": true,
                            "status": "%s",
                            "code": "%s"
                        }
                        """, status, status);
            }
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return """
                    {
                        "success": false,
                        "status": null,
                        "code": "SERVER_ERROR"
                    }
                    """;
        }
    }

    // 16. 주문 목록 조회
    @GetMapping("/orderList.htm")
    public String orderList(HttpSession session, Model model) {
        SellerAuthDTO auth = (SellerAuthDTO) session.getAttribute("sellerAuth");
        if (auth == null) {
            return "redirect:/seller/login.htm";
        }

        List<SellerOrderDTO> orderList = orderService.getOrderList(auth.getBrandName());
        model.addAttribute("orderList", orderList);
        return "seller/order_list";
    }

    // 17. 정산 목록 조회
    @GetMapping("/settlementList.htm")
    public String settlementList(HttpSession session, Model model) {
        SellerAuthDTO auth = (SellerAuthDTO) session.getAttribute("sellerAuth");
        if (auth == null) {
            return "redirect:/seller/login.htm";
        }

        String brandName = auth.getBrandName();
        List<SellerOrderDTO> settlementList = orderService.getSettlementList(brandName);

        long totalSales = 0;
        for (SellerOrderDTO item : settlementList) {
            totalSales += (long) item.getPrice() * item.getQuantity();
        }
        long totalCommission = (long) (totalSales * 0.02);
        long finalSettlement = totalSales - totalCommission;

        model.addAttribute("settlementList", settlementList);
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalCommission", totalCommission);
        model.addAttribute("finalSettlement", finalSettlement);

        return "seller/settlement";
    }

    // 18. 배송 상태 업데이트
    @GetMapping("/updateDelivery.htm")
    public String updateDelivery(@RequestParam("orderDetailId") int orderDetailId,
                                 @RequestParam("status") int status,
                                 @RequestParam(value = "from", required = false) String from,
                                 HttpSession session, HttpServletResponse response) throws Exception {
        if (session == null || session.getAttribute("sellerAuth") == null) {
            return "redirect:/seller/login.htm";
        }

        boolean success = orderService.changeDeliveryStatus(orderDetailId, status);
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (success) {
            String redirectUrl = "/seller/orderList.htm";
            if ("claim".equals(from)) {
                redirectUrl = "/seller/claimList.htm";
            }
            out.print("<script>alert('처리가 완료되었습니다.'); location.href='" + redirectUrl + "';</script>");
        } else {
            out.print("<script>alert('처리에 실패했습니다.'); history.back();</script>");
        }
        out.flush();
        return null;
    }

    // 19. 클레임 목록 조회
    @GetMapping("/claimList.htm")
    public String claimList(HttpSession session, Model model) {
        SellerAuthDTO auth = (SellerAuthDTO) session.getAttribute("sellerAuth");
        if (auth == null) {
            return "redirect:/seller/login.htm";
        }

        List<SellerOrderDTO> claimList = orderService.getClaimList(auth.getBrandName());
        model.addAttribute("claimList", claimList);
        return "seller/claim_list";
    }

    // 보조 메서드들
    private String trim(String str) {
        return str == null ? null : str.trim();
    }

    private String normalizeBusinessNumber(String businessNumber) {
        if (businessNumber == null) return null;
        return businessNumber.replaceAll("[^0-9]", "");
    }

    private void writeJson(HttpServletResponse res, boolean success, String status, String message) {
        try {
            String statusJson = status == null ? "null" : "\"" + escapeJson(status) + "\"";
            String json = String.format("{\"success\":%b,\"status\":%s,\"message\":\"%s\"}", success, statusJson, escapeJson(message));
            PrintWriter out = res.getWriter();
            out.print(json);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
*/