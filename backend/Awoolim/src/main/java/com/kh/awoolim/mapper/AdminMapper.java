package com.kh.awoolim.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.awoolim.domain.Admin;
import com.kh.awoolim.domain.Member;

@Mapper
public interface AdminMapper {
    List<Member> userList();
    
    public Admin findById(String email);
}
