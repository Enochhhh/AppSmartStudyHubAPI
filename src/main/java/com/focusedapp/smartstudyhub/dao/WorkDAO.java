package com.focusedapp.smartstudyhub.dao;


import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.focusedapp.smartstudyhub.model.User;
import com.focusedapp.smartstudyhub.model.Work;

@Repository
public interface WorkDAO extends JpaRepository<Work, Integer> {
	
	Optional<Work> findByIdAndStatus(Integer workId, String status);
	
	Optional<Work> findByIdAndUserIdAndStatus(Integer workId, Integer userId, String status);
	
	List<Work> findByProjectIdAndStatus(Integer projectid, String status);
	
	List<Work> findByProjectIdAndUserIdAndStatus(Integer projectid, Integer userId, String status);
	
	List<Work> findByUserIdAndStatus(Integer userId, String status);
	
	List<Work> findByWorkNameContainingAndUserIdAndStatus(String keySearch, Integer userId, String status);
	
	@Query(value = "select * from works w where w.user_id = :userId and w.work_name like %:keySearch% and (w.status = :statusFirst "
			+ " or w.status = :statusSecond)", nativeQuery = true)
	List<Work> findByWorkNameContainingAndUserIdAndStatusBetween(@Param("keySearch") String keySearch, @Param("userId") Integer userId, @Param("statusFirst") String statusFirst, 
			@Param("statusSecond") String statusSecond);
	
	@Query(value = "select * from works w where w.user_id = :userId and (w.status = :statusFirst "
			+ " or w.status = :statusSecond)", nativeQuery = true)
	List<Work> findByUserIdAndOneOfTwoStatus(@Param("userId") Integer userId, @Param("statusFirst") String statusFirst, 
			@Param("statusSecond") String statusSecond);
	
	@Query(value = "select * from works w where w.user_id = :userId and w.priority = :priority and (w.status = :statusFirst "
			+ " or w.status = :statusSecond)", nativeQuery = true)
	List<Work> findByUserIdAndPriorityAndOneOfTwoStatus(@Param("userId") Integer userId, @Param("priority") String priority, 
			@Param("statusFirst") String statusFirst, 
			@Param("statusSecond") String statusSecond);
	
	void deleteByUser(User user);
	
	List<Work> findByDueDateBetweenAndStatusNotAndUser(Date startDate, Date endDate, String status, User user);
	
	List<Work> findByIdInAndStatus(List<Integer> workIds, String status);
	
	List<Work> findByUserIdAndStatusAndDateMarkCompletedGreaterThanEqualAndDateMarkCompletedLessThan(Integer userId, String status,
			Date startDate, Date endDate);
	
	List<Work> findByTimeWillAnnounceLessThanEqualAndStatusAndIsReminderedTrue(Date nowDate, String status);

}
