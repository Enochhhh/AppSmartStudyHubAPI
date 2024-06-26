package com.focusedapp.smartstudyhub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

import com.focusedapp.smartstudyhub.model.Folder;
import com.focusedapp.smartstudyhub.model.custom.AllResponseTypeDTO;
import com.focusedapp.smartstudyhub.model.custom.FolderDTO;
import com.focusedapp.smartstudyhub.model.custom.Result;
import com.focusedapp.smartstudyhub.model.custom.UserDTO;
import com.focusedapp.smartstudyhub.service.FolderService;
import com.focusedapp.smartstudyhub.util.enumerate.StatusCode;

@RestController
@RequestMapping("/mobile/v1/user/guest/folder")
@CrossOrigin(origins ="*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT})
public class FolderController extends BaseController {
	
	@Autowired
	FolderService folderService;
	
	/**
	 * API Create new folder
	 * 
	 * @param folderDTO
	 * @return
	 */
	@PostMapping("/create")
	public ResponseEntity<Result<FolderDTO>> createFolder(@RequestBody FolderDTO folderDTO) {
		Result<FolderDTO> result = new Result<>();
		
		FolderDTO folderCreated = folderService.createFolder(folderDTO);
		
		result.setData(folderCreated);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get folder of user
	 * 
	 * @param userId
	 * @return
	 */
	@GetMapping("/get-by-user")
	public ResponseEntity<Result<List<FolderDTO>>> getAllFolderOfUser(@RequestParam Integer userId) {
		Result<List<FolderDTO>> result = new Result<>();
		
		if (userId == null || userId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("userId Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		List<FolderDTO> folderCreated = folderService.getAllFolderOfUser(userId);
		
		result.setData(folderCreated);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get deleted folder of user
	 * 
	 * @param userId
	 * @return
	 */
	@GetMapping("/get-deleted-by-user")
	public ResponseEntity<Result<List<FolderDTO>>> getAllFolderDeletedOfUser(@RequestParam Integer userId) {
		Result<List<FolderDTO>> result = new Result<>();
		
		if (userId == null || userId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("userId Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		List<FolderDTO> folderDeleted = folderService.getAllFolderDeletedOfUser(userId);
		
		result.setData(folderDeleted);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Update Folder of user
	 * 
	 * @param folderDTO
	 * @return
	 */
	@PutMapping("/update")
	public ResponseEntity<Result<FolderDTO>> updateFolder(@RequestBody FolderDTO folderDTO) {
		Result<FolderDTO> result = new Result<>();
		
		if (folderDTO == null) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Folder Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		FolderDTO folderCreated = folderService.updateFolder(folderDTO);
		
		result.setData(folderCreated);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Delete Folder of user
	 * 
	 * @param folderDTO
	 * @return
	 */
	@DeleteMapping("/delete/{folderId}")
	public ResponseEntity<Result<FolderDTO>> deleteFolder(@PathVariable Integer folderId) {
		Result<FolderDTO> result = new Result<>();
		
		if (folderId == null || folderId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Folder Id Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		FolderDTO folderCreated = folderService.deleteFolder(folderId);
		
		if (folderCreated == null) {
			result.getMeta().setStatusCode(StatusCode.FAIL.getCode());
			result.getMeta().setMessage(StatusCode.FAIL.getMessage());
			return createResponseEntity(result, HttpStatus.FORBIDDEN);
		}
		
		result.setData(folderCreated);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get Detail Information of folder
	 * 
	 * @param folderId
	 * @return
	 */
	@GetMapping("/get-detail")
	public ResponseEntity<Result<FolderDTO>> getDetailFolder(@RequestParam Integer folderId) {
		Result<FolderDTO> result = new Result<>();
		
		Folder folder = folderService.findById(folderId);
		
		if (folder == null) {
			result.getMeta().setStatusCode(StatusCode.FAIL.getCode());
			result.getMeta().setMessage(StatusCode.FAIL.getMessage());
			return createResponseEntity(result, HttpStatus.FORBIDDEN);
		}
		
		result.setData(new FolderDTO(folder));
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Check Maximum number of Folder
	 * 
	 * @param userRequest
	 * @return
	 */
	@PostMapping("/check-maximum-folder")
	public ResponseEntity<Result<AllResponseTypeDTO>> checkMaximumFolder(@RequestBody UserDTO userRequest) {
		Result<AllResponseTypeDTO> result = new Result<>();
		
		if (userRequest == null || userRequest.getId() == null || userRequest.getId() < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Request Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		Boolean isMaximum = folderService.checkMaximumFolder(userRequest.getId());
		
		result.setData(AllResponseTypeDTO.builder()
				.booleanType(isMaximum).build());
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Delete Completyly Folder Controller
	 * 
	 * @param folderId
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
	@DeleteMapping("/delete-completely/{folderId}")
	public ResponseEntity<Result<FolderDTO>> deleteCompletelyFolder(@PathVariable Integer folderId) {
		Result<FolderDTO> result = new Result<>();
		
		if (folderId == null || folderId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Request Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		FolderDTO data = folderService.deleteCompletelyFolder(folderId);
		
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
	
	/**
	 * Recover Folder Controller
	 * 
	 * @param folderId
	 * @return
	 */
	@PutMapping("/recover/{folderId}")
	public ResponseEntity<Result<FolderDTO>> recover(@PathVariable Integer folderId) {
		Result<FolderDTO> result = new Result<>();
		
		if (folderId == null || folderId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Folder Id Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		FolderDTO folderRecovered = folderService.recover(folderId);
		
		if (folderRecovered == null) {
			result.getMeta().setStatusCode(StatusCode.RECOVER_FOLDER_FAILURE.getCode());
			result.getMeta().setMessage(StatusCode.RECOVER_FOLDER_FAILURE.getMessage());
			return createResponseEntity(result);
		}
		
		result.setData(folderRecovered);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Find Folder by name
	 * 
	 * @param projectId
	 * @return
	 */
	@PostMapping("/search-by-name")
	public ResponseEntity<Result<List<FolderDTO>>> findByName(@RequestBody AllResponseTypeDTO req) {
		Result<List<FolderDTO>> result = new Result<>();
		
		if (req == null || req.getStringType() == null) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);							
		}
		
		List<FolderDTO> folders = folderService.searchByName(req.getStringType(), req.getIntegerType());
		
		result.setData(folders);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Delete Completyly All Folders of User Controller
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
	@DeleteMapping("/delete-completely-all/{userId}")
	public ResponseEntity<Result<AllResponseTypeDTO>> deleteCompletelyAllFolderOfUser(@PathVariable Integer userId) {
		Result<AllResponseTypeDTO> result = new Result<>();
		
		if (userId == null || userId < 1) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Request Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		
		Boolean isDeleted = folderService.deleteCompletelyAllFolderOfUser(userId);
		AllResponseTypeDTO data = new AllResponseTypeDTO();
		data.setBooleanType(isDeleted);
		data.setStringType("Deleted Successfully!");
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		if (!isDeleted) {
			result.getMeta().setStatusCode(StatusCode.DELETE_ALL_FOLDERS_COMPLETELY_FAILURE.getCode());
			result.getMeta().setMessage(StatusCode.DELETE_ALL_FOLDERS_COMPLETELY_FAILURE.getMessage());
			data.setStringType("Not found any folder to delete!");
		}
		
		result.setData(data);
		return createResponseEntity(result);
	}
	
}
