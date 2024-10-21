package com.example.question.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.example.community.dto.QuestionDTO;
import com.example.login_signup_back.security.JwtTokenProvider;
import com.example.mapper.Mappers;
import com.example.question.model.Question;
import jakarta.servlet.http.HttpServletRequest;

/**
 * # 작성자 : 이재훈
 * # 작성일 : 2024-10-08
 * # 목  적 : 문의 게시물에 대한 CRUD 및 답변 관리 기능 제공
 * # 기  능 : 문의 게시물 생성, 수정, 삭제, 조회 및 답변 추가/수정/삭제 기능 제공
 */
@Service
public class QuestionService {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;  // JWT 토큰 제공자
	
	@Autowired
	private Mappers mappers;

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 모든 문의글 조회
     * # 기  능 : 데이터베이스에서 모든 문의글을 조회
     * # 매개변수 : 없음
     * # 반환값 : List<Question> - 문의글 목록
     */
    public List<Question> getAllQuestions() {
    	
        return mappers.getAllQuestions();
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 특정 문의글 조회
     * # 기  능 : 문의글 번호로 조회수 증가 후 문의글 조회
     * # 매개변수 : int questionNum - 문의글 번호
     * # 반환값 : Question - 조회된 문의글 객체
     */
    public Question getQuestionById(int questionNum) {
    	
        mappers.increaseQuestionView(questionNum);
        return mappers.getQuestionById(questionNum);
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 문의글 생성
     * # 기  능 : 새로운 문의글을 생성하고 비밀번호 처리 및 JWT 유효성 검사
     * # 매개변수 : HttpServletRequest request, Question question - 요청 객체, 문의글 객체
     * # 반환값 : 없음
     */
    public void createQuestion(HttpServletRequest request, Question question) {

        // JWT 토큰에서 인증 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();
        System.out.println("##########################################################################################################");
        System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("##########################################################################################################");
        System.out.println(userUniqueNumber);

        if (question.getPrivateQuestionPassworld() == null) {
            question.setPrivateQuestionPassworld(null); // 실제 null 처리
        }
        mappers.createQuestion(question);
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 문의글 수정
     * # 기  능 : 특정 문의글 수정 및 JWT 유효성 검사
     * # 매개변수 : HttpServletRequest request, int questionNum, Question question - 요청 객체, 문의글 번호, 문의글 객체
     * # 반환값 : 없음
     */
    public void updateQuestion(HttpServletRequest request, int questionNum, Question question) {

        // JWT 토큰에서 인증 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();
        System.out.println("##########################################################################################################");
        System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("##########################################################################################################");
        System.out.println(userUniqueNumber);

        question.setQuestionNum(questionNum);
        mappers.updateQuestion(question);
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 문의글 삭제
     * # 기  능 : 특정 문의글 삭제 및 JWT 유효성 검사
     * # 매개변수 : HttpServletRequest request, int questionNum - 요청 객체, 문의글 번호
     * # 반환값 : 없음
     */
    public void deleteQuestion(HttpServletRequest request, int questionNum) {

        // JWT 토큰에서 인증 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();
        System.out.println("##########################################################################################################");
        System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("##########################################################################################################");
        System.out.println(userUniqueNumber);

        mappers.deleteQuestion(questionNum);
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 답변 추가 또는 수정
     * # 기  능 : 특정 문의글에 대한 답변을 추가하거나 수정
     * # 매개변수 : HttpServletRequest request, int questionNum, String answer - 요청 객체, 문의글 번호, 답변 내용
     * # 반환값 : 없음
     */
    public void addOrUpdateAnswer(HttpServletRequest request, int questionNum, String answer) {

        // JWT 토큰에서 인증 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();
        System.out.println("##########################################################################################################");
        System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("##########################################################################################################");
        System.out.println(userUniqueNumber);

        mappers.updateAnswer(questionNum, answer); // 답변이 없으면 새로 추가, 있으면 수정
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 답변 삭제
     * # 기  능 : 특정 문의글에 대한 답변을 삭제
     * # 매개변수 : HttpServletRequest request, int questionNum - 요청 객체, 문의글 번호
     * # 반환값 : 없음
     */
    public void deleteAnswer(HttpServletRequest request, int questionNum) {

        // JWT 토큰에서 인증 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();
        System.out.println("##########################################################################################################");
        System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("##########################################################################################################");
        System.out.println(userUniqueNumber);

        mappers.deleteAnswer(questionNum);
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 관리자용 문의 목록 조회
     * # 기  능 : 관리자용으로 모든 문의 목록을 조회
     * # 매개변수 : HttpServletRequest request - 요청 객체
     * # 반환값 : List<QuestionDTO> - 문의글 목록
     */
    public List<QuestionDTO> getAdminAllQuestions(HttpServletRequest request) {

        // JWT 토큰에서 인증 정보 추출
        Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
        String userUniqueNumber = authentication.getName();
        System.out.println("##########################################################################################################");
        System.out.println("이게 인증통과한 유저 유니크 넘버다!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("##########################################################################################################");
        System.out.println(userUniqueNumber);

        return mappers.getAdminAllQuestions();
    }

	 // # 작성자 : 이재훈
	 // # 작성일 : 2024-10-08
	 // # 목  적 : 특정 사용자의 모든 문의글을 조회함
	 // # 기  능 : 주어진 userUniqueNumber로 작성된 모든 문의글을 데이터베이스에서 가져와 반환함
	 // # 매개변수 : userUniqueNumber : 사용자의 고유번호
	 // # 반환값 : 주어진 사용자의 문의글 목록 (List<Question>)
   public List<Question> getQuestionsByUser(HttpServletRequest request) {
       // JWT에서 사용자 정보 추출
       Authentication authentication = jwtTokenProvider.resolveAndAuthenticateToken(request);
       String userUniqueNumber = authentication.getName();

       System.out.println("###############################################");
       System.out.println("인증된 사용자 유니크 넘버입니다: " + userUniqueNumber);
       System.out.println("###############################################");

       // 사용자에 맞는 문의글을 조회하여 반환
       return mappers.getQuestionsByUser(userUniqueNumber);
   }
}
