package com.example.scheduleresults.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.scheduleresults.model.ScheduleResults;
import com.example.scheduleresults.model.ScoreBoard;
import com.example.scheduleresults.service.ResultsService;

@RestController
@RequestMapping("/api") // API 경로를 '/api'로 시작하게 변경
public class ScheduleResultsController {

    @Autowired
    private ResultsService resultsService;
    
    // 결과 조회 (GET)
    @GetMapping("/scheduleresults")
    public Map<String, Object> getResultsPage(@RequestParam(value = "year", defaultValue = "2024") int year,
                                  @RequestParam(value = "month", defaultValue = "08") int month) {

        List<ScheduleResults> scheduleResults = resultsService.getAllResults(year, month);
        Map<String, Object> response = new HashMap<>();
        response.put("scheduleResults", scheduleResults);
        response.put("year", year);
        response.put("month", month);
        
        return response;  // JSON 형식으로 반환
    }
    
    @GetMapping("/postscheduleresults")
    public Map<String, Object> getPostResultsPage(@RequestParam(value = "year", defaultValue = "2023") int year,
                                  @RequestParam(value = "month", defaultValue = "10") int month) {

        List<ScheduleResults> postScheduleResults = resultsService.getPostResults(year, month);
        Map<String, Object> response = new HashMap<>();
        response.put("postScheduleResults", postScheduleResults);
        response.put("year", year);
        response.put("month", month);
        
        return response;  // JSON 형식으로 반환
    }

    // 점수보드 조회 (GET)
    @GetMapping("/scoreboard")
    public Map<String, Object> getScoreboardPage(@RequestParam(value = "date", required = false, defaultValue = "2024-08-13") String date) {
        List<ScoreBoard> scoreBoard = resultsService.getSelectAllScoreBoard(date);
        Map<String, Object> response = new HashMap<>();
        response.put("scoreBoard", scoreBoard);
        response.put("date", date);
        
        return response;  // JSON 형식으로 반환
    }
    
    @GetMapping("/prevscoreboard")
    public Map<String, Object> getPrevScoreboardPage(@RequestParam(value = "date", required = false) String date) {
        List<ScoreBoard> scoreBoard = resultsService.getSelectPrevScoreBoard(date);
        Map<String, Object> response = new HashMap<>();
        response.put("scoreBoard", scoreBoard);
        response.put("date", date);
        
        return response;  // JSON 형식으로 반환
    }
    
    @GetMapping("/nextscoreboard")
    public Map<String, Object> getNextScoreboardPage(@RequestParam(value = "date", required = false) String date) {
        List<ScoreBoard> scoreBoard = resultsService.getSelectNextScoreBoard(date);
        Map<String, Object> response = new HashMap<>();
        response.put("scoreBoard", scoreBoard);
        response.put("date", date);
        
        return response;  // JSON 형식으로 반환
    }
    

    @GetMapping("/mainschedule")
    public Map<String, Object> getMainSchedule(@RequestParam(value = "date") String date){
    	List<ScheduleResults> mainSchedule = resultsService.getMainResults(date);
    	Map<String, Object> response = new HashMap<>();
    	response.put("mainSchedule", mainSchedule);
    	response.put("date", date);
    	
    	return response;
    }
}
