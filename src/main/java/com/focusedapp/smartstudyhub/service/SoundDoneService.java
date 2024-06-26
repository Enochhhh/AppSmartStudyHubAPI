package com.focusedapp.smartstudyhub.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.focusedapp.smartstudyhub.dao.SoundDoneDAO;
import com.focusedapp.smartstudyhub.exception.NoRightToPerformException;
import com.focusedapp.smartstudyhub.exception.NotFoundValueException;
import com.focusedapp.smartstudyhub.model.SoundDone;
import com.focusedapp.smartstudyhub.model.User;
import com.focusedapp.smartstudyhub.model.custom.SoundDoneDTO;
import com.focusedapp.smartstudyhub.util.constant.ConstantUrl;
import com.focusedapp.smartstudyhub.util.enumerate.EnumStatus;
import com.focusedapp.smartstudyhub.util.enumerate.EnumStatusCustomContent;
import com.focusedapp.smartstudyhub.util.enumerate.EnumTypeFile;

@Service
public class SoundDoneService {

	@Autowired
	SoundDoneDAO soundDoneDAO;
	
	@Autowired 
	CloudinaryService cloudinaryService;
	
	@Autowired
	FilesService filesService;
	
	/**
	 * Get Sound Concentration of Guest
	 * 
	 * @return
	 */
	public List<SoundDoneDTO> getSoundDoneOfGuest() {
		
		List<SoundDone> sounds = soundDoneDAO.findByUserIdIsNullAndStatus(EnumStatus.ACTIVE.getValue());
		
		return sounds.stream()
				.map(s -> new SoundDoneDTO(s))
				.collect(Collectors.toList());
	}
	
	/**
	 * Get Sound Done of Premium User
	 * 
	 * @param user
	 * @return
	 */
	public List<SoundDoneDTO> getSoundDoneOfPremiumUser(User user) {
		
		List<SoundDone> sounds = soundDoneDAO.findByUserIdOrUserIdIsNullAndStatus(user.getId(), EnumStatus.ACTIVE.getValue());
		
		return sounds.stream()
				.map(s -> new SoundDoneDTO(s))
				.collect(Collectors.toList());
	}
	
	/**
	 * Insert Sound Done of Premium User
	 * 
	 * @param soundConcentrationData
	 * @param user
	 * @return
	 */
	public SoundDoneDTO insertSoundDoneOfPremiumUser(SoundDoneDTO soundDoneData, User user) {
		
		SoundDone soundDone = SoundDone.builder()
				.user(user)
				.nameSound(soundDoneData.getNameSound())
				.url(soundDoneData.getUrl())
				.statusSound(EnumStatusCustomContent.OWNED.getValue())
				.status(EnumStatus.ACTIVE.getValue())
				.createdDate(new Date())
				.build();
		
		soundDone = soundDoneDAO.save(soundDone);
		
		return new SoundDoneDTO(soundDone);
	}
	
	/**
	 * Update Sound Done 
	 * 
	 * @param soundDoneData
	 * @return
	 */
	public SoundDoneDTO updateSoundDoneOfPremiumUser(SoundDoneDTO soundDoneData) 
			throws IOException {
		
		SoundDone soundDone = soundDoneDAO.findByIdAndStatus(soundDoneData.getId(), EnumStatus.ACTIVE.getValue())
				.orElseThrow(() -> new NotFoundValueException("Not Found the Sound Done to update!", 
						"SoundDoneService -> updateSoundConcentrationOfPremiumUser"));
		if (soundDone.getUser() == null || !soundDone.getStatusSound().equals(EnumStatusCustomContent.OWNED.getValue())) {
			throw new NoRightToPerformException("No right to perform exception!", 
					"SoundDoneService -> updateSoundConcentrationOfPremiumUser");
		}
		soundDone.setNameSound(soundDoneData.getNameSound());
		if (!soundDoneData.getUrl().equals(soundDone.getUrl())) {
			String publicId = null;
			if (StringUtils.isNotBlank(soundDone.getUrl())) {
				Integer startingIndex = soundDone.getUrl().indexOf(ConstantUrl.URL_FOLDER);
				publicId = soundDone.getUrl().substring(startingIndex + 1).split("\\.")[0];
			}
			
			if (publicId != null) {
				cloudinaryService.deleteFileInCloudinary(publicId);
			}
			soundDone.setUrl(soundDoneData.getUrl());
		}
		
		soundDone = soundDoneDAO.save(soundDone);
		
		return new SoundDoneDTO(soundDone);
	}
	
	/**
	 * Mark Deleted Sound Done of Premium User 
	 * 
	 * @param soundDoneId
	 * @return
	 */
	public SoundDoneDTO markDeletedSoundDoneOfPremiumUser(Integer soundDoneId) {
		
		SoundDone soundDone = soundDoneDAO.findById(soundDoneId)
				.orElseThrow(() -> new NotFoundValueException("Not Found the Sound Done to delete!", 
						"SoundDoneService -> markDeletedSoundDoneOfPremiumUser"));
		if (soundDone.getUser() == null || !soundDone.getStatusSound().equals(EnumStatusCustomContent.OWNED.getValue())) {
			throw new NoRightToPerformException("No right to perform exception!", 
					"SoundDoneService -> markDeletedSoundDoneOfPremiumUser");
		}
		soundDone.setStatus(EnumStatus.DELETED.getValue());
		
		soundDone = soundDoneDAO.save(soundDone);
		
		return new SoundDoneDTO(soundDone);
	}
	
	/**
	 * Recover Sound Concentration 
	 * 
	 * @param themeData
	 * @return
	 */
	public SoundDoneDTO recoverSoundDoneOfPremiumUser(Integer soundDoneId) {
		
		SoundDone soundDone = soundDoneDAO.findById(soundDoneId)
				.orElseThrow(() -> new NotFoundValueException("Not Found the Sound Done to recover!", 
						"SoundDoneService -> recoverSoundDoneOfPremiumUser"));
		if (soundDone.getUser() == null || !soundDone.getStatusSound().equals(EnumStatusCustomContent.OWNED.getValue())) {
			throw new NoRightToPerformException("No right to perform exception!", 
					"SoundDoneService -> recoverSoundDoneOfPremiumUser");
		}
		soundDone.setStatus(EnumStatus.ACTIVE.getValue());
		
		soundDone = soundDoneDAO.save(soundDone);
		
		return new SoundDoneDTO(soundDone);
	}
	
	/**
	 * Delete Sound Done 
	 * 
	 * @param soundDoneId
	 * @return
	 */
	public SoundDoneDTO deleteSoundDoneAndFileUploaded(Integer soundDoneId) throws IOException {
		
		SoundDone soundDone = soundDoneDAO.findById(soundDoneId)
				.orElseThrow(() -> new NotFoundValueException("Not Found the Sound Done to delete!", 
						"SoundDoneService -> deleteSoundDoneAndFileUploaded"));
		if (soundDone.getUser() == null || !soundDone.getStatusSound().equals(EnumStatusCustomContent.OWNED.getValue())) {
			throw new NoRightToPerformException("No right to perform exception!", 
					"SoundDoneService -> deleteSoundDoneAndFileUploaded");
		}
		
		String publicId = null;
		if (StringUtils.isNotBlank(soundDone.getUrl())) {
			Integer startingIndex = soundDone.getUrl().indexOf(ConstantUrl.URL_FOLDER);
			publicId = soundDone.getUrl().substring(startingIndex + 1).split("\\.")[0];
		}
		
		if (publicId != null) {
			cloudinaryService.deleteFileInCloudinary(publicId);
		}

		soundDoneDAO.delete(soundDone);
		
		return new SoundDoneDTO(soundDone);
	}
	
	/**
	 * Get Sound Done Deleted of Premium User
	 * 
	 * @param user
	 * @return
	 */
	public List<SoundDoneDTO> getSoundDoneDeletedOfPremiumUser(User user) {
		
		List<SoundDone> sounds = soundDoneDAO.findByUserIdAndStatus(user.getId(), EnumStatus.DELETED.getValue());
		
		return sounds.stream()
				.map(s -> new SoundDoneDTO(s))
				.collect(Collectors.toList());
	}
	
	/**
	 * Delete All Sounds Done Of User
	 * 
	 * @param user
	 * @throws IOException
	 */
	@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
	public void deleteAllSoundsDoneOfUser(User user) throws IOException {
		filesService.deleteAllFilesByTypeOfUser(user, EnumTypeFile.SOUNDDONE.getValue());
		soundDoneDAO.deleteByUser(user);
	}
	
	public SoundDone findByUrl(String url) {
		return soundDoneDAO.findByUrl(url);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
	public void delete(SoundDone soundDone) {
		soundDoneDAO.delete(soundDone);
	}
	
	public SoundDone persistent(SoundDone soundDone) {
		return soundDoneDAO.save(soundDone);
	}
	
	public SoundDone findById(Integer id) {
		return soundDoneDAO.findById(id)
				.orElseThrow(() -> new NotFoundValueException("Not Found Sound Done!", "SoundDoneService -> findById"));
	}
	
	public Page<SoundDone> findByUserNullAndStatusSound(String statusSound, Pageable pageable) {
		return soundDoneDAO.findByUserNullAndStatusSound(statusSound, pageable);
	}
	
	public Page<SoundDone> findByUserNullAndStatusSoundAndCreatedDateBetween(String statusSound, Date startDate, 
			Date endDate, Pageable pageable) {
		return soundDoneDAO.findByUserNullAndStatusSoundAndCreatedDateBetween(statusSound, startDate, endDate, pageable);
	}
	
	public Page<SoundDone> findByUserNull(Pageable pageable) {
		return soundDoneDAO.findByUserNull(pageable);
	}
	
	public List<SoundDone> findByUserNull() {
		return soundDoneDAO.findByUserNull();
	}
	
	public Page<SoundDone> findByUserNullAndCreatedDateBetween(Date starDate, Date enDate, Pageable pageable) {
		return soundDoneDAO.findByUserNullAndCreatedDateBetween(enDate, enDate, pageable);
	}
	
	/**
	 * Delete Sound Done 
	 * 
	 * @param soundDoneId
	 * @return
	 */
	public SoundDoneDTO adminDeleteSoundDoneAndFileUploaded(Integer soundDoneId) throws IOException {
		
		SoundDone soundDone = soundDoneDAO.findById(soundDoneId)
				.orElseThrow(() -> new NotFoundValueException("Not Found the Sound Done to delete!", 
						"SoundDoneService -> deleteSoundDoneAndFileUploaded"));
		
		String publicId = null;
		if (StringUtils.isNotBlank(soundDone.getUrl())) {
			Integer startingIndex = soundDone.getUrl().indexOf(ConstantUrl.URL_FOLDER);
			publicId = soundDone.getUrl().substring(startingIndex + 1).split("\\.")[0];
		}
		
		if (publicId != null) {
			cloudinaryService.deleteFileInCloudinary(publicId);
		}

		soundDoneDAO.delete(soundDone);
		
		return new SoundDoneDTO(soundDone);
	}
}
