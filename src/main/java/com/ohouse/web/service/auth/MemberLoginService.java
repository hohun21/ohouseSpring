package com.ohouse.web.service.auth;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.springframework.stereotype.Service;

import com.ohouse.web.mapper.auth.MemberAuthMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

	private final MemberAuthMapper memberAuthMapper;
	
    public Integer statusCheck(String id) throws SQLException, NamingException {
        
    	 return memberAuthMapper.statusCheck(id);
    }
    
}