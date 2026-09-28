package com.ohouse.web.domain.order;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderOptionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private long option_group_id;
    private String option_group_name;
    private long option_value_id;
    private String option_value_name;
}
