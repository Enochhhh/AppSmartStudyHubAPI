package com.focusedapp.smartstudyhub.service;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.focusedapp.smartstudyhub.dao.FolderDAO;
import com.focusedapp.smartstudyhub.dao.ProjectDAO;
import com.focusedapp.smartstudyhub.exception.NotFoundValueException;
import com.focusedapp.smartstudyhub.model.Folder;
import com.focusedapp.smartstudyhub.model.Project;
import com.focusedapp.smartstudyhub.model.User;
import com.focusedapp.smartstudyhub.model.Work;
import com.focusedapp.smartstudyhub.model.custom.ProjectDTO;
import com.focusedapp.smartstudyhub.model.custom.ProjectGroupByDateDTO;
import com.focusedapp.smartstudyhub.util.DateUtils;
import com.focusedapp.smartstudyhub.util.enumerate.EnumRole;
import com.focusedapp.smartstudyhub.util.enumerate.EnumStatus;

@Service
public class ProjectService {

	@Autowired
	ProjectDAO projectDAO;
	@Autowired
	FolderService folderService;
	@Autowired
	UserService userService;
	@Autowired 
	FolderDAO folderDAO;
	@Autowired
	WorkService workService;
	@Autowired
	ExtraWorkService extraWorkService;
	@Autowired 
	ThreadService threadService;
	
	/**
	 * Find project by id
	 * 
	 * @param id
	 * @return
	 */
	public Project findByIdAndStatus(Integer id, String status) {
		Project project = projectDAO.findByIdAndStatus(id, EnumStatus.ACTIVE.getValue())
				.orElseThrow(() -> new NotFoundValueException("Not found project by Id!", "ProjectService -> findByIdAndStatus"));
		return project;
	}
	
	/**
	 * Find Project by Id
	 * 
	 * @param id
	 * @return
	 */
	public Project findById(Integer id) {
		return projectDAO.findById(id)
					.orElseThrow(() -> new NotFoundValueException("Not Found Project by Id", "ProjectService -> findById"));
	}
	
	/**
	 * Create Project
	 * 
	 * @param dataCreate
	 * @return
	 */
	public ProjectDTO createProject(ProjectDTO dataCreate) {

		Optional<Folder> folderOpt = folderDAO.findByIdAndStatus(dataCreate.getFolderId(), EnumStatus.ACTIVE.getValue());
		Folder folder = null;
		if (!folderOpt.isEmpty()) {
			folder = folderOpt.get();
		}
		
		Project project = Project.builder()
				.user(userService.findByIdAndStatus(dataCreate.getUserId(), EnumStatus.ACTIVE.getValue()))
				.folder(folder)
				.projectName(dataCreate.getProjectName())
				.colorCode(dataCreate.getColorCode())
				.iconUrl(dataCreate.getIconUrl())
				.createdDate(new Date())
				.status(EnumStatus.ACTIVE.getValue()).build();
		project = projectDAO.save(project);

		return new ProjectDTO(project);
	}
	
	/**
	 * Get Projects of user by status
	 * 
	 * @param userId
	 * @return
	 */
	public List<ProjectDTO> getProjectsOfUserByStatus(Integer userId, String status) {

		List<Project> projects = projectDAO.findByUserIdAndStatus(userId, status);
		List<ProjectDTO> projectsDto = projects.stream().map(p -> new ProjectDTO(p)).collect(Collectors.toList());
		return projectsDto;
	}
	
	/**
	 * Get Projects of user by folder and status
	 * 
	 * @param userId
	 * @param folderId
	 * @param status
	 * @return
	 */
	public List<ProjectDTO> getProjectsOfUserByFolderAndStatus(Integer userId, Integer folderId, String status) {

		List<Project> projects = projectDAO.findByUserIdAndFolderIdAndStatus(userId, folderId, status);
		List<ProjectDTO> projectsDto = projects.stream()
				.map(p -> new ProjectDTO(p))
				.collect(Collectors.toList());
		return projectsDto;
	}
	
	/**
	 * Get Project for updating Folder
	 * 
	 * @param userId
	 * @param folderId
	 * @return
	 */
	public List<ProjectDTO> getProjectsForUpdatingFolder(Integer userId, Integer folderId) {

		List<Project> projects = projectDAO.findProjectsForUpdatingFolder(userId, folderId);
		List<ProjectDTO> projectsDto = projects.stream()
				.map(p -> new ProjectDTO(p))
				.collect(Collectors.toList());
		return projectsDto;
	}
	
	/**
	 * Update Project Information
	 * 
	 * @param projectRequest
	 * @return
	 */
	public Project updateProject(ProjectDTO projectRequest) {
		Project projectDb = projectDAO.findById(projectRequest.getId())
				.orElseThrow(() -> new NotFoundValueException("Not Fould the Project to update!", "ProjectService -> updateProject"));
		Folder folder = null;
		if (projectRequest.getFolderId() != null) {
			folder = folderService.findByIdAndStatus(projectRequest.getFolderId(), EnumStatus.ACTIVE.getValue());
		}
		
		projectDb.setProjectName(projectRequest.getProjectName());
		projectDb.setColorCode(projectRequest.getColorCode());
		projectDb.setIconUrl(projectRequest.getIconUrl());
		projectDb.setFolder(folder);
		projectDb.setStatus(projectRequest.getStatus() == null ? EnumStatus.ACTIVE.getValue() : projectRequest.getStatus());
		
		projectDb = projectDAO.save(projectDb);
		return projectDb;		
	}
	
	/**
	 * Delete Project
	 * 
	 * @param projectRequest
	 * @return
	 */
	public Project deleteProject(Integer projectId) {
		Optional<Project> projectOpt = projectDAO.findById(projectId);
		if (projectOpt.isEmpty()) {
			return null;
		}
		Project projectDb = projectOpt.get();
		
		if (!projectDb.getStatus().equals(EnumStatus.DELETED.getValue())) {
			projectDb.setOldStatus(projectDb.getStatus());
			projectDb.setStatus(EnumStatus.DELETED.getValue());
		}
				
		List<Work> works = projectDb.getWorks();
		if (works != null) {
			works.stream()
				.forEach(w -> {
					workService.markDeletedWork(w.getId());
				});
		}

		projectDb = projectDAO.save(projectDb);
		return projectDb;		
	}
	
	/**
	 * Get Projects for adding folder
	 * 
	 * @return
	 */
	public List<ProjectDTO> getProjectsForAddingFolder(Integer userId) {
		
		List<Project> projects = projectDAO.findByUserIdAndFolderIdAndStatus(userId, null, EnumStatus.ACTIVE.getValue());
		
		return projects.stream()
				.map(proj -> new ProjectDTO(proj))
				.collect(Collectors.toList());
	}
	
	/**
	 * Check Maxium number of projects
	 * 
	 * @param userId
	 * @return
	 */
	public Boolean checkMaximumProject(Integer userId) {
		
		User user = userService.findByIdAndStatus(userId, EnumStatus.ACTIVE.getValue());
		if (user.getRole().equals(EnumRole.PREMIUM.getValue())) {
			return false;
		}
		
		List<Project> projects = projectDAO.findByUserIdAndStatus(userId, EnumStatus.ACTIVE.getValue());
		Integer size = projects.size();
		
		if (size >= 7) {
			return true;
		}
		return false;
		
	}
	
	/**
	 * Get Projects Active and Completed Of User
	 * 
	 * @param userId
	 * @return
	 */
	public List<Project> getProjectsActiveAndCompletedOfUser(Integer userId) {
		
		List<Project> projects = projectDAO.getProjectsActiveAndCompletedByUserId(userId);
		
		return projects;
	}
	
	/**
	 * Delete Completely Project
	 * 
	 * @param projectId
	 * @return
	 */
	public ProjectDTO deleteCompletelyProject(Integer projectId) {
		
		Optional<Project> project = projectDAO.findById(projectId);
		if (project.isEmpty()) {
			return null;
		}
		
		projectDAO.delete(project.get());
		
		return new ProjectDTO(project.get());
	}
	
	/**
	 * Mark Completed Project
	 * 
	 * @param projectId
	 * @return
	 */
	public ProjectDTO markCompleted(Integer projectId) {
		
		Optional<Project> projectOption = projectDAO.findByIdAndStatus(projectId, EnumStatus.ACTIVE.getValue());
		if (projectOption.isEmpty()) {
			return null;
		}
		Project project = projectOption.get();
		List<Work> works = project.getWorks();
		
		if (works != null) {
			works.stream()
				.forEach(w -> {
					if (w.getStatus().equals(EnumStatus.ACTIVE.getValue())) {
						workService.markCompleted(w.getId());
					}
				});
		}
		
		project.setStatus(EnumStatus.COMPLETED.getValue());
		project.setEndTime(new Date());		
		project = projectDAO.save(project);
		
		return new ProjectDTO(project);
	}
	
	/**
	 * Recover Project
	 * 
	 * @param projectId
	 * @return
	 */
	public ProjectDTO recover(Integer projectId) {
		Optional<Project> projectOptional = projectDAO.findById(projectId);
		if (projectOptional.isEmpty()) {
			return null;
		}
		Project project = projectOptional.get();
		List<Work> works = project.getWorks() == null ? new ArrayList<>() : project.getWorks();
		
		if (project.getStatus().equals(EnumStatus.COMPLETED.getValue())) {
			project.setStatus(EnumStatus.ACTIVE.getValue());
			project = projectDAO.save(project);
						
			works.stream()
				.forEach(w -> {
					if (w.getStatus().equals(EnumStatus.COMPLETED.getValue())) {
						workService.recover(w.getId());
					}
				});					
		} else if (project.getStatus().equals(EnumStatus.DELETED.getValue())) {
			project.setStatus(project.getOldStatus() == null ? EnumStatus.ACTIVE.getValue() : project.getOldStatus());
			project.setOldStatus(null);
			if (project.getFolder() != null && project.getFolder().getStatus().equals(EnumStatus.DELETED.getValue())) {
				project.setFolder(null);
			}
			project = projectDAO.save(project);
			works.stream()
				.forEach(w -> {
					if (w.getStatus().equals(EnumStatus.DELETED.getValue())) {
						workService.recover(w.getId());
					}
				});
		}
		
		return new ProjectDTO(project);
	}	
	
	/**
	 * Searh Project By Name
	 * 
	 * @param keySearch
	 * @return
	 */
	public List<ProjectDTO> searchByName(String keySearch, Integer userId) {
		List<Project> projects = new ArrayList<>();
		if (StringUtils.isEmpty(keySearch)) {
			projects = projectDAO.findByUserIdAndStatus(userId, EnumStatus.ACTIVE.getValue());
		} else {
			projects = projectDAO.findByProjectNameContainingAndUserIdAndStatus(keySearch, userId, EnumStatus.ACTIVE.getValue());
		}
		return projects.stream()
					.map(p -> new ProjectDTO(p))
					.collect(Collectors.toList());
	}
	
	/**
	 * Delete All Project Of User
	 * 
	 * @param user
	 */
	@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
	public void deleteAllProjectOfUser(User user) {
		projectDAO.deleteByUser(user);
	}
	
	/**
	 * Get Projects completed of User
	 * 
	 * @param userId
	 * @return
	 */
	public List<ProjectGroupByDateDTO> getProjectsCompletedOfUser(Integer userId) {
		List<ProjectDTO> projectsDb = getProjectsOfUserByStatus(userId, EnumStatus.COMPLETED.getValue());
		
		Map<Long, List<ProjectDTO>> mapProject = projectsDb.stream()				
				.collect(Collectors.groupingBy(p -> { 
						return DateUtils.setTimeOfDateToMidnight(p.getEndTime()).getTime();
					}, Collectors.toList()));
		mapProject.values().forEach(list -> list.sort(Comparator.comparing(ProjectDTO::getEndTime).reversed()));
		List<ProjectGroupByDateDTO> projects = new ArrayList<>();
		for (Map.Entry<Long, List<ProjectDTO>> entry : mapProject.entrySet()) {
			projects.add(new ProjectGroupByDateDTO(entry.getKey(), entry.getValue()));
		}
		return projects;
	}
	
	/**
	 * Delete Complete All Projects of User
	 * 
	 * @param userId
	 * @return
	 */
	public Boolean deleteCompletelyAllProjectsOfUser(Integer userId) {
		threadService.deleteAllProjectsOfUser(userId);
		return true;
	}

}
