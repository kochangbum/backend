package com.example.question.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.community.dto.QuestionDTO;
import com.example.question.model.Question;
import com.example.question.service.QuestionService;
import com.example.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;

/**
 * # 작성자 : 이재훈
 * # 작성일 : 2024-10-08
 * # 목  적 : 문의글에 대한 CRUD 및 파일 업로드 처리
 * # 기  능 : 문의글의 생성, 조회, 수정, 삭제 기능과 파일 첨부 기능을 제공
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {

	@Autowired
	private QuestionService questionService;

	private final FileStorageService fileStorageService;

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : FileStorageService 주입
     * # 기  능 : 파일 저장 서비스를 주입받아 사용
     * @param fileStorageService 파일 저장 서비스
     */
	@Autowired
	public QuestionController(FileStorageService fileStorageService) {
		
		this.fileStorageService = fileStorageService;
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 관리자용 모든 문의글 조회
     * # 기  능 : 관리자가 모든 문의글을 조회할 수 있도록 목록 반환
     * # 반환값 : List<QuestionDTO> - 모든 문의글 목록
     */
	@GetMapping("/list")
	public List<QuestionDTO> getAdminAllQuestions(HttpServletRequest request) {
		
		return questionService.getAdminAllQuestions(request);
	}

	 // # 작성자 : 이재훈
	 // # 작성일 : 2024-10-08
	 // # 목  적 : 특정 사용자의 문의글 목록을 조회함
	 // # 기  능 : userUniqueNumber를 통해 해당 사용자가 작성한 모든 문의글을 반환함
	 // # 매개변수 : userUniqueNumber - 사용자의 고유번호
	 // # 반환값 : 해당 사용자가 작성한 문의글 목록 (List<Question>)
   @GetMapping("/user/questions")
   public List<Question> getQuestionsByUser(HttpServletRequest request) {
       return questionService.getQuestionsByUser(request);
   }
   
   /**
    * # 작성자 : 이재훈
    * # 작성일 : 2024-10-08
    * # 목  적 : 모든 문의글 조회
    * # 기  능 : 모든 문의글을 조회하여 반환
    * # 반환값 : List<Question> - 모든 문의글 목록
    */
   @GetMapping("/all")
   public List<Question> getAllQuestions() {
       return questionService.getAllQuestions();
   }

   /**
    * # 작성자 : 이재훈
    * # 작성일 : 2024-10-08
    * # 목  적 : 특정 문의글 조회
    * # 기  능 : 문의글 번호로 특정 문의글 조회
    * # 매개변수 : int questionNum - 문의글 번호
    * # 반환값 : ResponseEntity<Question> - 해당 문의글 또는 NotFound 상태 반환
    */
	@GetMapping("/{questionNum}")
	public ResponseEntity<Question> getQuestionById(@PathVariable int questionNum) {
		
		Question question = questionService.getQuestionById(questionNum);
		if (question != null) {
			return ResponseEntity.ok(question);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 문의글 생성
     * # 기  능 : 제목, 내용, 비밀번호, 파일 등을 받아 새로운 문의글 생성
     * # 매개변수 : HttpServletRequest request, String questionTitle, String questionContent, int privateOption, String userUniqueNumber, MultipartFile file, String privateQuestionPassword
     * # 반환값 : ResponseEntity<String> - 문의글 생성 성공 또는 실패 메시지
     */
	@PostMapping("/create")
	public ResponseEntity<String> createQuestion(HttpServletRequest request, @RequestParam("questionTitle") String questionTitle,
			@RequestParam("questionContent") String questionContent, @RequestParam("privateOption") int privateOption,
			@RequestParam("userUniqueNumber") String userUniqueNumber,
			@RequestPart(value = "file", required = false) MultipartFile file, // 파일 첨부 추가
			@RequestParam(value = "privateQuestionPassword", required = false) String privateQuestionPassword) {
		
		try {
			Question question = new Question();
			question.setQuestionTitle(questionTitle);
			question.setQuestionContent(questionContent);
			question.setPrivateOption(privateOption);
			question.setUserUniqueNumber(userUniqueNumber);

            // 조회수 기본값 설정
			question.setQuestionPostView(0);

			if (privateQuestionPassword != null && !privateQuestionPassword.trim().isEmpty()) {
				question.setPrivateQuestionPassworld(privateQuestionPassword);
			} else {
				question.setPrivateQuestionPassworld(null);
			}

			// 파일이 있을 경우 처리
			if (file != null && !file.isEmpty()) {
				String fileName = fileStorageService.storeFile(file);
				question.setQuestion_img_path("/uploads/" + fileName);
			}
			questionService.createQuestion(request, question);
			return ResponseEntity.ok("문의글이 성공적으로 생성되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("문의글 생성 중 오류가 발생했습니다.");
		}
	}

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 문의글 수정
     * # 기  능 : 문의글 번호를 이용해 제목, 내용, 비공개 옵션 등을 수정
     * # 매개변수 : HttpServletRequest request, int questionNum, String questionTitle, String questionContent, int privateOption, MultipartFile file
     * # 반환값 : ResponseEntity<String> - 문의글 수정 성공 또는 실패 메시지
     */
    @PutMapping("/{questionNum}")
    public ResponseEntity<String> updateQuestion(HttpServletRequest request, @PathVariable int questionNum,
            @RequestParam("questionTitle") String questionTitle,
            @RequestParam("questionContent") String questionContent, @RequestParam("privateOption") int privateOption,
            @RequestPart(value = "file", required = false) MultipartFile file) {
    	
        try {
            Question question = new Question();
            question.setQuestionTitle(questionTitle);
            question.setQuestionContent(questionContent);
            question.setPrivateOption(privateOption);

            if (file != null && !file.isEmpty()) {
                String fileName = fileStorageService.storeFile(file);
                question.setQuestion_img_path("/uploads/" + fileName);
            }

            questionService.updateQuestion(request, questionNum, question);
            return ResponseEntity.ok("문의글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("문의글 수정 중 오류가 발생했습니다.");
        }
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 문의글 삭제
     * # 기  능 : 문의글 번호로 특정 문의글 삭제
     * # 매개변수 : HttpServletRequest request, int questionNum - 삭제할 문의글 번호
     * # 반환값 : ResponseEntity<String> - 문의글 삭제 성공 메시지
     */
    @DeleteMapping("/{questionNum}")
    public ResponseEntity<String> deleteQuestion(HttpServletRequest request, @PathVariable int questionNum) {
    	
        questionService.deleteQuestion(request, questionNum);
        
        return ResponseEntity.ok("문의글이 성공적으로 삭제되었습니다.");
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 문의글 비밀번호 확인
     * # 기  능 : 특정 문의글의 비밀번호가 맞는지 확인
     * # 매개변수 : int questionNum - 문의글 번호, Map<String, String> requestBody - 입력받은 비밀번호
     * # 반환값 : ResponseEntity<Map<String, Boolean>> - 비밀번호 확인 성공 여부
     */
    @PostMapping("/{questionNum}/verify-password")
    public ResponseEntity<Map<String, Boolean>> verifyPassword(@PathVariable int questionNum,
            @RequestBody Map<String, String> requestBody) {
    	
        String inputPassword = requestBody.get("password");

        Question question = questionService.getQuestionById(questionNum);

        if (question == null) {
            return ResponseEntity.status(404).body(Collections.singletonMap("success", false));
        }

        if (question.getPrivateQuestionPassworld() == null
                || question.getPrivateQuestionPassworld().equals(inputPassword)) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.ok(Collections.singletonMap("success", false));
        }
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 파일 다운로드
     * # 기  능 : 파일명을 이용해 파일 다운로드 제공
     * # 매개변수 : String fileName - 다운로드할 파일명
     * # 반환값 : ResponseEntity<Resource> - 파일 리소스 또는 오류 상태 반환
     */
    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
    	
        try {
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            String contentType = Files.probeContentType(resource.getFile().toPath());

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 이미지 제공
     * # 기  능 : 파일명을 이용해 이미지 파일 제공
     * # 매개변수 : String filename - 제공할 이미지 파일명
     * # 반환값 : ResponseEntity<Resource> - 이미지 리소스 또는 오류 상태 반환
     */
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
    	
        try {
            Resource file = fileStorageService.loadFileAsResource(filename);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(file);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 답변 추가 또는 수정
     * # 기  능 : 문의글 번호로 답변 추가 또는 수정
     * # 매개변수 : HttpServletRequest request, int questionNum, String answer - 추가 또는 수정할 답변
     * # 반환값 : ResponseEntity<String> - 답변 추가 또는 수정 성공 메시지
     */
    @PutMapping("/{questionNum}/answer")
    public ResponseEntity<String> addOrUpdateAnswer(HttpServletRequest request, @PathVariable int questionNum, @RequestParam String answer) {
    	
        questionService.addOrUpdateAnswer(request, questionNum, answer);
        
        return ResponseEntity.ok("Answer added or updated successfully");
    }

    /**
     * # 작성자 : 이재훈
     * # 작성일 : 2024-10-08
     * # 목  적 : 답변 삭제
     * # 기  능 : 문의글 번호로 답변 삭제
     * # 매개변수 : HttpServletRequest request, int questionNum - 답변 삭제할 문의글 번호
     * # 반환값 : ResponseEntity<String> - 답변 삭제 성공 메시지
     */
    @DeleteMapping("/{questionNum}/answer")
    public ResponseEntity<String> deleteAnswer(HttpServletRequest request, @PathVariable int questionNum) {
    	
        questionService.deleteAnswer(request, questionNum);
        
        return ResponseEntity.ok("Answer deleted successfully");
    }
}
