package com.example.community.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.community.dto.CommentReplyDTO;
import com.example.community.model.Comment;
import com.example.community.model.Reply;
import com.example.login_signup_back.security.JwtTokenProvider;
import com.example.mapper.Mappers;
import jakarta.servlet.http.HttpServletRequest;

/**
 * # 작성자 : 이재훈
 * # 작성일 : 2024-10-08
 * # 목  적 : 댓글 및 대댓글에 대한 CRUD 기능을 제공하는 서비스
 * # 기  능 : 댓글 및 대댓글 생성, 수정, 삭제, 조회 등의 비즈니스 로직 처리
 */
@Service
public class CommentService {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;  // JWT 토큰 제공자
	
	@Autowired
	private Mappers mappers;

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 특정 게시물의 모든 댓글 조회
     * # 기  능 : 게시물 ID로 해당 게시물에 달린 모든 댓글을 조회
     * # 매개변수 : int postId - 게시물 ID
     * # 반환값 : List<Comment> - 게시물의 모든 댓글 목록
     */
	public List<Comment> getCommentsByPostId(int postId) {
		
		return mappers.getCommentsByPostId(postId);
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 사용자 고유번호로 해당 사용자의 모든 댓글 조회
     * # 기  능 : 사용자 고유번호로 사용자가 작성한 모든 댓글을 조회
     * # 매개변수 : String nickname - 사용자 닉네임
     * # 반환값 : List<Comment> - 사용자가 작성한 댓글 목록
     */
    public List<Comment> getCommentsByUser(HttpServletRequest request) {
        // JWT에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();
        System.out.println("###############################################");
        System.out.println("인증된 사용자 유니크 넘버입니다: " + userUniqueNumber);
        System.out.println("###############################################");

        // 사용자에 맞는 댓글을 조회하여 반환
        return mappers.getCommentsByUser(userUniqueNumber);
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 특정 게시물의 특정 댓글 조회
     * # 기  능 : 게시물 ID와 댓글 번호로 해당 댓글을 조회
     * # 매개변수 : int postId, int postCommentNum - 게시물 ID와 댓글 번호
     * # 반환값 : Comment - 조회된 댓글 객체
     */
	public Comment getCommentById(int postId, int postCommentNum) {
		
		return mappers.getCommentById(postId, postCommentNum);
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 댓글 생성
     * # 기  능 : 게시물에 새로운 댓글을 생성하며 JWT 토큰을 통해 사용자 인증 처리
     * # 매개변수 : Comment comment, HttpServletRequest request - 댓글 객체와 요청 객체
     * # 반환값 : 없음
     */
	@Transactional
	public void createComment(Comment comment, HttpServletRequest request) {
		
		// JWT 토큰 토큰추출, 유효성검사 통합메서드
		Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
		// JWT 토큰에서 userUniqueNumber 추출
		String userUniqueNumber = authentication.getName();
		System.out.println("##########################################################################################################");
		System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("##########################################################################################################");
		System.out.println(userUniqueNumber);
		
		Integer maxCommentNum = mappers.getMaxCommentNumByPostId(comment.getPostId());
		if (maxCommentNum == null) {
			maxCommentNum = 1; // 해당 게시물의 첫 번째 댓글일 경우
		} else {
			maxCommentNum += 1;
		}
		comment.setPostCommentNum(maxCommentNum);
		mappers.createComment(comment);
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 댓글 수정
     * # 기  능 : 댓글을 수정하며 JWT 토큰을 통해 사용자 인증 처리
     * # 매개변수 : Comment comment, HttpServletRequest request - 댓글 객체와 요청 객체
     * # 반환값 : 없음
     */
	public void updateComment(Comment comment, HttpServletRequest request) {
		
		// JWT 토큰 토큰추출, 유효성검사 통합메서드
		Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
		// JWT 토큰에서 userUniqueNumber 추출
		String userUniqueNumber = authentication.getName();
		System.out.println("##########################################################################################################");
		System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("##########################################################################################################");
		System.out.println(userUniqueNumber);
		
		mappers.updateComment(comment);
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 댓글 삭제
     * # 기  능 : 게시물의 특정 댓글을 삭제하며 삭제된 댓글 이후의 댓글 번호 업데이트
     * # 매개변수 : int postId, int postCommentNum, HttpServletRequest request - 게시물 ID, 댓글 번호, 요청 객체
     * # 반환값 : 없음
     */
	@Transactional
	public void deleteComment(int postId, int postCommentNum, HttpServletRequest request) {
		
		// JWT 토큰 토큰추출, 유효성검사 통합메서드
		Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
		// JWT 토큰에서 userUniqueNumber 추출
		String userUniqueNumber = authentication.getName();
		System.out.println("##########################################################################################################");
		System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("##########################################################################################################");
		System.out.println(userUniqueNumber);
		
		// 댓글 삭제
		mappers.deleteComment(postId, postCommentNum);

		// 삭제된 댓글 번호 이후의 댓글 조회
		List<Comment> comments = mappers.getCommentsByPostIdAndGreaterThanCommentNum(postId, postCommentNum);
		for (Comment comment : comments) {
			int newPostCommentNum = comment.getPostCommentNum() - 1;
			// 댓글 번호 업데이트
			mappers.updateCommentNumber(newPostCommentNum, comment.getPostCommentNum(), postId);
			comment.setPostCommentNum(newPostCommentNum); // 로컬 객체 업데이트
		}
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 대댓글 조회
     * # 기  능 : 특정 게시물의 특정 댓글에 달린 대댓글 조회
     * # 매개변수 : int postId, int postCommentNum - 게시물 ID와 댓글 번호
     * # 반환값 : List<Reply> - 대댓글 목록
     */
	public List<Reply> getRepliesByCommentId(int postId, int postCommentNum) {
		
		return mappers.getRepliesByCommentId(postId, postCommentNum);
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 특정 대댓글 조회
     * # 기  능 : 게시물 ID, 댓글 번호, 대댓글 번호로 대댓글 조회
     * # 매개변수 : int postId, int postCommentNum, int replyId - 게시물 ID, 댓글 번호, 대댓글 번호
     * # 반환값 : Reply - 조회된 대댓글 객체
     */
	public Reply getReplyById(int postId, int postCommentNum, int replyId) {
		
		return mappers.getReplyById(postId, postCommentNum, replyId);
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 대댓글 생성
     * # 기  능 : 특정 게시물의 댓글에 대한 새로운 대댓글 생성
     * # 매개변수 : HttpServletRequest request, Reply replyComment, int postId, int postCommentNum - 요청 객체, 대댓글 객체, 게시물 ID, 댓글 번호
     * # 반환값 : 없음
     */
	@Transactional
	public void createReplyComment(HttpServletRequest request, Reply replyComment, int postId, int postCommentNum) {
		
		// JWT 토큰 토큰추출, 유효성검사 통합메서드
		Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);

		// JWT 토큰에서 userUniqueNumber 추출
		String userUniqueNumber = authentication.getName();
		System.out.println("##########################################################################################################");
		System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("##########################################################################################################");
		System.out.println(userUniqueNumber);
		
		// 해당 댓글의 최대 reply_id 값을 가져와서 +1로 설정
		int maxReplyId = mappers.getMaxReplyIdByPostIdAndCommentNum(postId, postCommentNum);
		replyComment.setReplyId(maxReplyId + 1);

		// 대댓글 생성
		mappers.createReply(replyComment);
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 대댓글 수정
     * # 기  능 : 특정 대댓글 수정
     * # 매개변수 : HttpServletRequest request, Reply replyComment - 요청 객체와 대댓글 객체
     * # 반환값 : 없음
     */
	public void updateReplyComment(HttpServletRequest request, Reply replyComment) {
		
		// JWT 토큰 토큰추출, 유효성검사 통합메서드
		Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);

		// JWT 토큰에서 userUniqueNumber 추출
		String userUniqueNumber = authentication.getName();
		System.out.println("##########################################################################################################");
		System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("##########################################################################################################");
		System.out.println(userUniqueNumber);
		
		mappers.updateReply(replyComment);
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 대댓글 삭제
     * # 기  능 : 특정 대댓글 삭제하며 삭제된 대댓글 이후의 대댓글 번호 업데이트
     * # 매개변수 : HttpServletRequest request, int postId, int postCommentNum, int replyId - 요청 객체, 게시물 ID, 댓글 번호, 대댓글 번호
     * # 반환값 : 없음
     */
	@Transactional
	public void deleteReplyComment(HttpServletRequest request, int postId, int postCommentNum, int replyId) {
		
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();
        System.out.println("##########################################################################################################");
        System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("##########################################################################################################");
        System.out.println(userUniqueNumber);
		
		mappers.deleteReply(postId, postCommentNum, replyId);

        List<Reply> replies = mappers.getRepliesByPostIdAndCommentNumAndGreaterThanReplyId(postId, postCommentNum, replyId);
        for (Reply reply : replies) {
            int newReplyId = reply.getReplyId() - 1;
            mappers.updateReplyNumber(newReplyId, reply.getReplyId(), postId, postCommentNum);
            reply.setReplyId(newReplyId);
		}
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 모든 댓글 및 대댓글 조회
     * # 기  능 : 모든 게시물에 달린 댓글과 대댓글을 함께 조회
     * # 매개변수 : 없음
     * # 반환값 : List<CommentReplyDTO> - 댓글 및 대댓글 목록
     */
	public List<CommentReplyDTO> getAllCommentsAndReplies() {
		
        return mappers.getAllCommentsAndReplies();
	}
	
	@Transactional
	public void deleteAdminComment(HttpServletRequest request, int postId, int postCommentNum) {
		
		// JWT 토큰 토큰추출, 유효성검사 통합메서드
		Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
		// JWT 토큰에서 userUniqueNumber 추출
		String userUniqueNumber = authentication.getName();
		System.out.println("##########################################################################################################");
		System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("##########################################################################################################");
		System.out.println(userUniqueNumber);
		
		// 댓글 삭제
		mappers.deleteAdminComment(postId, postCommentNum);
	}
}
