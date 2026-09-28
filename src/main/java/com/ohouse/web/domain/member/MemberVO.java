package com.ohouse.web.domain.member;

import com.ohouse.web.domain.auth.AuthoritiesVO;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
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
