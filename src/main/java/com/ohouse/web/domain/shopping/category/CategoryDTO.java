package com.ohouse.web.domain.shopping.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CategoryDTO {
	// 칼럼들
    private int category_id;			// 카테고리 식별번호
    private String category_name;	// 카테고리 이름
    private Integer parentId;			// 누구에 속한 카테고리인지 확인
    private int sortOrder;		// 부모 아래 표시 순서
    
}
