package com.ohouse.web.domain.order;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int cart_items_id;
    private long brand_id;
    private String brand_name;
    private String product_name;
    private String image_url;
    private long product_option_id;
    private long product_id;
    private String sku;
    private int price;
    private int quantity;

    private List<OrderOptionDTO> options;

}
