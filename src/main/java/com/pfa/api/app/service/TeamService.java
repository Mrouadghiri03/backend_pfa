package com.pfa.api.app.service;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.pfa.api.app.dto.requests.TeamDTO;
import com.pfa.api.app.dto.responses.TeamResponseDTO;


public interface TeamService {
    TeamResponseDTO createTeam(TeamDTO teamDTO) throws NotFoundException ;
    void deleteTeamById(Long teamId) ;
    void deleteTeamByName(String teamName) ;
    TeamResponseDTO updateTeam(Long teamId, TeamDTO teamDTO) throws NotFoundException;
    TeamResponseDTO getTeamById(Long teamId) ;
    TeamResponseDTO getTeamByName(String teamName);
    List<TeamResponseDTO> getAllTeams(String academicYear);


}
