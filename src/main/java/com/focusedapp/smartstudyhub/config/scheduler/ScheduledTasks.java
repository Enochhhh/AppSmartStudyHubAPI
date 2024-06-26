package com.focusedapp.smartstudyhub.config.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.focusedapp.smartstudyhub.service.HistoryDailyService;
import com.focusedapp.smartstudyhub.service.UserService;
import com.focusedapp.smartstudyhub.service.WorkService;

@Component
public class ScheduledTasks {
	
	@Autowired UserService userService;
	@Autowired HistoryDailyService historyDailyService;
	@Autowired WorkService workService;
	
	@Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
	public void taskRunOnMidnightEveryday() {
		userService.deleteAccountAfter30Days();
		historyDailyService.createHistoryDaily();
		userService.resetDataOfUserDaily();
		userService.resetDueDatePremium();
	}
	
	@Scheduled(cron = "0 0 0 ? * Mon", zone = "Asia/Ho_Chi_Minh")
	public void taskRunOnWeekly() {
		userService.resetDataOfUserDaily();
		userService.resetDataOfUserWeekly();
	}
	
}
