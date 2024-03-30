package com.pfa.api.app.service;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.pfa.api.app.dto.TeamDTO;
import com.pfa.api.app.entity.Team;


public interface TeamService {
    Team createTeam(TeamDTO teamDTO) throws NotFoundException ;
    void deleteTeamById(Long teamId) ;
    void deleteTeamByName(String teamName) ;
    Team updateTeam(Long teamId, TeamDTO teamDTO) throws NotFoundException;
    Team getTeamById(Long teamId) ;
    Team getTeamByName(String teamName);
    List<Team> getAllTeams();


}
