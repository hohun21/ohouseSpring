package com.ohouse.web.controller.product;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohouse.web.domain.product.ProductDetailDTO;
import com.ohouse.web.domain.product.ProductOptionDTO;
import com.ohouse.web.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping(value="/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
   private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(value="/productDetail.htm")
    public String productDetail(Model model, @RequestParam("product_id") long product_id) throws SQLException {
        ProductDetailDTO pdto = productService.productDetail(product_id);
        model.addAttribute("pdto", pdto);
        return "product/product_detail";
    }
    @GetMapping("/productOption.htm")
    @ResponseBody
    public ResponseEntity<String> productOption(
            @RequestParam("product_id") long product_id,
            @RequestParam("option_value_ids") List<Long> option_value_ids
    ) throws SQLException, JsonProcessingException {

        ProductOptionDTO result =
                productService.productOption(product_id, option_value_ids);

        System.out.println("========== PRODUCT OPTION ==========");
        System.out.println("result = " + result);
        System.out.println("brand_name = " + result.getBrand_name());
        System.out.println("product_option_id = " + result.getProduct_option_id());
        System.out.println("===================================");


        String json = objectMapper.writeValueAsString(result);


        System.out.println("========== JSON BEFORE RETURN ==========");
        System.out.println(json);
        System.out.println("========================================");

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .body(json);
    }

}
