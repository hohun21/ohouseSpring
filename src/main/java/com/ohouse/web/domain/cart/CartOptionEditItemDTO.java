package com.ohouse.web.domain.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOptionEditItemDTO {
    private Integer cart_items_id;
    private long product_option_id;
    private int quantity;

    @JsonProperty("is_new")
    private boolean isNew;
}