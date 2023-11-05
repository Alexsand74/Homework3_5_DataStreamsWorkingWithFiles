SELECT s.name, s.age, f.name
FROM student s
         LEFT JOIN  faculty f ON f.id = s.faculty_id;

SELECT s.name, s.age
FROM student s
         INNER JOIN  avatar a ON s.id = a.student_id;