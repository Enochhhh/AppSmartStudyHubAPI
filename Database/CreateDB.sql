--  drop database if exists smart_study_hub;
--  create database smart_study_hub;
--  use smart_study_hub;

drop database if exists defaultdb; 
create database defaultdb; 
use defaultdb;

create table otp_code (
	id int auto_increment,
    email varchar(50),
    otp_code text,
    otp_time_expiration datetime,
    constraint OtpCodePrimaryKey primary key(id)
);

create table users(
	id int auto_increment,
    user_name varchar(50),
    email varchar(50), 
    passwords text,
    phone_number varchar(11),
    first_name nvarchar(50),
    last_name nvarchar(50),
    address nvarchar(100),
    date_of_birth date,
    country nvarchar(50),
    created_at datetime,
    roles varchar(20) not null,
    image_url text,
    total_time_focus int,
    provider text,
    status text, -- It have 2 value: ACTIVE, BANNED and DELETED
    time_admin_modified datetime,
    constraint UserPrimaryKey primary key(id)
);

create table manage_users(
	user_manager_id int not null,
    user_id int not null,
    created_date datetime,
    constraint ManageUserPrimaryKey primary key(user_manager_id, user_id),
	constraint ManageUsersUserManagerIdForeignKey foreign key(user_manager_id) references users(id),
	constraint ManageUsersUserIdForeignKey foreign key(user_id) references users(id)
);

create table friend_users(
	user_id int not null,
    user_id_friend int not null,
    created_date datetime,
    status text, -- BLOCK, FRIEND, UNFRIEND
    constraint FriendUserPrimaryKey primary key(user_id, user_id_friend),
    constraint FriendUsersUserIdForeignKey foreign key(user_id) references users(id),
	constraint FriendUsersUserIdFriendForeignKey foreign key(user_id_friend) references users(id)
);

create table report(
	id int auto_increment,
    user_id int not null,
    email varchar(50), 
    phone_number varchar(11),
    title nvarchar(100),
    content nvarchar(300),
    status_report text,
    created_date datetime,
    type_report nvarchar(50),
    url_file text,
    status text,
	constraint ReportPrimaryKey primary key(id),
    constraint ReportUserIdForeignKey foreign key(user_id) references users(id)
);

create table vote(
	id int auto_increment,
    user_id int,
    title nvarchar(100),
    content nvarchar(300),
    created_date datetime,
    number_star int,
    status text,
	constraint VotePrimaryKey primary key(id),
    constraint VoteUserIdForeignKey foreign key(user_id) references users(id)
);

create table study_group(
	id int auto_increment,
    codes text,
    name_group nvarchar(50),
    descriptions nvarchar(300),
    total_member int,
    total_time_focus int,
    image_url text,
    created_date datetime,
    status_group text, -- It have 2 value: PRIVATE, PUBLIC
    status text,
	constraint StudyGroupPrimaryKey primary key(id)
);

create table users_join_study_group(
	user_id int not null,
    study_group_id int not null,
    created_date datetime,
    status text, -- JOINED, UNJOIN, BANNED
    constraint UserJoinStudyGroupPrimaryKey primary key(user_id, study_group_id),
    constraint UserJoinStudyGroupUserIdForeignKey foreign key(user_id) references users(id),
    constraint UserJoinStudyGroupStudyGroupIdForeignKey foreign key(study_group_id) references study_group(id)
);

create table theme(
	id int auto_increment,
    user_id int,
    name_theme nvarchar(50),
    url text,
    status_theme text, -- It have 3 value: DEFAULT, OWNED, PREMIUM
    status text,
    created_date datetime,
    constraint ThemePrimaryKey primary key(id),
    constraint ThemeUserIdForeignKey foreign key(user_id) references users(id)
);

create table folder(
	id int auto_increment,
    user_id int not null,
    folder_name nvarchar(50),
    color_code text,
    icon_url text,
    created_date datetime,
    status text,
    old_status text,
    constraint FolderPrimaryKey primary key(id),
    constraint FolderUserIdForeignKey foreign key(user_id) references users(id)
);

create table project(
	id int auto_increment,
	user_id int not null,
    folder_id int,
    project_name nvarchar(50),
    color_code text,
    icon_url text,
    created_date datetime,
    status text,
    old_status text,
    constraint ProjectPrimaryKey primary key(id),
    constraint ProjectFolderId foreign key(folder_id) references folder(id),
    constraint ProjectUserIdForeignKey foreign key(user_id) references users(id)
);

create table works(
	id int auto_increment,
    user_id int not null,
    project_id int,
    due_date datetime,
    work_name nvarchar(100),
    priority nvarchar(20),
    number_of_pomodoros int,
    time_of_pomodoro int,
    time_passed int,
    number_of_pomodoros_done int,
    start_time datetime,
    end_time datetime,
    is_remindered boolean,
    is_repeated boolean,
    note nvarchar(300),
    assignee_id int,
    created_date datetime,
    time_will_start datetime,
    time_will_announce datetime,
    status text,
    old_status text,
    constraint WorksPrimaryKey primary key(id),
    constraint WorksUserIdForeignKey foreign key(user_id) references users(id),
    constraint WorksProjectIdForeignKey foreign key(project_id) references project(id),
    constraint WorksAssigneeIdForeignKey foreign key(assignee_id) references users(id)
);

create table extra_work(
	id int auto_increment,
    work_id int not null,
    extra_work_name nvarchar(50),
    status nvarchar(20),
    start_time datetime,
    end_time datetime,
    number_of_pomodoros int,
    time_passed int,
    created_date datetime,
    old_status text,
    constraint ExtraWorkPrimaryKey primary key(id),
    constraint ExtraWorkWorkIdForeignKey foreign key(work_id) references works(id)
);

create table tag(
	id int auto_increment,
    user_id int not null,
    tag_name nvarchar(50),
    color_code text,
    created_date datetime,
    status text,
    constraint TagPrimaryKey primary key(id),
    constraint TagUserIdForeignKey foreign key(user_id) references users(id)
);

create table work_tag(
	work_id int not null,
    tag_id int not null,
    constraint WorkTagPrimaryKey primary key(work_id, tag_id),
    constraint WorkTagWorkIdForeignKey foreign key(work_id) references works(id),
    constraint WorkTagTagIdForeignKey foreign key(tag_id) references tag(id)
);

create table sound_concentration(
	id int auto_increment,
    user_id int,
    name_sound nvarchar(50),
    url text,
    status_sound text, -- It have 3 value: DEFAULT, PREMIUM, OWNED
    status text,
    created_date datetime,
    constraint SoundConcentrationPrimaryKey primary key(id),
    constraint SoundConcentrationUserIdForeignKey foreign key(user_id) references users(id)
);

create table sound_done(
	id int auto_increment,
    user_id int,
    name_sound nvarchar(50),
    url text,
    status_sound text, -- It have 3 value: DEFAULT, PREMIUM, OWNED
	status text,
    created_date datetime,
    constraint SoundDonePrimaryKey primary key(id),
    constraint SoundDUserIdForeignKey foreign key(user_id) references users(id)
);

create table category_forum(
	id int auto_increment,
    name_category nvarchar(50),
    total_post int,
    total_comment int,
    created_date datetime,
    constraint CategoryForumPrimaryKey primary key(id)
);

create table post_forum(
	id int auto_increment,
    user_id int not null,
    category_forum_id int not null,
    title nvarchar(50),
    content nvarchar(3000),
    tag text,
    image_url text,
    total_like int,
    total_view int,
    created_date datetime,
    url_post text,
    total_type_react text,
    limits text,
    status_post text, -- It have 2 value: ACTIVE, DELETED, BANNED
    status text,
    constraint PostForumPrimaryKey primary key(id),
    constraint PostForumUserIdForeignKey foreign key(user_id) references users(id),
    constraint PostForumCategoryForumIdForeignKey foreign key(category_forum_id) references category_forum(id)
);

create table like_post(
	post_id int not null,
    user_id int not null,
    created_date datetime,
    type_react text,
    constraint LikePostPrimaryKey primary key(post_id, user_id),
    constraint LikePostPostIdForeignKey foreign key(post_id) references post_forum(id),
    constraint LikePostUserIdForeignKey foreign key(user_id) references users(id)
);

create table comment_post(
	id int auto_increment,
	post_id int not null,
    user_id int not null,
    content nvarchar(3000),
    created_date datetime,
    total_like int,
    total_type_react text,
    status text,
    constraint CommentPostPrimaryKey primary key(id),
	constraint CommentPostPostIdForeignKey foreign key(post_id) references post_forum(id),
    constraint CommentPostUserIdForeignKey foreign key(user_id) references users(id)
);

create table like_comment(
	comment_id int not null,
    user_id int not null,
    created_date datetime,
    type_react text,
    constraint LikeCommentPrimaryKey primary key(comment_id, user_id),
    constraint LikeCommentCommentIdForeignKey foreign key(comment_id) references comment_post(id),
    constraint LikeCommentUserIdForeignKey foreign key(user_id) references users(id)
);

create table report_post(
	id int auto_increment,
	user_id int not null,
    post_id int not null,
    title text,
    content nvarchar(3000),
    created_date datetime,
    status_report text,
    status text,
    constraint ReportPostPrimaryKey primary key(id),
    constraint ReportPostUserIdForeignKey foreign key(user_id) references users(id),
    constraint ReportPostPostIdForeignKey foreign key(post_id) references post_forum(id)
);

create table errorlog(
	id int auto_increment,
	class_name text,
	error text,
	message text,
	stack_trace text,
	path text,
	created_date datetime,
	constraint ErrorLogPrimaryKey primary key(id)
);

create table pomodoros(
	id int auto_increment,
    user_id int,
    work_id int,
    extra_work_id int,
    pomodoro_name text,
    time_of_pomodoro int,
    start_time datetime,
    end_time datetime,
    is_start_pomo boolean,
    is_end_pomo boolean,
    mode text,
    number_pomo_done_of_work int,
    created_date datetime,
    constraint PomodorosPrimaryKey primary key(id),
    constraint PomodorosUserIdForeignKey foreign key(user_id) references users(id),
    constraint PomodorosWorkIdForeignKey foreign key(work_id) references works(id),
    constraint PomodorosExtraWorkIdForeignKey foreign key(extra_work_id) references extra_work(id)
);

create table files(
	id int auto_increment,
    user_id int,
    type text,
    folder text,
    file_name text,
    format text,
    resource_type text,
    secure_url text,
    created_at datetime,
    public_id text,
    constraint FilesPrimaryKey primary key(id),
    constraint FilesUserIdForeignKey foreign key(user_id) references users(id)
);

-- Expected to do more later
create table event_schedule(
	id int auto_increment,
    event_id_group int,
    type_event text, -- EVENT-MEETING
    location text,
    is_all_day boolean,
    color_code text,
    announce_date datetime,
    descriptions text,
    url_attach_file text,
    created_date datetime,
    status text,
    constraint EventSchedulePrimaryKey primary key(id)
);

-- Expected to do more later
create table assignee_event_schedule(
	event_id int,
    assignee_id int,
	constraint AssigneeEventSchedulePrimaryKey primary key(event_id, assignee_id),
    constraint AssigneeEventScheduleEventIdForeignKey foreign key(event_id) references event_schedule(id),
    constraint AssigneeEventScheduleAssigneeIdForeignKey foreign key(assignee_id) references users(id)
);

-- TRIGGER
-- Trigger auto update number of pomodoros and time passed of work when update extra work
DELIMITER $$  
create trigger after_update_extra_work
after update 
on extra_work for each row
begin 
	update works 
    set number_of_pomodoros_done = ifnull(number_of_pomodoros_done, 0) + ifnull(new.number_of_pomodoros, 0) - ifnull(old.number_of_pomodoros, 0),
		time_passed = ifnull(time_passed, 0) + ifnull(new.time_passed, 0) - ifnull(old.time_passed, 0)
    where id = old.work_id;
end $$
DELIMITER ;

-- TRIGGER auto update time focus of user when update time passed of work
DELIMITER $$
create trigger after_update_works
after update 
on works for each row
begin 
	if new.time_passed > old.time_passed then
		update users 
				set total_time_focus  = ifnull(total_time_focus, 0) + (new.time_passed - old.time_passed)
				where id = new.user_id;
    end if;
end $$
DELIMITER ;

-- Trigger auto update work and extra work when insert pomodoro
DELIMITER $$  
create trigger after_insert_pomodoros
after insert 
on pomodoros for each row
begin 
	if new.work_id is not null then
		if new.is_start_pomo = true and new.is_end_pomo = false then
			update works 
				set number_of_pomodoros_done = ifnull(number_of_pomodoros_done, 0) + 1,
					time_passed = ifnull(time_passed, 0) + new.time_of_pomodoro,
                    start_time = now()
				where id = new.work_id;
		elseif new.is_end_pomo = false then 
			update works 
				set number_of_pomodoros_done = ifnull(number_of_pomodoros_done, 0) + 1,
					time_passed = ifnull(time_passed, 0) + new.time_of_pomodoro
				where id = new.work_id;
		end if;
	elseif new.extra_work_id is not null then
		if new.is_start_pomo = true and new.is_end_pomo = false then
			update extra_work 
				set number_of_pomodoros = ifnull(number_of_pomodoros, 0) + 1,
					time_passed = ifnull(time_passed, 0) + new.time_of_pomodoro,
                    start_time = now()
				where id = new.extra_work_id;
		elseif new.is_end_pomo = false then 
			update extra_work 
				set number_of_pomodoros = ifnull(number_of_pomodoros, 0) + 1,
					time_passed = ifnull(time_passed, 0) + new.time_of_pomodoro
				where id = new.extra_work_id;
		end if;
	end if;
end $$
DELIMITER ;



