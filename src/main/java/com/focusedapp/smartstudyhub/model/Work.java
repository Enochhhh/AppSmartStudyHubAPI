package com.focusedapp.smartstudyhub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@Entity
@Builder
@Table(name = "works")
public class Work implements Serializable {

	// Field endTime and field dateMarkCompleted is the same, you can use any of them
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;
	
	@Column(name = "due_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dueDate;
	
	@Column(name = "work_name", length = 100)
	private String workName;
	
	@Column(name = "priority", length = 20)
	private String priority;
	
	@Column(name = "number_of_pomodoros")
	private Integer numberOfPomodoros;
	
	@Column(name = "time_of_pomodoro")
	private Integer timeOfPomodoro;
	
	@Column(name = "time_passed")
	private Integer timePassed;
	
	@Column(name = "start_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;
	
	@Column(name = "end_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;
	
	@Column(name = "is_remindered")
	private Boolean isRemindered;
	
	@Column(name = "note", length = 300)
	private String note;
	
	@Column(name = "time_will_announce")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeWillAnnounce;
	
	@Column(name = "type_repeat")
	private String typeRepeat;
	
	@Column(name = "unit_repeat")
	private String unitRepeat;
	
	@Column(name = "amount_repeat")
	private Integer amountRepeat;
	
	@Column(name = "days_of_week_repeat")
	private String daysOfWeekRepeat;
	
	private String status;
	
	@Column(name = "old_status")
	private String oldStatus;
	
	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@Column(name = "number_of_pomodoros_done")
	private Integer numberOfPomodorosDone;
	
	@Column(name = "date_mark_completed")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateMarkCompleted;
	
	@Column(name = "date_end_repeat")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateEndRepeat;
	
	@OneToMany(mappedBy = "work", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ExtraWork> extraWorks;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "work_tag",
			joinColumns = @JoinColumn(name = "work_id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false))
	List<Tag> tags;
	
	@OneToMany(mappedBy = "work", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Pomodoro> pomorodos;
	
	public Work(Work work) {
		this.user = work.getUser();
		this.project = work.getProject();
		this.dueDate = work.getDueDate();
		this.workName = work.getWorkName();
		this.priority = work.getPriority();
		this.numberOfPomodoros = work.getNumberOfPomodoros();
		this.timeOfPomodoro = work.getTimeOfPomodoro();
		this.isRemindered = work.getIsRemindered();
		this.note = work.getNote();
		this.timeWillAnnounce = work.getTimeWillAnnounce();
		this.typeRepeat = work.getTypeRepeat();
		this.unitRepeat = work.getUnitRepeat();
		this.amountRepeat = work.getAmountRepeat();
		this.daysOfWeekRepeat = work.getDaysOfWeekRepeat();
		this.status = work.getStatus();
		this.createdDate = work.getCreatedDate();
		this.tags  = new ArrayList<>(work.getTags());
		this.dateMarkCompleted = work.getDateMarkCompleted();
		this.numberOfPomodorosDone = 0;
		this.timePassed = 0;
		this.dateEndRepeat = work.getDateEndRepeat();
	}
	
	public void setDataInRelationship(Work work) {
		List<ExtraWork> extraWorks = new ArrayList<>(work.getExtraWorks());
		extraWorks.stream().forEach(e -> {
			e.setWork(this);
		});
		this.extraWorks = extraWorks;
	}
}
