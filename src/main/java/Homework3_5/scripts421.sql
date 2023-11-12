ALTER TABLE student
    ADD CHECK ( age >= 13 ),
    ALTER COLUMN name SET NOT NULL,
    ADD CONSTRAINT unique_name UNIQUE (name),
    ALTER COLUMN age SET DEFAULT 20;

ALTER TABLE faculty
    ADD CONSTRAINT unique_name_and_color UNIQUE (name, color);

ALTER TABLE student
    ALTER  column age set default 20;
