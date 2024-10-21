package com.example.login_signup_back.controller;

import com.example.login_signup_back.model.UserDTO;
import com.example.login_signup_back.service.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/update")
public class UserUpdateController {

	@Autowired
	private UserUpdateService userUpdateService;

	// # 작성자 : 나기표
	// # 작성일 : 2024-10-08
	// # 기  능 : 회원 정보 수정 메서드 (사용자 정보 수정 엔드포인트)
	// # 매개변수 : HttpServletRequest (현재 요청), UserDTO (사용자가 수정하고자 하는 정보)
	// # 반환값 : 회원 정보 수정 결과 메시지
	@PutMapping("/info")
	public ResponseEntity<?> updateUserInfo(HttpServletRequest request, @RequestBody UserDTO userInfo) {
		// 데이터베이스에서 현재 사용자 정보 조회
		UserDTO currentUser = userUpdateService.getUserInfo(request);
		if (currentUser == null) {
			System.out.println("##########################################################################################################");
			System.out.println("사용자를 찾을 수 없습니다.");
			System.out.println("##########################################################################################################");
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
		}
		
		// **수정해야 할 위치**: 닉네임이 변경된 경우에만 중복 체크 진행
		if (!currentUser.getUserNickname().equals(userInfo.getUserNickname())) {
			System.out.println("##########################################################################################################");
			System.out.println("닉네임이 변경되었습니다. 중복 여부를 확인합니다.");
			System.out.println("##########################################################################################################");
			
			// 닉네임이 변경된 경우 중복 체크 실행
			boolean isNicknameDuplicate = userUpdateService.isNicknameDuplicate(userInfo.getUserNickname());
			if (isNicknameDuplicate) {
				System.out.println("##########################################################################################################");
				System.out.println("중복된 닉네임입니다.");
				System.out.println("##########################################################################################################");
				
				return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 닉네임입니다.");
			}
		} else {
			System.out.println("##########################################################################################################");
			System.out.println("닉네임이 변경되지 않았습니다. 기존 닉네임을 유지합니다.");
			System.out.println("##########################################################################################################");
		}
		
		// 필드별로 수정사항이 없으면 기존 값을 유지하도록 처리
		String updatedNickname = userInfo.getUserNickname() != null && !userInfo.getUserNickname().isEmpty() ? userInfo.getUserNickname() : currentUser.getUserNickname();
		String updatedBirthDay = userInfo.getUserBirthDay() != null && !userInfo.getUserBirthDay().isEmpty() ? userInfo.getUserBirthDay() : currentUser.getUserBirthDay();
		int updatedGender = userInfo.getUserGender() != 0 ? userInfo.getUserGender() : currentUser.getUserGender();
		String updatedFavoriteTeam = userInfo.getUserFavoriteTeam() != null && !userInfo.getUserFavoriteTeam().isEmpty() ? userInfo.getUserFavoriteTeam() : currentUser.getUserFavoriteTeam();
		
		// **수정해야 할 위치**: 필드 업데이트 전 값 출력
		System.out.println("##########################################################################################################");
		System.out.println("업데이트 전 정보:");
		System.out.println("닉네임: " + currentUser.getUserNickname() + " -> " + updatedNickname);
		System.out.println("생년월일: " + currentUser.getUserBirthDay() + " -> " + updatedBirthDay);
		System.out.println("성별: " + currentUser.getUserGender() + " -> " + updatedGender);
		System.out.println("좋아하는 팀: " + currentUser.getUserFavoriteTeam() + " -> " + updatedFavoriteTeam);
		System.out.println("##########################################################################################################");
		
		// 사용자가 수정할 수 있는 필드만 업데이트
		boolean isUpdated = userUpdateService.updateUserInfo(
			currentUser.getUserUniqueNumber(),  // 수정 불가능한 값
			currentUser.getUserSocialLoginSep(),  // 수정 불가능한 값
			updatedNickname,
			updatedBirthDay,
			updatedGender,
			updatedFavoriteTeam
				);
		
		if (isUpdated) {
			System.out.println("##########################################################################################################");
			System.out.println("회원 정보가 성공적으로 업데이트되었습니다.");
			System.out.println("##########################################################################################################");
			return ResponseEntity.ok("회원 정보가 성공적으로 업데이트되었습니다.");
		} else {
			System.out.println("##########################################################################################################");
			System.out.println("회원 정보 업데이트에 실패했습니다.");
			System.out.println("##########################################################################################################");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원 정보 업데이트에 실패했습니다.");
		}
	}

	// # 작성자 : 나기표
	// # 작성일 : 2024-10-08
	// # 기  능 : 비밀번호 변경 메서드 (사용자 비밀번호 변경 엔드포인트)
	// # 매개변수 : HttpServletRequest (현재 요청), passwordData (현재 비밀번호와 새로운 비밀번호)
	// # 반환값 : 비밀번호 변경 결과 메시지
	@PutMapping("/password")
	public ResponseEntity<?> updatePassword(
													 @RequestBody Map<String, String> passwordData,
													 HttpServletRequest request) {
		String currentPassword = passwordData.get("currentPassword");
		String newPassword = passwordData.get("newPassword");
		System.out.println("사용자 비밀번호 변경 요청");

		// 비밀번호 업데이트 (user_unique_number 기준)
		System.out.println("##########################################################################################################");
		System.out.println("비밀번호 변경 요청 - 현재 비밀번호: " + currentPassword + ", 새로운 비밀번호: " + newPassword);
		System.out.println("##########################################################################################################");

		// 비밀번호 업데이트를 시도하고, 그에 대한 결과를 받음
		boolean isPasswordUpdated = userUpdateService.updatePassword(
			currentPassword, newPassword, request);

		if (!isPasswordUpdated) {
			// 비밀번호 업데이트 실패 시 적절한 메시지를 프론트엔드로 반환
			if (userUpdateService.isPreviousPasswordSame()) {
				// 이전 비밀번호와 동일한 경우
				System.out.println("##########################################################################################################");
				System.out.println("새로운 비밀번호가 이전 비밀번호와 동일합니다.");
				System.out.println("##########################################################################################################");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이전 비밀번호와 동일한 비밀번호입니다.");
			}
			System.out.println("##########################################################################################################");
			System.out.println("비밀번호 변경 실패");
			System.out.println("##########################################################################################################");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경에 실패했습니다.");
		}

		System.out.println("##########################################################################################################");
		System.out.println("비밀번호가 성공적으로 변경되었습니다.");
		System.out.println("##########################################################################################################");
		return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
	}

	// # 작성자 : 나기표
	// # 작성일 : 2024-10-08
	// # 기  능 : 사용자 정보 조회 메서드 (사용자 정보 조회 엔드포인트)
	// # 매개변수 : HttpServletRequest (현재 요청)
	// # 반환값 : 사용자 정보 또는 오류 메시지
	@GetMapping("/get-user")
	public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
		// userUniqueNumber로 사용자 정보 조회
		UserDTO userDTO = userUpdateService.getUserInfo(request);

		if (userDTO != null) {
			// 사용자 정보에 소셜 로그인 구분자를 포함하여 반환
			Map<String, Object> response = Map.of(
				"userId", Optional.ofNullable(userDTO.getUserId()).orElse("Unknown"),
				"userName", Optional.ofNullable(userDTO.getUserName()).orElse("Unknown"),
				"userEmail", Optional.ofNullable(userDTO.getUserEmail()).orElse("Unknown"),
				"userNickname", Optional.ofNullable(userDTO.getUserNickname()).orElse("Unknown"),
				"userBirthDay", Optional.ofNullable(userDTO.getUserBirthDay()).orElse("Unknown"),
				"userGender", Optional.ofNullable(userDTO.getUserGender()).orElse(0),
				"userFavoriteTeam", Optional.ofNullable(userDTO.getUserFavoriteTeam()).orElse("Unknown")
			);
			return ResponseEntity.ok(response);  // 사용자 정보 반환
		} else {
			System.out.println("##########################################################################################################");
			System.out.println("유효하지 않은 사용자 정보");
			System.out.println("##########################################################################################################");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
		}
	}

	// 테스트용(삭제예정)
	@GetMapping("/profile")
	public String profilePage() {
		return "profile";  // profile.jsp로 연결됨
	}
}
