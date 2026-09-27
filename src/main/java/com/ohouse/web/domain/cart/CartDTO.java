package com.ohouse.web.domain.cart;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDTO {

    long cart_id;
    long member_id;
    long total_price;
}
