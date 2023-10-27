package Homework3_5.repository;
import Homework3_5.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);
    Collection<Student> findAllByAgeBetween(int min, int max);
    Collection<Student> findStudentsByFaculty_Id (long ig);
}

