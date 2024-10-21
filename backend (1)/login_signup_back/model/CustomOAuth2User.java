package com.example.login_signup_back.model;

import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User extends CustomUserDetails implements OAuth2User {
	private Map<String, Object> attributes;

	public CustomOAuth2User(User user, Map<String, Object> attributes) {
		super(user); // CustomUserDetails의 생성자 호출
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return getUsername(); // OAuth2User의 이름으로 이메일 반환
	}
}
