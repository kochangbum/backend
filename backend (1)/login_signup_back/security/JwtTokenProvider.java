package com.example.login_signup_back.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;

@Component
public class JwtTokenProvider {

	private Key secretKey;
	private final long validityInMilliseconds = 3600000; // 1시간

	// secretKey를 문자열로 주입받고, Base64로 디코딩하여 Key 객체로 변환
	@Value("${jwt.secret}")
	public void setSecretKey(String secretKey) {
		byte[] keyBytes = Base64.getDecoder().decode(secretKey);  // Base64 디코딩
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);  // HMAC-SHA 알고리즘용 키 생성
	}

	// JWT 토큰 생성
	public String createToken(String userUniqueNumber, String role, String userFavoriteTeam) {
		Claims claims = Jwts.claims().setSubject(userUniqueNumber);
		claims.put("role", role);		//role 정보 추가(관리자 테이블 오류방지용)
		
		// admin인 경우 선호구단을 null로 설정
		if ("admin".equals(role)) {
			userFavoriteTeam = null;  // admin의 경우 userFavoriteTeam을 null로 설정
		} else {
			claims.put("favoriteTeam", userFavoriteTeam);  // 일반 유저의 경우 좋아하는 팀 정보 추가
		}
		
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);
		System.out.println("JwtTokenProvider : createToken 토큰생성");
		System.out.println("토큰안에 선호구단 추가!!!!" + userFavoriteTeam);
		return Jwts.builder()
					.setClaims(claims) // 데이터
					.setIssuedAt(now) // 토큰 발행일자
					.setExpiration(validity) // 토큰 만료일자
					.signWith(secretKey) // 서명
					.compact();
	}

	// JWT 토큰에서 인증 정보 조회
	public Authentication getAuthentication(String token) {
		String userUniqueNumber = getUserUniqueNumber(token);
		UserDetails userDetails = new User(userUniqueNumber, "", Collections.emptyList());
		System.out.println("JwtTokenProvider : getAuthentication 토큰인증정보조회");
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	// 토큰에서 회원 정보 추출
	public String getUserUniqueNumber(String token) {
		System.out.println("JwtTokenProvider : getUserUniqueNumber 토큰에서 유저유니크넘버추출");
		return Jwts.parserBuilder().setSigningKey(secretKey).build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
	}

	public String resolveToken(HttpServletRequest request) {
		// 요청 메서드 (GET, POST 등)
		System.out.println("요청 메서드: " + request.getMethod());
		// 요청 URI (예: /api/auth/user)
		System.out.println("요청 URI: " + request.getRequestURI());
		// 전체 URL (예: http://localhost:8090/api/auth/user)
		System.out.println("전체 URL: " + request.getRequestURL());
		// 쿼리 파라미터 (예: ?token=123)
		System.out.println("쿼리 문자열: " + request.getQueryString());
		// 쿠키에서 토큰 추출
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("jwtToken".equals(cookie.getName())) {
					System.out.println("JwtTokenProvider : resolveToken 쿠키에서 토큰 추출: 성공");
					return cookie.getValue();  // 쿠키에서 추출한 토큰 반환
				}
			}
		}

		// 토큰이 없을 경우 null 반환
		System.out.println("JwtTokenProvider : Authorization 헤더, 쿼리 파라미터, 쿠키에서 토큰 없음");
		return null;
	}

	// 토큰의 유효성 및 만료일자 확인
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(secretKey).build()
			.parseClaimsJws(token);
			System.out.println("JwtTokenProvider : validateToken 토큰유효성검사");
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			// 유효하지 않은 토큰
			return false;
		}
	}

	public Authentication resolveAndAuthenticateToken(HttpServletRequest request) {
	    // 1. 요청에서 토큰 추출
	    String token = resolveToken(request);
  
	    // 2. 토큰이 존재하지 않거나 유효하지 않은 경우 처리
	    if (token == null || !validateToken(token)) {
	        System.out.println("유효하지 않은 토큰입니다.");
	        return null;  // 유효하지 않으면 null 반환 또는 예외 발생 처리 가능
	    }

	    // 3. 유효한 토큰에서 인증 정보 추출
	    Authentication authentication = getAuthentication(token);

	    System.out.println("JwtTokenProvider : 토큰 인증 및 정보 추출 완료");
	    System.out.println(authentication);
	    return authentication;
	}

	
	// JWT 토큰에서 사용자 이메일 추출
	public String getEmailFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
					.setSigningKey(secretKey)  // 서명 검증에 사용할 키 설정
					.build()
					.parseClaimsJws(token)
					.getBody();

		return claims.getSubject();  // 'sub' 클레임에 저장된 사용자 이메일 반환
	}
}