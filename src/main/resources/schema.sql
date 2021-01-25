DROP TABLE IF EXISTS inventory;

CREATE TABLE IF NOT EXISTS inventory (
	id serial PRIMARY KEY,
	item VARCHAR ( 50 ) UNIQUE NOT NULL,
	quantity Integer NOT NULL
);