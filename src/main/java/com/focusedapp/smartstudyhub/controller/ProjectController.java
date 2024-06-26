package com.focusedapp.smartstudyhub.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.focusedapp.smartstudyhub.model.Project;
import com.focusedapp.smartstudyhub.model.custom.AllResponseTypeDTO;
import com.focusedapp.smartstudyhub.model.custom.ProjectDTO;
import com.focusedapp.smartstudyhub.model.custom.ProjectGroupByDateDTO;
import com.focusedapp.smartstudyhub.model.custom.Result;
import com.focusedapp.smartstudyhub.model.custom.UserDTO;
import com.focusedapp.smartstudyhub.service.ProjectService;
import com.focusedapp.smartstudyhub.util.enumerate.StatusCode;

@RestController
@RequestMapping("/mobile/v1/user/guest/project")
@CrossOrigin(origins ="*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT})
public class ProjectController extends BaseController {

	@Autowired
	ProjectService projectService;
	
	/**
	 * Create Project
	 * 
	 * @param projectDTO
	 * @return
	 */
	@PostMapping("/create")
	public ResponseEntity<Result<ProjectDTO>> createProject(@RequestBody ProjectDTO projectDTO) {
		Result<ProjectDTO> result = new Result<>();
		
		ProjectDTO projectCreated = projectService.createProject(projectDTO);
		
		result.setData(projectCreated);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get projects of User by userId
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	@GetMapping("/get-by-user-status")
	public ResponseEntity<Result<List<ProjectDTO>>> getProjectsOfUserByUserIdAndStatus(@RequestParam Integer userId, 
			@RequestParam String status) {
		Result<List<ProjectDTO>> result = new Result<>();
		
		if (userId == null || userId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("userId Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		List<ProjectDTO> projectCreated = projectService.getProjectsOfUserByStatus(userId, status);
		
		result.setData(projectCreated);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get Projects Of User by Folder and status
	 * 
	 * @param userId
	 * @param folderId
	 * @param status
	 * @return
	 */
	@GetMapping("/get-by-user-folder-status")
	public ResponseEntity<Result<List<ProjectDTO>>> getProjectsOfUserByFolderAndSatus(@RequestParam Integer userId, @RequestParam Integer folderId,
			@RequestParam String status) {
		Result<List<ProjectDTO>> result = new Result<>();
		
		if (userId == null || userId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("userId Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		List<ProjectDTO> projectCreated = projectService.getProjectsOfUserByFolderAndStatus(userId, folderId, status);
		
		result.setData(projectCreated);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get Project for updating Folder
	 * 
	 * @param userId
	 * @param folderId
	 * @return
	 */
	@GetMapping("/get-for-updating-folder")
	public ResponseEntity<Result<List<ProjectDTO>>> getProjectsForUpdatingFolder(@RequestParam Integer userId, @RequestParam Integer folderId) {
		Result<List<ProjectDTO>> result = new Result<>();
		
		if (userId == null || userId < 1 || folderId == null || folderId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails(" UserId or FolderId Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		List<ProjectDTO> projectCreated = projectService.getProjectsForUpdatingFolder(userId, folderId);
		
		result.setData(projectCreated);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get Detail Information of Project
	 * 
	 * @param projectId
	 * @return
	 */
	@GetMapping("/get-detail")
	public ResponseEntity<Result<ProjectDTO>> getDetailProject(@RequestParam Integer projectId) {
		Result<ProjectDTO> result = new Result<>();
		
		Project project = projectService.findById(projectId);
		
		if (project == null) {
			result.getMeta().setStatusCode(StatusCode.FAIL.getCode());
			result.getMeta().setMessage(StatusCode.FAIL.getMessage());
			return createResponseEntity(result);
		}
		
		result.setData(new ProjectDTO(project));
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Update Project Information
	 * 
	 * @param projectRequest
	 * @return
	 */
	@PutMapping("/update")
	public ResponseEntity<Result<ProjectDTO>> updateProject(@RequestBody ProjectDTO projectRequest) {
		Result<ProjectDTO> result = new Result<>();
		
		Project project = projectService.updateProject(projectRequest);
		
		if (project == null) {
			result.getMeta().setStatusCode(StatusCode.FAIL.getCode());
			result.getMeta().setMessage(StatusCode.FAIL.getMessage());
			return createResponseEntity(result, HttpStatus.FORBIDDEN);
		}
		
		result.setData(new ProjectDTO(project));
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Delete Project
	 * 
	 * @param projectId
	 * @return
	 */
	@DeleteMapping("/delete/{projectId}")
	public ResponseEntity<Result<ProjectDTO>> deleteProject(@PathVariable Integer projectId) {
		Result<ProjectDTO> result = new Result<>();
		
		if (projectId == null || projectId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		Project project = projectService.deleteProject(projectId);
		
		if (project == null) {
			result.getMeta().setStatusCode(StatusCode.MARK_DELETED_PROJECT_FAILURE.getCode());
			result.getMeta().setMessage(StatusCode.MARK_DELETED_PROJECT_FAILURE.getMessage());
			return createResponseEntity(result, HttpStatus.FORBIDDEN);
		}
		
		result.setData(new ProjectDTO(project));
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get Projects for adding folder
	 * 
	 * @return
	 */
	@GetMapping("/get-for-add-folder")
	public ResponseEntity<Result<List<ProjectDTO>>> getProjectForAddingFolder(@RequestParam Integer userId) {
		Result<List<ProjectDTO>> result = new Result<>();
		
		List<ProjectDTO> projects = projectService.getProjectsForAddingFolder(userId);
		
		if (CollectionUtils.isEmpty(projects)) {
			result.getMeta().setStatusCode(StatusCode.FAIL.getCode());
			result.getMeta().setMessage(StatusCode.FAIL.getMessage());
			return createResponseEntity(result, HttpStatus.FORBIDDEN);
		}
		
		result.setData(projects);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	@PostMapping("/check-maximum-project")
	public ResponseEntity<Result<AllResponseTypeDTO>> checkMaximumFolder(@RequestBody UserDTO userRequest) {
		Result<AllResponseTypeDTO> result = new Result<>();
		
		if (userRequest == null || userRequest.getId() == null || userRequest.getId() < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Request Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		Boolean isMaximum = projectService.checkMaximumProject(userRequest.getId());
		
		result.setData(AllResponseTypeDTO.builder()
				.booleanType(isMaximum).build());
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get projects of User by userId
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	@GetMapping("/get-active-and-completed")
	public ResponseEntity<Result<List<ProjectDTO>>> getProjectsActiveAndCompletedOfUser(@RequestParam Integer userId) {
		Result<List<ProjectDTO>> result = new Result<>();
		
		if (userId == null || userId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("userId Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		List<Project> projects = projectService.getProjectsActiveAndCompletedOfUser(userId);
		
		try {
			List<ProjectDTO> dtos = projects.stream()
					.map(proj -> new ProjectDTO(proj))
					.collect(Collectors.toList());
			result.setData(dtos);
		} catch (Exception e) {
			throw e;
		}
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Delete Completyly Project Controller
	 * 
	 * @param projectId
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
	@DeleteMapping("/delete-completely/{projectId}")
	public ResponseEntity<Result<ProjectDTO>> deleteCompletelyFolder(@PathVariable Integer projectId) {
		Result<ProjectDTO> result = new Result<>();
		
		if (projectId == null || projectId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Request Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		ProjectDTO data = projectService.deleteCompletelyProject(projectId);
		
		if (data == null) {
			result.getMeta().setStatusCode(StatusCode.DELETE_FOLDER_COMPLETELY_FAILURE.getCode());
			result.getMeta().setMessage(StatusCode.DELETE_FOLDER_COMPLETELY_FAILURE.getMessage());
			return createResponseEntity(result, HttpStatus.FORBIDDEN);
		}
		
		result.setData(data);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	@PutMapping("/mark-completed/{projectId}")
	public ResponseEntity<Result<ProjectDTO>> markCompleted(@PathVariable Integer projectId) {
		Result<ProjectDTO> result = new Result<>();
		
		if (projectId == null || projectId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Project Id Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		ProjectDTO projectMarked = projectService.markCompleted(projectId);
		
		if (projectMarked == null) {
			result.getMeta().setStatusCode(StatusCode.MARK_COMPLETED_PROJECT_FAILURE.getCode());
			result.getMeta().setMessage(StatusCode.MARK_COMPLETED_PROJECT_FAILURE.getMessage());
			return createResponseEntity(result, HttpStatus.FORBIDDEN);
		}
		
		result.setData(projectMarked);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Recover Project
	 * 
	 * @param extraWorkId
	 * @return
	 */
	@PutMapping("/recover/{projectId}")
	public ResponseEntity<Result<ProjectDTO>> recover(@PathVariable Integer projectId) {
		Result<ProjectDTO> result = new Result<>();
		
		if (projectId == null || projectId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Project Id Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		ProjectDTO projectRecovered = projectService.recover(projectId);
		
		if (projectRecovered == null) {
			result.getMeta().setStatusCode(StatusCode.RECOVER_PROJECT_FAILURE.getCode());
			result.getMeta().setMessage(StatusCode.RECOVER_PROJECT_FAILURE.getMessage());
			return createResponseEntity(result, HttpStatus.FORBIDDEN);
		}
		
		result.setData(projectRecovered);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Search Project by name
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping("/search-by-name")
	public ResponseEntity<Result<List<ProjectDTO>>> findByName(@RequestBody AllResponseTypeDTO req) {
		Result<List<ProjectDTO>> result = new Result<>();
		
		if (req == null || req.getStringType() == null) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);							
		}
		
		List<ProjectDTO> projects = projectService.searchByName(req.getStringType(), req.getIntegerType());
		
		result.setData(projects);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get Projects completed of User
	 * 
	 * @param userId
	 * @return
	 */
	@GetMapping("/get-project-completed")
	public ResponseEntity<Result<List<ProjectGroupByDateDTO>>> getProjectsCompletedOfUser(@RequestParam Integer userId) {
		Result<List<ProjectGroupByDateDTO>> result = new Result<>();
		
		if (userId == null || userId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Invalid !");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		List<ProjectGroupByDateDTO> projects = projectService.getProjectsCompletedOfUser(userId);
		
		result.setData(projects);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Delete Completyly All Projects of User Controller
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
	@DeleteMapping("/delete-completely-all/{userId}")
	public ResponseEntity<Result<AllResponseTypeDTO>> deleteCompletelyAllProjectsOfUser(@PathVariable Integer userId) {
		Result<AllResponseTypeDTO> result = new Result<>();
		
		if (userId == null || userId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Request Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		Boolean isDeleted = projectService.deleteCompletelyAllProjectsOfUser(userId);
		AllResponseTypeDTO data = new AllResponseTypeDTO();
		data.setBooleanType(isDeleted);
		data.setStringType("Deleted Successfully!");
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		if (!isDeleted) {
			result.getMeta().setStatusCode(StatusCode.DELETE_ALL_PROJECTS_COMPLETELY_FAILURE.getCode());
			result.getMeta().setMessage(StatusCode.DELETE_ALL_PROJECTS_COMPLETELY_FAILURE.getMessage());
			data.setStringType("Not found any project to delete!");
		}
		
		result.setData(data);
		return createResponseEntity(result);
	}
	
}
