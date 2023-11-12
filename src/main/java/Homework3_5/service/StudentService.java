package Homework3_5.service;
import Homework3_5.model.Faculty;
import Homework3_5.model.Student;
import Homework3_5.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final static Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student add(Student student){
        logger.info("Was invoked method for create student");
    return studentRepository.save(student);
    }

    public Student get(long id) {
        logger.info("Was invoked method for getting student by id = {}", id);
        return studentRepository.findById(id).orElse(null);
    }

    public Student remove(long id) {
        logger.info("Was invoked method for removing student by id = {}", id);
        var entity = studentRepository.findById(id).orElse(null);
        if (entity != null) {
            studentRepository.delete(entity);
        }
        return entity;
    }
    public Student update(Student student) {
        logger.info("Was invoked method for change student ");
        return studentRepository.findById(student.getId())
                .map(entity -> studentRepository.save(student))
                .orElse(null);
    }
    public Collection<Student> filterByAge(int age) {
        logger.info("The method of searching for a students by age was called ");
        return studentRepository.findByAge(age);
    }

    public Collection<Student> filterByAgeBetween(int min, int max) {
        logger.info("A method was called to search for students by age from minimum to maximum ");
        return studentRepository.findAllByAgeBetween(min, max);
    }
    public List <Student> returnAllStudents() {
        logger.info("The method for calling all students from the database was called ");
        return studentRepository.findAll();
    }

    public Collection<Student> returnByFaculty(long id) {
        logger.info("The method for calling all students belonging to one faculty was called id = {} ", id);
        return  studentRepository.findStudentsByFaculty_Id( id);
    }

    public Integer totalCountOfStudents() {
        logger.info("Method that displays the number of all students in the database ");
        return studentRepository.totalCountOfStudents();
    }

    public double averageAgeOfStudents() {
        logger.info("Method showing the average age of all students ");
        return studentRepository.averageAgeOfStudents();
    }

    public Collection<Student> lastStudents(int count) {
        logger.info("Method showing the number of students since the last ");
        return studentRepository.lastStudents(count);
    }
}

