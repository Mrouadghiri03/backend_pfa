package com.pfa.api.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.pfa.api.app.entity.Assignment;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.TeamPreference;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.ProjectPreferenceRepository;
import com.pfa.api.app.repository.ProjectRepository;
import com.pfa.api.app.repository.UserRepository;

public class ProjectUtils {
    @SuppressWarnings("null")
    public static Map<User, Project> assignTeamsToProjects(List<TeamPreference> projectsPreferences 
        ,UserRepository userRepository ,ProjectPreferenceRepository projectPreferenceRepository
        ,ProjectRepository projectRepository) throws NotFoundException {
        for (TeamPreference teamPreference : projectsPreferences) {
            System.out.println("Team ID: " + teamPreference.getId());
            System.out.println("User ID: " + teamPreference.getUser().getId());
            System.out.println("Project Preference Ranks: " + teamPreference.getProjectPreferenceRanks());
            System.out.println();
        }
        int iterations = projectsPreferences.size();
        Map<Long,Long> assignedProjects = new HashMap<>();
        List<TeamPreference> holderProjectsPreferences = new ArrayList<>(projectsPreferences);
        for (int rank = 1; rank <= iterations; rank++) {
            for (TeamPreference projectsPreference : holderProjectsPreferences) {
                if (assignedProjects.containsKey(projectsPreference.getUser().getId())) {
                    continue;
                } 
                Long chosenOne = null;
                List<Long> hasSameRank = new ArrayList<>();
                chosenOne = getCurrentRankProject(projectsPreference, chosenOne, rank);
                if (chosenOne == null) {
                    continue;
                }
                hasSameRank.add(projectsPreference.getUser().getId());

                for (TeamPreference otherProjectsPreference : holderProjectsPreferences) {
                    if (projectsPreference != otherProjectsPreference
                            && !assignedProjects.containsKey(otherProjectsPreference.getUser().getId())) {

                        for (Map.Entry<Long, Integer> entry : otherProjectsPreference.getProjectPreferenceRanks()
                                .entrySet()) {

                            if (entry.getValue() != null && entry.getValue() == rank) {
                                if (chosenOne == entry.getKey()) {
                                    hasSameRank.add(otherProjectsPreference.getUser().getId());
                                }
                            }

                        }
                    }
                }

                if (hasSameRank.size() > 1) {
                    makeRandomShit(holderProjectsPreferences,chosenOne, assignedProjects, hasSameRank);
                    continue;
                } else {
                    assignedProjects.put(projectsPreference.getUser().getId(), chosenOne);
                    removeOtherProjectsFromUser(projectsPreference, chosenOne);
                }
                holderProjectsPreferences = removeTakenProjectFromOthers(holderProjectsPreferences, projectsPreference,chosenOne);
                System.out.println("first elimination : ");
                printPreferences(holderProjectsPreferences);

            }
        }
        projectPreferenceRepository.saveAll(holderProjectsPreferences);
        printPreferences(holderProjectsPreferences);
        System.out.println(assignedProjects);
        Map<User,Project> resultMap = new HashMap<>();
        for (Map.Entry<Long, Long> entry : assignedProjects.entrySet()) {
            Long userId = entry.getKey();
            Long projectId = entry.getValue();

            User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

            Project project = projectRepository.findById(projectId).orElseThrow(NotFoundException::new);

            resultMap.put(user, project);
        }

        return resultMap;

    }

    public static void printPreferences(List<TeamPreference> holderProjectsPreferences){
        for (TeamPreference teamPreference : holderProjectsPreferences) {
            System.out.println("Team preference ID: " + teamPreference.getId());
            System.out.println("User ID: " + teamPreference.getUser().getId());
            System.out.println("Project Preference Ranks: " + teamPreference.getProjectPreferenceRanks());
            System.out.println();
        }
    }
    public static TeamPreference removeOtherProjectsFromUser(TeamPreference projectsPreference, Long chosenOne) {
                for (Map.Entry<Long, Integer> entry : projectsPreference.getProjectPreferenceRanks()
                        .entrySet()) {
                    if (entry.getKey() != chosenOne) {
                        entry.setValue(null); // Remove this project from his preferences
                    }

                }
        return projectsPreference;
    }
    public static List<TeamPreference> removeTakenProjectFromOthers(List<TeamPreference> holderProjectsPreferences,
            TeamPreference projectsPreference, Long chosenOne) {
        for (TeamPreference otherProjectPreference : holderProjectsPreferences) {
            if (otherProjectPreference != projectsPreference) { // Skip comparing with itself
                for (Map.Entry<Long, Integer> entry : otherProjectPreference.getProjectPreferenceRanks()
                        .entrySet()) {
                    if (entry.getKey() == chosenOne) {
                        entry.setValue(null); // Remove this project from their preferences
                    }

                }
            }
        }
        return holderProjectsPreferences;
    }

    public static void makeRandomShit(List<TeamPreference> holderProjectPreferences, Long chosenOne, Map<Long, Long> assignedProjects, List<Long> hasSameRank) {
        int bound = hasSameRank.size() * 5;
        int start = 1;
        Map<Long, List<Integer>> intervals = new HashMap<>();
        for (Long user : hasSameRank) {
            List interval = new ArrayList<>();
            for (int j = start; j <= start + 4; j++) {
                interval.add(j);
            }
            intervals.put(user, interval);
            start += 5;
            if (start > bound) {
                break;
            }
        }

        int randomIndex = new Random().nextInt(bound) + 1;

        for (Map.Entry<Long, List<Integer>> entry : intervals.entrySet()) {
            if (entry.getValue().contains(randomIndex)) {
                System.out.println("user " + entry.getKey() + " : " + chosenOne + " , index :  " + randomIndex);
                assignedProjects.put(entry.getKey(), chosenOne);
                TeamPreference theLuckyOne = holderProjectPreferences.stream()
                    .filter(teamPreference -> teamPreference.getUser().getId() == entry.getKey()).findFirst().get();
                removeTakenProjectFromOthers(holderProjectPreferences, theLuckyOne, chosenOne);
                removeOtherProjectsFromUser(theLuckyOne, chosenOne);
                break;
            }
        }
    }

    public static Long getCurrentRankProject(TeamPreference projectPreference,
            Long chosenOne, int rank) {
        for (Map.Entry<Long, Integer> entry : projectPreference.getProjectPreferenceRanks().entrySet()) {
            if (entry.getValue() != null && entry.getValue() == rank) {
                return entry.getKey(); // Found the project with the current rank
            }
        }
        return chosenOne;
    }

}
