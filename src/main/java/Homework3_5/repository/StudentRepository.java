package Homework3_5.repository;
import Homework3_5.model.Student;
import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;


public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);
    Collection<Student> findAllByAgeBetween(int min, int max);
    Collection<Student> findStudentsByFaculty_Id (long ig);

    @Query(value = "SELECT count(id) FROM student", nativeQuery = true)
    int totalCountOfStudents();

    @Query(value = "SELECT avg(age) FROM student", nativeQuery = true)
    double averageAgeOfStudents();

    @Query(value = "SELECT * FROM student ORDER BY id DESC LIMIT :count", nativeQuery = true)
    Collection<Student> lastStudents(@Param("count") int count);
}

