package com.ohouse.web.domain.seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionGroupDTO {
    private Integer optionGroupId;
    private Integer productId;
    private String groupName;
    private Integer sortOrder;
    private Integer required;
}