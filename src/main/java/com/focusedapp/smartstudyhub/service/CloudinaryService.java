package com.focusedapp.smartstudyhub.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.focusedapp.smartstudyhub.dao.FilesDAO;
import com.focusedapp.smartstudyhub.exception.ISException;
import com.focusedapp.smartstudyhub.exception.NotFoundValueException;
import com.focusedapp.smartstudyhub.model.Files;
import com.focusedapp.smartstudyhub.model.User;
import com.focusedapp.smartstudyhub.model.custom.FilesDTO;
import com.focusedapp.smartstudyhub.util.constant.ConstantUrl;
import com.focusedapp.smartstudyhub.util.enumerate.EnumStatusFile;
import com.focusedapp.smartstudyhub.util.enumerate.EnumTypeFile;
import com.focusedapp.smartstudyhub.util.enumerate.EnumZoneId;

@Service
public class CloudinaryService {

	@Value("${cloudinary.cloud-name}")
	private String cloudName;

	@Value("${cloudinary.api-key}")
	private String apiKey;

	@Value("${cloudinary.api-secret}")
	private String apiSecret;

	@Autowired
	private FilesDAO filesDAO;
	
	@Autowired
	UserService userService;

	/**
	 * Upload file User
	 * 
	 * @param file
	 * @param type
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public String uploadFile(MultipartFile file, String type, User user) throws IOException {

		Cloudinary cloudinary = new Cloudinary(
				ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));

		String folder = ConstantUrl.URL_FOLDER + "/" + type + "/";
		String resourceType = "image";
		if (type.equals(EnumTypeFile.SOUNDCONCENTRATION.getValue()) 
				|| type.equals(EnumTypeFile.SOUNDDONE.getValue())) {
			resourceType = "video";
		}

		// Set the transformation if needed (e.g., resizing, cropping, etc.)
		// Transformation transformation = new
		// Transformation().width(300).height(300).crop("fill");

		// Get Extension of file
		String extension = file.getOriginalFilename().split("\\.")[1].toLowerCase();

		Files fileSave = Files.builder()
				.user(user)
				.build();
		filesDAO.save(fileSave);
		// Set the new file name in Cloudinary
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
		formatter.setTimeZone(TimeZone.getTimeZone(ZoneId.of(EnumZoneId.ASIA_HOCHIMINH.getNameZone())));
		String newFileName = "File_" + user.getId() + "_" + fileSave.getId() + "_" + formatter.format(new Date()) + 
				"_" + extension;
		
		String publicId = newFileName;

		// Upload the image to Cloudinary
		Map uploadResult;
		try {
			uploadResult = cloudinary.uploader().upload(file.getBytes(),
					ObjectUtils.asMap("folder", folder, "public_id", publicId, "resource_type", resourceType));
		} catch (Exception e) {
			filesDAO.delete(fileSave);
			throw e;
		}
//		Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
//				ObjectUtils.asMap("folder", folder, "public_id", publicId, "resource_type", resourceType));

		fileSave = Files.builder().folder(folder)
				.id(fileSave.getId())
				.type(type)
				.fileName(newFileName).format((String) uploadResult.get("format"))
				.user(user)
				.resourceType((String) uploadResult.get("resource_type"))
				.secureUrl((String) uploadResult.get("secure_url"))
				.createdAt(new Date())
				.publicId((String) uploadResult.get("public_id"))
				.build();
		fileSave = filesDAO.save(fileSave);
		
		return fileSave.getSecureUrl();
	}
	
	/**
	 * Delete File in cloudinary
	 * 
	 * @param publicId
	 * @return
	 * @throws IOException
	 */
	public FilesDTO deleteFileInCloudinary(String publicId) throws IOException {
	
		Cloudinary cloudinary = new Cloudinary(
				ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
		String resourceType = "image"; 
		if (publicId.split("/")[1].equals(EnumTypeFile.SOUNDCONCENTRATION.getValue()) 
				|| publicId.split("/")[1].equals(EnumTypeFile.SOUNDDONE.getValue())) {
			resourceType = "video"; 
		}
		Files file = filesDAO.findByPublicId(publicId)
				.orElseThrow(() -> new NotFoundValueException("Not Found the file to delete!", "CloudinaryService -> deleteFileInCloudinary"));

		// Upload the image to Cloudinary
		cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));

		filesDAO.delete(file);
		
		return new FilesDTO(file);
	}
	
	/**
	 * Upload File User Guest
	 * 
	 * @param file
	 * @param type
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	public String uploadFileUserGuest(MultipartFile file, String type, Integer userId) throws IOException {
		
		// Because User Account that is Deleted or Banned still can upload file to Report
		User user = userService.findById(userId);
		return uploadFile(file, type, user);
	}
	
	/**
	 * Upload file User
	 * 
	 * @param file
	 * @param type
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public String uploadFileAdmin(MultipartFile file, String statusFile, String type) throws IOException {

		Cloudinary cloudinary = new Cloudinary(
				ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));

		String folder = ConstantUrl.URL_FOLDER + "/" + type + "/" + statusFile + "/";
		String resourceType = "image";
		if (type.equals(EnumTypeFile.SOUNDCONCENTRATION.getValue()) 
				|| type.equals(EnumTypeFile.SOUNDDONE.getValue())) {
			resourceType = "video";
		}

		// Get Extension of file
		String extension = file.getOriginalFilename().split("\\.")[1].toLowerCase();

		Files fileSave = Files.builder()
				.build();
		filesDAO.save(fileSave);
		// Set the new file name in Cloudinary
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
		formatter.setTimeZone(TimeZone.getTimeZone(ZoneId.of(EnumZoneId.ASIA_HOCHIMINH.getNameZone())));
		String newFileName = "File_ADMINUPDATED_" + fileSave.getId() + "_" + formatter.format(new Date()) + 
				"_" + extension;
		
		String publicId = newFileName;

		// Upload the image to Cloudinary
		Map uploadResult;
		try {
			uploadResult = cloudinary.uploader().upload(file.getBytes(),
					ObjectUtils.asMap("folder", folder, "public_id", publicId, "resource_type", resourceType));
		} catch (Exception e) {
			filesDAO.delete(fileSave);
			throw e;
		}

		fileSave = Files.builder().folder(folder)
				.id(fileSave.getId())
				.type(type)
				.fileName(newFileName).format((String) uploadResult.get("format"))
				.resourceType((String) uploadResult.get("resource_type"))
				.secureUrl((String) uploadResult.get("secure_url"))
				.createdAt(new Date())
				.publicId((String) uploadResult.get("public_id"))
				.build();
		fileSave = filesDAO.save(fileSave);
		
		return fileSave.getSecureUrl();
	}
	
	public String moveFileToAnotherFolder(String currentPublicId, String newPublicId) {
		Cloudinary cloudinary = new Cloudinary(
				ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
		String resourceType = "image"; 
		if (currentPublicId.split("/")[1].equals(EnumTypeFile.SOUNDCONCENTRATION.getValue()) 
				|| currentPublicId.split("/")[1].equals(EnumTypeFile.SOUNDDONE.getValue())) {
			resourceType = "video"; 
		}
		Map uploadResult;
		try {
			uploadResult = cloudinary.uploader().rename(currentPublicId, newPublicId, 
					ObjectUtils.asMap("resource_type", resourceType));
		} catch (Exception e) {
			throw new ISException(e);
		}
		String[] tokensNewPublicId = newPublicId.split("/");
		String folder = "/".concat(tokensNewPublicId[0])
				.concat("/").concat(tokensNewPublicId[1])
				.concat("/").concat(tokensNewPublicId[2])
				.concat("/");
		Files files = filesDAO.findByPublicId(currentPublicId).get();
		files.setPublicId((String) uploadResult.get("public_id"));
		files.setSecureUrl((String) uploadResult.get("secure_url"));
		files.setFormat((String) uploadResult.get("format"));
		files.setResourceType((String) uploadResult.get("resource_type"));
		files.setFolder(folder);
		files.setCreatedAt(new Date());
		filesDAO.save(files);
		return (String) uploadResult.get("secure_url");
	}
	
}
