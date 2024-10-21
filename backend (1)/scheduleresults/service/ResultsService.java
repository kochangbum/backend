package com.example.scheduleresults.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mapper.Mappers;
import com.example.scheduleresults.model.ScheduleResults;
import com.example.scheduleresults.model.ScoreBoard;

@Service
public class ResultsService {
	
	@Autowired
	private Mappers mapper;
	
	public List<ScheduleResults> getAllResults(int year, int month) {
		List<ScheduleResults> results;
		results = mapper.selectAllResults(year, month);
		for(int i=0; i<results.size(); i++) {
			results.get(i).setHomeWinLose();
			results.get(i).setAwayWinLose();
			results.get(i).setHomePitcher();
			results.get(i).setAwayPitcher();
		}
		
		return results;
	}
	
	public List<ScheduleResults> getPostResults(int year, int month) {
		List<ScheduleResults> results;
		results = mapper.selectPostResults(year, month);
		for(int i=0; i<results.size(); i++) {
			results.get(i).setHomeWinLose();
			results.get(i).setAwayWinLose();
			results.get(i).setHomePitcher();
			results.get(i).setAwayPitcher();
			
		}
		return results;
	}

	public List<ScoreBoard> getSelectAllScoreBoard(String date) {
			List<ScoreBoard> results;
			results = mapper.selectAllScoreBoard(date); 
			for(int i=0; i<results.size(); i++) {
				results.get(i).setStatus();
				results.get(i).setAwayWinOrLose();
				results.get(i).setHomeWinOrLose();
			}
			return results;
	}
	
	public List<ScoreBoard> getSelectPrevScoreBoard(String date) {
			return mapper.selectPrevScoreBoard(date);
	}
	
	public List<ScoreBoard> getSelectNextScoreBoard(String date) {
			return mapper.selectNextScoreBoard(date);
	}

	
	public List<ScheduleResults> getMainResults(String date) {
		List<ScheduleResults> results;
		results = mapper.selectMainSchedule(date);
		for(int i=0; i<results.size(); i++) {
			results.get(i).setHomeWinLose();
			results.get(i).setAwayWinLose();
			results.get(i).setHomePitcher();
			results.get(i).setAwayPitcher();
			if(results.get(i).getEtc().equals("-")) {
				LocalDate datethis = LocalDate.parse(results.get(i).getGameDate().substring(0,10));
				if(datethis.isBefore(LocalDate.now())) {
					results.get(i).setEtc("경기종료");
				} else {
					results.get(i).setEtc("경기예정");
				}
			}
		}
		
		return results;
	}
}
