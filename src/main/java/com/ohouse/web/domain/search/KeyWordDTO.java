package com.ohouse.web.domain.search;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyWordDTO {
	private Integer keywordId;
	private String keyword;
	private Integer searchCount;
	private Integer currentRank;
	private Integer previousRank;
	private Date regDate;
	private Integer isNew;
}
