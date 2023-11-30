package com.focusedapp.smartstudyhub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.focusedapp.smartstudyhub.model.custom.ProjectDTO;
import com.focusedapp.smartstudyhub.model.custom.Result;
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
	
}
