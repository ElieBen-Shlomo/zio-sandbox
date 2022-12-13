USE chatbox;

CREATE TABLE IF NOT EXISTS USER (
	username text,
	session_id text,
	date_and_time timestamp,
	message text,
	status text,
	primary key (username, date_and_time)
);
