-- Game schema

-- !Ups
CREATE TABLE GAME(
  id SERIAL NOT NULL PRIMARY KEY,
  name text NOT NULL,
  row_count integer NOT NULL,
  col_count integer NOT NULL,
  bomb_amount integer,
  started_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE RUNNING_GAME(
    id SERIAL NOT NULL PRIMARY KEY,
    game_name text,
    game_id integer NOT NULL,
    cell_type text NOT NULL,
    is_flagged boolean DEFAULT false,
    is_open boolean DEFAULT false,
    bombs_touching integer DEFAULT 0,
    cells_arround text DEFAULT ''
);


-- !Downs

drop table GAME;
drop table RUNNING_GAME;