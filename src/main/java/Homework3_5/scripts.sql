select * from student;
select * from student where age > 15 and age < 19;
select name from student;
select * from student where name like '%о%';
select * from student where age > student.id;
select * from student ORDER BY age;
select * from faculty;
update student set faculty_id = 1;
select * from avatar;