package com.ohouse.web.controller.seller;

import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ohouse.web.domain.auth.SellerAuthDTO;
import com.ohouse.web.domain.seller.ProductDTO;
import com.ohouse.web.domain.seller.ProductFormDTO;
import com.ohouse.web.domain.seller.ProductOptionDTO;
import com.ohouse.web.domain.seller.SellerOrderDTO;
import com.ohouse.web.domain.shopping.category.CategoryDTO;
import com.ohouse.web.service.seller.SellerOrderService;
import com.ohouse.web.service.seller.SellerService;
import com.ohouse.web.service.shopping.category.CategoryService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Controller
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private SellerOrderService orderService;

    @Autowired
    private CategoryService categoryService;

    private static final String R2_ENDPOINT = "https://c118a7efdddd35d3edac1db3a63ed76d.r2.cloudflarestorage.com";
    private static final String R2_ACCESS_KEY = "8f8a91958a3c06d4ce11ba80f5d60e2f";
    private static final String R2_SECRET_KEY = "5ab97a22e5baa3fe630165a9e770f0eada870d8672aaea80d3258ccbc2440667";
    private static final String R2_BUCKET = "productimage";
    private static final String R2_PUBLIC_URL = "https://pub-3490b121289f419194b634a98c9d4ba5.r2.dev";

    @GetMapping("/addForm.htm")
    public String addForm(Model model) throws Exception {
        List<CategoryDTO> categoryList = categoryService.getAllLeafCategories();
        model.addAttribute("categoryList", categoryList);
        return "seller/seller_add";
    }

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
            model.addAttribute("categoryList", categoryService.getAllLeafCategories());
            return "seller/seller_add";
        }
    }

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

    @GetMapping("/editForm.htm")
    public String editForm(@RequestParam("productId") int productId, Model model) throws Exception {
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

    @GetMapping("/dashboard.htm")
    public String dashboard(@AuthenticationPrincipal SellerAuthDTO sellerAuth, Model model) {
        if (sellerAuth == null || sellerAuth.getBrandName() == null || sellerAuth.getBrandName().trim().isEmpty()) {
            return "redirect:/seller/login.htm";
        }

        String myBrandName = sellerAuth.getBrandName();
        
        Map<String, Object> stats = new HashMap<>();
        Map<String, Integer> productStats = sellerService.getDashboardStats(myBrandName);
        if (productStats != null) stats.putAll(productStats);

        Map<String, Object> orderStats = orderService.getDashboardOrderStats(myBrandName);
        if (orderStats != null) stats.putAll(orderStats);

        model.addAttribute("stats", stats);
        return "seller/dashboard";
    }

    @GetMapping("/productList.htm")
    public String productList(@AuthenticationPrincipal SellerAuthDTO sellerAuth, Model model) {
        if (sellerAuth == null || sellerAuth.getBrandName() == null) {
            return "redirect:/seller/login.htm";
        }
        List<ProductDTO> productList = sellerService.getProductListByBrandName(sellerAuth.getBrandName());
        model.addAttribute("productList", productList);
        return "seller/seller_product_list";
    }

    @GetMapping("/orderList.htm")
    public String orderList(@AuthenticationPrincipal SellerAuthDTO sellerAuth, Model model) {
        if (sellerAuth == null || sellerAuth.getBrandName() == null) {
            return "redirect:/seller/login.htm";
        }
        List<SellerOrderDTO> orderList = orderService.getOrderList(sellerAuth.getBrandName());
        model.addAttribute("orderList", orderList);
        return "seller/order_list";
    }

    @GetMapping("/settlementList.htm")
    public String settlementList(@AuthenticationPrincipal SellerAuthDTO sellerAuth, Model model) {
        if (sellerAuth == null || sellerAuth.getBrandName() == null) {
            return "redirect:/seller/login.htm";
        }

        String brandName = sellerAuth.getBrandName();
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

    @GetMapping("/updateDelivery.htm")
    public String updateDelivery(@RequestParam("orderDetailId") int orderDetailId,
                                 @RequestParam("status") int status,
                                 @RequestParam(value = "from", required = false) String from,
                                 @AuthenticationPrincipal SellerAuthDTO sellerAuth, HttpServletResponse response) throws Exception {
        if (sellerAuth == null) {
            response.sendRedirect("/seller/login.htm");
            return null;
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

    @GetMapping("/claimList.htm")
    public String claimList(@AuthenticationPrincipal SellerAuthDTO sellerAuth, Model model) {
        if (sellerAuth == null || sellerAuth.getBrandName() == null) {
            return "redirect:/seller/login.htm";
        }
        List<SellerOrderDTO> claimList = orderService.getClaimList(sellerAuth.getBrandName());
        model.addAttribute("claimList", claimList);
        return "seller/claim_list";
    }
}