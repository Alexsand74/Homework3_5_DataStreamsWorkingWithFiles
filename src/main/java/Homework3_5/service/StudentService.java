package Homework3_5.service;
import Homework3_5.model.Faculty;
import Homework3_5.model.Student;
import Homework3_5.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student add(Student student) {
        return studentRepository.save(student);
    }

    public Student get(long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student remove(long id) {
        var entity = studentRepository.findById(id).orElse(null);
        if (entity != null) {
            studentRepository.delete(entity);
        }
        return entity;
    }
    public Student update(Student student) {
        return studentRepository.findById(student.getId())
                .map(entity -> studentRepository.save(student))
                .orElse(null);
    }
    public Collection<Student> filterByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public Collection<Student> filterByAgeBetween(int min, int max) {
        return studentRepository.findAllByAgeBetween(min, max);
    }
    public List <Student> returnAllStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> returnByFaculty(long id) {
        return  studentRepository.findStudentsByFaculty_Id( id);
    }

    public Integer totalCountOfStudents() {
        return studentRepository.totalCountOfStudents();
    }

    public double averageAgeOfStudents() {
        return studentRepository.averageAgeOfStudents();
    }

    public Collection<Student> lastStudents(int count) {
        return studentRepository.lastStudents(count);
    }
}

