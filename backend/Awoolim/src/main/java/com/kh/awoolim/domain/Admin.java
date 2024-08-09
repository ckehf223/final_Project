package com.kh.awoolim.domain;

import lombok.Data;

@Data
public class Admin extends Member{
	private int userId;
	private String userEmail;
	private String password;
	private String role;
}
