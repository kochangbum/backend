package com.example.records.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mapper.Mappers;
import com.example.records.model.Batters;
import com.example.records.model.BattersTeamRecord;
import com.example.records.model.Defence;
import com.example.records.model.DefencesTeamRecord;
import com.example.records.model.PitBatMatchup;
import com.example.records.model.Pitchers;
import com.example.records.model.PitchersTeamRecord;

@Service
public class RecordsService {
	
	@Autowired
	private Mappers mapper;
    
    public List<Defence> getSelectAllDefence(int year, String teamName){
    	return mapper.selectAllDefence(year, teamName);
    }
    
    public List<Batters> getSelectAllBatters(int year, String teamName){
    	return mapper.selectAllBatters(year, teamName);
    }
    
    public List<Pitchers> getselectAllPitchers(int year, String teamName){
    	return mapper.selectAllPitchers(year, teamName);
    }
    
    public List<PitBatMatchup> getPitBatMatchup(String pitcherTeam, String pitcher, String batterTeam, String batter){
		return mapper.selectPitBatMatchup(pitcherTeam, pitcher, batterTeam, batter);
    }
    public List<PitBatMatchup> getPitchersList(String pitcherTeam){
    	return mapper.selectPitchersList(pitcherTeam);
    }
    public List<PitBatMatchup> getBattersList(String batterTeam){
    	return mapper.selectBattersList(batterTeam);
    }
    public List<DefencesTeamRecord> getselectDefencesTeamRecords(int year){
    	return mapper.selectDefencesTeamRecord(year);
    }
    
    public List<BattersTeamRecord> getselectBattersTeamRecords(int year){
    	return mapper.selectBattersTeamRecord(year);
    }
    
    public List<PitchersTeamRecord> getselectPitchersTeamRecords(int year){
    	return mapper.selectPitchersTeamRecord(year);
    }
}
