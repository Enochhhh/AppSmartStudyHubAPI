package com.focusedapp.smartstudyhub.controller.customer;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.focusedapp.smartstudyhub.controller.BaseController;
import com.focusedapp.smartstudyhub.model.Files;
import com.focusedapp.smartstudyhub.model.User;
import com.focusedapp.smartstudyhub.model.custom.AllResponseTypeDTO;
import com.focusedapp.smartstudyhub.model.custom.FilesDTO;
import com.focusedapp.smartstudyhub.model.custom.Result;
import com.focusedapp.smartstudyhub.service.CloudinaryService;
import com.focusedapp.smartstudyhub.service.FilesService;
import com.focusedapp.smartstudyhub.util.enumerate.EnumStatusFile;
import com.focusedapp.smartstudyhub.util.enumerate.EnumTypeFile;
import com.focusedapp.smartstudyhub.util.enumerate.StatusCode;
import com.nimbusds.oauth2.sdk.util.StringUtils;

@RestController
@RequestMapping("/mobile/v1/user")
@CrossOrigin(origins = "*", methods = { RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE,
		RequestMethod.PUT })
public class FilesController extends BaseController {

	@Autowired
	CloudinaryService cloudinaryService;
	
	@Autowired
	FilesService filesService;

	/**
	 * Upload file user
	 * 
	 * @param files
	 * @param type
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/customer/files/upload")
	public ResponseEntity<Result<AllResponseTypeDTO>> uploadFile(@RequestParam("files") MultipartFile files,
			@RequestParam String type) throws IOException {

		Result<AllResponseTypeDTO> result = new Result<>();
		
		if (files == null || files.isEmpty()) {
			result.getMeta().setStatusCode(StatusCode.MISSING_FILE.getCode());
			result.getMeta().setMessage(StatusCode.MISSING_FILE.getMessage());
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
        }

		User user = getAuthenticatedUser(); 
		String filesUploaded = cloudinaryService.uploadFile(files, type, user);
		AllResponseTypeDTO allResponseTypeDTO = new AllResponseTypeDTO();
		allResponseTypeDTO.setStringType(filesUploaded);

		result.setData(allResponseTypeDTO);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}

	/**
	 * Delete File
	 * 
	 * @param fileDelete
	 * @return
	 * @throws IOException
	 */
	@DeleteMapping("/customer/files/delete")
	public ResponseEntity<Result<FilesDTO>> deleteFile(@RequestBody Files fileDelete) throws IOException {

		Result<FilesDTO> result = new Result<>();

		FilesDTO filesDeleted = cloudinaryService.deleteFileInCloudinary(fileDelete.getPublicId());

		result.setData(filesDeleted);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Get Files uploaded of User
	 * 
	 * @param type
	 * @return
	 */
	@GetMapping("/customer/files/get-files-uploaded-user")
	public ResponseEntity<Result<List<FilesDTO>>> getImageUploadedOfUser(@RequestParam String type) {

		Result<List<FilesDTO>> result = new Result<>();

		User user = getAuthenticatedUser();
		List<FilesDTO> filesUploaded = filesService.getFilesUploadedOfUser(user, type);

		result.setData(filesUploaded);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Delete Completely Files by Id
	 * 
	 * @param userInfo
	 * @return
	 */
	@DeleteMapping("/customer/files/delete-completely-files-by-id/{filesId}")
	public ResponseEntity<Result<FilesDTO>> deleteCompletelyFilesById(@PathVariable Integer filesId) 
			throws IOException {
		
		Result<FilesDTO> result = new Result<>();
		
		User user = getAuthenticatedUser();
		
		FilesDTO filesDeleted = filesService.deleteCompletelyFilesById(user, filesId);	
		
		result.setData(filesDeleted);		
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Upload file user Guest
	 * 
	 * @param files
	 * @param type
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/guest/files/upload")
	public ResponseEntity<Result<AllResponseTypeDTO>> uploadFile(@RequestParam("files") MultipartFile files,
			@RequestParam String type, @RequestParam Integer userId) throws IOException {

		Result<AllResponseTypeDTO> result = new Result<>();

		String filesUploaded = cloudinaryService.uploadFileUserGuest(files, type, userId);
		AllResponseTypeDTO allResponseTypeDTO = new AllResponseTypeDTO();
		allResponseTypeDTO.setStringType(filesUploaded);

		result.setData(allResponseTypeDTO);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
	
	/**
	 * Delete Completyly All avatars or cover images of User Controller
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
	@DeleteMapping("/customer/files/delete-completely-all")
	public ResponseEntity<Result<AllResponseTypeDTO>> deleteCompletelyAllAvatarsOrCoverImagesOfUser(@RequestParam String type) {
		Result<AllResponseTypeDTO> result = new Result<>();
		
		if (StringUtils.isBlank(type) 
				|| (!type.equals(EnumTypeFile.COVERIMAGE.getValue()) && !type.equals(EnumTypeFile.USER.getValue()))) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Request Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		User user = getAuthenticatedUser();
		Boolean isDeleted = filesService.deleteAllFilesOfUserByTypeUsingThread(user, type);
		AllResponseTypeDTO data = new AllResponseTypeDTO();
		data.setBooleanType(isDeleted);
		data.setStringType("Deleted Successfully!");
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		
		result.setData(data);
		return createResponseEntity(result);
	}
	
	/**
	 * Delete Completyly All Themes or sounds of User Controller
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
	@DeleteMapping("/premium/files/delete-completely-all")
	public ResponseEntity<Result<AllResponseTypeDTO>> deleteCompletelyAllThemesOrSoundsOfUser(@RequestParam String type) {
		Result<AllResponseTypeDTO> result = new Result<>();
		
		if (StringUtils.isBlank(type)
				|| (!type.equals(EnumTypeFile.THEME.getValue())
				&& !type.equals(EnumTypeFile.SOUNDCONCENTRATION.getValue())
				&& !type.equals(EnumTypeFile.SOUNDDONE.getValue()))) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Request Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}
		User user = getAuthenticatedUser();
		Boolean isDeleted = filesService.deleteAllThemesOrSoundsOfUserByUsingThread(user, type);
		AllResponseTypeDTO data = new AllResponseTypeDTO();
		data.setBooleanType(isDeleted);
		data.setStringType("Deleted Successfully!");
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		
		result.setData(data);
		return createResponseEntity(result);
	}
	
	/**
	 * Upload file user
	 * 
	 * @param files
	 * @param type
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/admin/files/upload")
	public ResponseEntity<Result<AllResponseTypeDTO>> uploadFileAdmin(@RequestParam("files") MultipartFile files,
			@RequestParam String type, @RequestParam String statusFile) throws IOException {

		Result<AllResponseTypeDTO> result = new Result<>();
		
		if (files == null || files.isEmpty()) {
			result.getMeta().setStatusCode(StatusCode.MISSING_FILE.getCode());
			result.getMeta().setMessage(StatusCode.MISSING_FILE.getMessage());
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
        }
		
		if ((StringUtils.isBlank(type) || (!type.equals(EnumTypeFile.THEME.getValue()) 
				&& !type.equals(EnumTypeFile.SOUNDCONCENTRATION.getValue())
				&& !type.equals(EnumTypeFile.SOUNDDONE.getValue()))) 
				|| (StringUtils.isBlank(statusFile) || (!statusFile.equals(EnumStatusFile.DEFAULT.getValue())
						&& !statusFile.equals(EnumStatusFile.PREMIUM.getValue())))) {
			result.getMeta().setStatusCode(StatusCode.PARAMETER_INVALID.getCode());
			result.getMeta().setMessage(StatusCode.PARAMETER_INVALID.getMessage());
			result.getMeta().setDetails("Data Invalid!");
			return createResponseEntity(result, HttpStatus.BAD_REQUEST);
		}

		String filesUploaded = cloudinaryService.uploadFileAdmin(files, statusFile, type);
		AllResponseTypeDTO allResponseTypeDTO = new AllResponseTypeDTO();
		allResponseTypeDTO.setStringType(filesUploaded);

		result.setData(allResponseTypeDTO);
		result.getMeta().setStatusCode(StatusCode.SUCCESS.getCode());
		result.getMeta().setMessage(StatusCode.SUCCESS.getMessage());
		return createResponseEntity(result);
	}
}
