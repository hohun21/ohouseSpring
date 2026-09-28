package com.ohouse.web.domain.order;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OrderStatusCountDTO {
    private int paymentCount;
    private int preparingCount;
    private int shippingCount;
    private int deliveredCount;
    private int confirmedCount;

}
