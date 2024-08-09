-- - Course
-- -----------------------------------------
-- Идентификатор
-- Название
-- Список студентов (ManyToMany)

CREATE TABLE course(
	id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	cname VARCHAR(50)
);

-- - Coordinator
-- -----------------------------------------
-- Идентификатор
-- Имя
-- Список студентов (OneToMany)

CREATE TABLE coordinator(
	id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	cord_name VARCHAR(50)
);

-- - Student
-- -----------------------------------------
-- Идентификатор
-- Имя
-- Координатор
-- Список курсов (ManyToMany)

CREATE TABLE student(
	id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	sname VARCHAR(50),
	coordinator_id INTEGER REFERENCES Coordinator(id)
);


CREATE TABLE stud_course(
	id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	stud_id INTEGER REFERENCES student(id),
	course_id INTEGER REFERENCES course(id)
);