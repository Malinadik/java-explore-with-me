DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS event CASCADE;
DROP TABLE IF EXISTS compilation CASCADE;
DROP TABLE IF EXISTS compilation_events CASCADE;
DROP TABLE If EXISTS request CASCADE;

CREATE TABLE IF NOT EXISTS categories (
	id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
	name varchar(255) NULL
);

CREATE TABLE IF NOT EXISTS compilation (
	id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
	pinned bool NULL,
	title varchar(50) NULL
);
CREATE TABLE IF NOT EXISTS users (
	id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
	email varchar(255) NULL,
	name varchar(255) NULL
);
CREATE TABLE IF NOT EXISTS event (
	id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
	annotation varchar(2000) NULL,
	confirmed_requests int8 NULL,
	created_on timestamp NULL,
	description varchar(7000) NULL,
	event_date timestamp NULL,
	lat float4 NULL,
	lon float4 NULL,
	paid bool NULL,
	participant_limit int8 NULL,
	published_on timestamp NULL,
	request_moderation bool NULL,
	state varchar(255) NULL,
	title varchar(255) NULL,
	views int8 NULL,
	category_id int8 NULL REFERENCES categories (id),
	initiator_id int8 NULL REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilation_events (
	id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
	compilation_id int8 NULL REFERENCES compilation(id),
	event_id int8 NULL REFERENCES event(id)
);

CREATE TABLE IF NOT EXISTS request (
	id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
	created timestamp NULL,
	status varchar(255) NULL,
	event_id int8 NULL REFERENCES event (id),
	requester_id int8 NULL REFERENCES users (id)
);
