package com.example.community.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.community.dto.PostCommentStatsDto;
import com.example.community.dto.PostWithCommentCountDTO;
import com.example.community.model.Community;
import com.example.login_signup_back.security.JwtTokenProvider;
import com.example.mapper.Mappers;
import jakarta.servlet.http.HttpServletRequest;

/**
 * # 작성자 : 이재훈
 * # 작성일 : 2024-10-08
 * # 목  적 : 커뮤니티 게시물에 대한 CRUD 기능을 제공하는 서비스
 * # 기  능 : 게시물 생성, 수정, 삭제, 조회 및 파일 저장 처리
 */
@Service
public class CommunityService {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;  // JWT 토큰 제공자
	
	@Autowired
	private Mappers mappers;

	// 파일 저장 경로 설정
	private static final String UPLOAD_DIR = "C:/DEV/uploads";

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 모든 커뮤니티 게시물 조회
     * # 기  능 : 커뮤니티에 존재하는 모든 게시물 조회
     * # 매개변수 : 없음
     * # 반환값 : List<Community> - 게시물 목록
     */
	@Transactional
	public List<Community> getAllCommunityPosts() {
		
		return mappers.getAllCommunityPosts();
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 사용자 고유번호로 게시물 조회
     * # 기  능 : 사용자의 고유번호를 이용해 작성한 게시물 조회
     * # 매개변수 : String communityId - 사용자의 고유번호
     * # 반환값 : List<Community> - 사용자가 작성한 게시물 목록
     */
    public List<Community> getPostsByUser(HttpServletRequest request) {
        // JWT 토큰에서 인증 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName(); // 토큰에서 유저 고유 번호 추출
        
        // 로그 출력
        System.out.println("########################################################################################");
        System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("########################################################################################");
        System.out.println(userUniqueNumber);

        // 추출한 userUniqueNumber로 해당 유저의 게시물 조회
        return mappers.getPostsByUser(userUniqueNumber);
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 특정 게시물 조회
     * # 기  능 : 게시물 ID로 게시물 조회 및 조회수 증가
     * # 매개변수 : int postId - 게시물 ID
     * # 반환값 : Community - 조회된 게시물 객체
     */
	public Community getCommunityPostById(int postId) {
		
		mappers.increasePostView(postId);
		return mappers.getCommunityPostById(postId);
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 게시물 생성
     * # 기  능 : 새로운 게시물을 생성하며 파일 업로드가 있을 경우 저장
     * # 매개변수 : HttpServletRequest request, Community community, MultipartFile file - 요청 객체, 게시물 객체, 업로드 파일
     * # 반환값 : 없음
     */
	public void createCommunityPost(HttpServletRequest request, Community community, MultipartFile file) throws IOException {
		
        // JWT 토큰에서 인증 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();
        System.out.println("##########################################################################################################");
        System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("##########################################################################################################");
        System.out.println(userUniqueNumber);
		
		community.setPostView(0);

		if (file != null && !file.isEmpty()) {
			String filePath = saveFile(file);
			// 상대 경로로 저장 (예: /uploads/uniqueFileName)
			community.setPostImgPath("/uploads/" + new File(filePath).getName());
		}
		mappers.createCommunityPost(community);
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 게시물 수정
     * # 기  능 : 기존 게시물 수정, 파일 업로드 시 처리 및 게시물 수정 시간 업데이트
     * # 매개변수 : HttpServletRequest request, int postId, Community community, MultipartFile file - 요청 객체, 게시물 ID, 게시물 객체, 업로드 파일
     * # 반환값 : 없음
     */
    public void updateCommunityPost(HttpServletRequest request, int postId, Community community, MultipartFile file) throws IOException {

        // JWT 토큰에서 인증 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();

        // 수정하려는 게시물의 작성자와 현재 사용자가 동일한지 확인
        Community existingPost = mappers.getCommunityPostById(postId);
        if (!existingPost.getUserUniqueNumber().equals(userUniqueNumber)) {
            throw new SecurityException("게시물을 수정할 권한이 없습니다.");
        }

        // 파일이 있을 경우 처리
        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file);
            community.setPostImgPath("/uploads/" + new File(filePath).getName());
        }

        community.setPostId(postId); // 게시물 ID 설정
        community.setCommChangeDate(LocalDateTime.now()); // 수정 날짜 업데이트
        mappers.updateCommunityPost(community); // 게시물 수정
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 게시물 삭제
     * # 기  능 : 특정 게시물 삭제
     * # 매개변수 : HttpServletRequest request, int postId - 요청 객체, 게시물 ID
     * # 반환값 : 없음
     */
	public void deleteCommunityPost(HttpServletRequest request, int postId) {
		
        // JWT 토큰에서 인증 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();
		
        // 삭제하려는 게시물의 작성자와 현재 사용자가 동일한지 확인
        Community existingPost = mappers.getCommunityPostById(postId);
        if (!existingPost.getUserUniqueNumber().equals(userUniqueNumber)) {
            throw new SecurityException("게시물을 삭제할 권한이 없습니다.");
        }

        mappers.deleteCommunityPost(postId); // 게시물 삭제
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 파일 저장
     * # 기  능 : 업로드된 파일을 서버의 특정 경로에 저장
     * # 매개변수 : MultipartFile file - 업로드된 파일 객체
     * # 반환값 : String - 저장된 파일의 경로
     */
	private String saveFile(MultipartFile file) throws IOException {
		String originalFileName = file.getOriginalFilename();
		String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
		String fullPath = UPLOAD_DIR + File.separator + uniqueFileName;
		File dest = new File(fullPath);
		file.transferTo(dest); // 파일 저장
		
		return fullPath; // 저장된 파일 경로 반환
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 관리자용 게시물 조회
     * # 기  능 : 모든 게시물과 댓글 수를 함께 조회
     * # 매개변수 : 없음
     * # 반환값 : List<PostWithCommentCountDTO> - 게시물과 댓글 수가 포함된 게시물 목록
     */
    public List<PostWithCommentCountDTO> getAllPostsWithCommentCount() {
    	
        return mappers.getAllPostsWithCommentCount();
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 게시물 세부 정보 조회
     * # 기  능 : 특정 게시물의 세부 정보 및 댓글 수 조회
     * # 매개변수 : int postId - 게시물 ID
     * # 반환값 : PostWithCommentCountDTO - 게시물 세부 정보와 댓글 수
     */
    public PostWithCommentCountDTO getPostDetail(int postId) {
    	
        return mappers.getPostDetail(postId);
    }
    
    // 회원별 게시글 수와 댓글 수 조회 서비스
    public PostCommentStatsDto getPostAndCommentStatsByUserId(String userId) {
    	
        return mappers.findPostAndCommentStatsByUserId(userId);
    }
}
