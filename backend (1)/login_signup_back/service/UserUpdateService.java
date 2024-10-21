package com.example.login_signup_back.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.example.login_signup_back.model.User;
import com.example.login_signup_back.model.UserDTO;
import com.example.login_signup_back.security.JwtTokenProvider;
import com.example.mapper.Mappers;
import jakarta.servlet.http.HttpServletRequest;

//# 작성자 : 나기표
//# 작성일 : 2024-10-08
//# 목  적 : 사용자 정보 수정, 비밀번호 변경, 사용자 정보 조회 등 기능 제공
//# 기  능 : 사용자 정보 관리, 비밀번호 업데이트, 닉네임 중복 확인, 사용자 정보 조회 등 처리
@Service
public class UserUpdateService {
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;  // JWT 토큰 제공자
	
	@Autowired
	private Mappers mappers;
	
	private boolean previousPasswordSame = false;

	// # 작성자 : 나기표
	// # 작성일 : 2024-10-08
	// # 목  적 : 사용자 정보 업데이트
	// # 기  능 : 사용자의 닉네임, 생년월일, 성별, 좋아하는 팀 정보를 업데이트
	// # 매개변수 : userUniqueNumber - 사용자 고유번호,
	//					 userSocialLoginSep - 소셜 로그인 구분자,
	//					 userNickname - 닉네임,
	//					 userBirthDay - 생년월일,
	//					 userGender - 성별,
	//					 userFavoriteTeam - 좋아하는 팀
	// # 반환값 : 업데이트 성공 여부 (true/false)
	public boolean updateUserInfo(String userUniqueNumber, String userSocialLoginSep,
		String userNickname, String userBirthDay, int userGender, String userFavoriteTeam) {
		// User 객체에 필드를 업데이트
		User user = new User();
		user.setUserUniqueNumber(userUniqueNumber);  // 수정 불가능한 값
		user.setUserSocialLoginSep(userSocialLoginSep);  // 수정 불가능한 값
		user.setUserNickname(userNickname);
		user.setUserBirthDay(userBirthDay);
		user.setUserGender(userGender);
		user.setUserFavoriteTeam(userFavoriteTeam);
		user.setUserEditInformationDate(new Date());  // 수정하는 날짜로 업데이트

		int rowsAffected = mappers.updateUserInfo(user);
		
		return rowsAffected > 0;
	}

	// # 작성자 : 나기표
	// # 작성일 : 2024-10-08
	// # 목  적 : 닉네임 중복 확인
	// # 기  능 : 사용자가 입력한 닉네임이 이미 존재하는지 확인
	// # 매개변수 : userNickname - 중복 확인할 닉네임
	// # 반환값 : 중복 여부 (true/false)
	public boolean isNicknameDuplicate(String userNickname) {
		List<String> nicknames = mappers.getAllUserNicknames();
		
		return nicknames.contains(userNickname);
	}

	// # 작성자 : 나기표
	// # 작성일 : 2024-10-08
	// # 목  적 : 비밀번호 변경
	// # 기  능 : 현재 비밀번호 확인 후 새로운 비밀번호로 업데이트
	// # 매개변수 : currentPassword - 현재 비밀번호
	//					 newPassword - 새 비밀번호
	//					 request - HttpServletRequest 객체
	// # 반환값 : 비밀번호 변경 성공 여부 (true/false)
	public boolean updatePassword(String currentPassword, String newPassword, HttpServletRequest request) {
		// JWT 토큰 토큰추출, 유효성검사 통합메서드
		Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);

		// JWT 토큰에서 userUniqueNumber 추출
		String userUniqueNumber = authentication.getName();
		System.out.println("##########################################################################################################");
		System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("##########################################################################################################");
		System.out.println(userUniqueNumber);
		
		
		// 기존 사용자 정보 조회 (user_unique_number로 조회)
		User existingUser = mappers.getUserPassword(userUniqueNumber);

		// 현재 비밀번호가 일치하는지 확인
		if (existingUser == null || !currentPassword.equals(existingUser.getUserPassword())) {
			System.out.println("현재 비밀번호가 일치하지 않거나 사용자를 찾을 수 없습니다.");
			return false;  // 현재 비밀번호가 일치하지 않으면 실패 처리
		}

		// 새 비밀번호가 기존 비밀번호와 동일한지 확인
		if (newPassword.equals(existingUser.getUserPassword()) ||
			(existingUser.getUserBeforePassword() != null && newPassword.equals(existingUser.getUserBeforePassword()))) {
			System.out.println("##########################################################################################################");
			System.out.println("새 비밀번호가 기존 비밀번호와 중복됩니다.");
			previousPasswordSame = true; 
			return false;  // 새 비밀번호가 기존 또는 이전 비밀번호와 중복되면 실패 처리
		}

		// 비밀번호 업데이트: 기존 비밀번호를 이전 비밀번호로 설정
		existingUser.setUserBeforePassword(existingUser.getUserPassword());
		existingUser.setUserPassword(newPassword);  // 새 비밀번호로 업데이트
		existingUser.setUserUniqueNumber(userUniqueNumber);  // userUniqueNumber 명시적으로 설정
		existingUser.setUserEditInformationDate(new Date());  // 수정 날짜 업데이트

		// 업데이트된 비밀번호를 DB에 저장
		System.out.println("##########################################################################################################");
		System.out.println("비밀번호 업데이트 쿼리 실행 - userUniqueNumber: " + existingUser.getUserUniqueNumber());
		int rowsAffected = mappers.updateUserPassword(existingUser);
		
		return rowsAffected > 0;  // 비밀번호 업데이트 성공 여부 반환
	}
	
//# 작성자 : 나기표
	// # 작성일 : 2024-10-09
	// # 목  적 : 이전 비밀번호와 동일한지 여부 확인
	// # 기  능 : 이전 비밀번호와 동일한지 여부 확인
	public boolean isPreviousPasswordSame() {
		return previousPasswordSame;
	}

	// # 작성자 : 나기표
	// # 작성일 : 2024-10-08
	// # 목  적 : 사용자 정보 조회
	// # 기  능 : JWT 토큰에서 추출한 사용자 고유번호로 사용자 정보 조회
	// # 매개변수 : request - HttpServletRequest 객체
	// # 반환값 : 조회된 사용자 정보 (UserDTO)
	public UserDTO getUserInfo(HttpServletRequest request) {
		// JWT 토큰 토큰추출, 유효성검사 통합메서드
		Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);

		// JWT 토큰에서 userUniqueNumber 추출
		String userUniqueNumber = authentication.getName();
		System.out.println("##########################################################################################################");
		System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("##########################################################################################################");
		System.out.println(userUniqueNumber);
		
		User user = mappers.findByUniqueId(userUniqueNumber);
		if (user == null) {
			return null;
		}
		return convertToDTO(user);  // User 객체를 DTO로 변환하여 반환
	}
	
	// # 작성자 : 나기표
	// # 작성일 : 2024-10-08
	// # 목  적 : User 객체를 UserDTO로 변환
	// # 기  능 : User 객체를 DTO로 변환하여 반환
	// # 매개변수 : user - 변환할 User 객체
	// # 반환값 : 변환된 UserDTO 객체
	private UserDTO convertToDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setUserUniqueNumber(user.getUserUniqueNumber());
		userDTO.setUserId(user.getUserId());
		userDTO.setUserName(user.getUserName());
		userDTO.setUserNickname(user.getUserNickname());
		userDTO.setUserEmail(user.getUserEmail());
		userDTO.setUserBirthDay(user.getUserBirthDay());
		userDTO.setUserGender(user.getUserGender());
		userDTO.setUserFavoriteTeam(user.getUserFavoriteTeam());
		userDTO.setUserSocialLoginSep(user.getUserSocialLoginSep());
		
		return userDTO;
	}
}
