package com.ohouse.web.domain.member;

import java.sql.Date;
import java.util.List;

import com.ohouse.web.domain.auth.AuthoritiesVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {

	private Integer memberId;
    private String id;
    private String password;
    private String name;
    private String rank;
    private Date regDate;
    private int status;
    //
    private List<AuthoritiesVO> authList;
}
