package Homework3_5.controller;

import Homework3_5.model.Faculty;
import Homework3_5.repository.StudentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import Homework3_5.model.Student;
import org.springframework.http.ResponseEntity;

import java.util.Collection;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
//      проверяем созданы ли ниже описанные сущности
        Assertions.assertThat(studentController).isNotNull();
        Assertions.assertThat(studentRepository).isNotNull();
    }

    //    @PostMapping
//    public Student add(@RequestBody Student student)
    @Test
    public void testPostStudent() {
//     создаем исходные данные, с чем будем работать и проверять
        var originalStudent = student("Ron", 20);
        var result = restTemplate.postForObject("/student", originalStudent, Student.class);
        System.out.println(result);
//     возвращаем с помощью метода restTemplate.postForObject() студента и проверяем его
        Assertions.assertThat(result.getAge()).isEqualTo(20);
        Assertions.assertThat(result.getName()).isEqualTo("Ron");
        Assertions.assertThat(result.getId()).isNotNull();
//      очищаем базу данных от вновь внесенных студентов
        restTemplate.delete("/student/" + result.getId());
    }

    //    @GetMapping("/{id}")
//    public Student get(@PathVariable long id)
    @Test
    public void testGetStudentId() {
//        создаем исходные данные, с чем будем работать и проверять
        var originalStudent = student("Harry", 18);
        var registeredStudentInTheDatabase = restTemplate.postForObject("/student", originalStudent, Student.class);
//        вызываем из базы данных студента с новым именем и возрастом и пережнем id и проверяем его
        var result = restTemplate.getForObject("/student/" + registeredStudentInTheDatabase.getId(), Student.class);
        Assertions.assertThat(result.getName()).isEqualTo("Harry");
        Assertions.assertThat(result.getAge()).isEqualTo(18);
//      очищаем базу данных от вновь внесенных студентов
        restTemplate.delete("/student/" + result.getId());
    }

    //    @PutMapping
//    public Student update(@RequestBody Student student)
    @Test
    public void testPutStudent() {
//        ПЕРВЫЙ СПОСОБ ПРОВЕРКИ МЕТОД restTemplate.put()
//        создаем исходные данные, с чем будем работать и проверять
        var originalStudent = student("Harry", 18);
        var registeredStudentInTheDatabase = restTemplate.postForObject("/student", originalStudent, Student.class);
//        меняем у студента который имеет уже id имя и возраст
        registeredStudentInTheDatabase.setName("Germiona");
        registeredStudentInTheDatabase.setAge(20);
//        заменяем имя и возраст студента под его прежнем id уже в таблице базы данных (проверяем метод PUT)
        restTemplate.put("/student", registeredStudentInTheDatabase);
//        вызываем из базы данных студента с новым именем и возрастом и пережнем id и проверяем его
        var result = restTemplate.getForObject("/student/" + registeredStudentInTheDatabase.getId(), Student.class);
        Assertions.assertThat(result.getName()).isEqualTo("Germiona");
        Assertions.assertThat(result.getAge()).isEqualTo(20);

//        ВТОРОЙ СПОСОБ ПРОВЕРКИ МЕТОД restTemplate.exchange()
//        меняем у студента который имеет уже id имя и возраст
        registeredStudentInTheDatabase.setName("Ron");
        registeredStudentInTheDatabase.setAge(21);
//        заменяем имя и возраст студента под его прежнем id уже в таблице базы данных (проверяем метод PUT)
        ResponseEntity<Student> studentEntity = restTemplate.exchange("/student",
                HttpMethod.PUT,
                new HttpEntity<>(registeredStudentInTheDatabase),
                Student.class);
//        возвращаем из метода restTemplate.exchange() - студента с новым именем и возрастом и пережнем id и проверяем его
        Assertions.assertThat(studentEntity.getBody().getName()).isEqualTo("Ron");
        Assertions.assertThat(studentEntity.getBody().getAge()).isEqualTo(21);
//      очищаем базу данных от вновь внесенных студентов
        restTemplate.delete("/student/" + studentEntity.getBody().getId());
    }

    //    @DeleteMapping("/{id}")
//    public Student remove(@PathVariable long id)
    @Test
    public void testDeleteStudent() {
//        создаем исходные данные, с чем будем работать и проверять
        var originalStudent = student("DeletedTest", 99);
        var registeredStudentInTheDatabase = restTemplate.postForObject("/student", originalStudent, Student.class);
//       удаляем студента из базы данных и возвращаем его ResponseEntity
        ResponseEntity<Student> studentEntity = restTemplate.exchange(
                "/student/" + registeredStudentInTheDatabase.getId(),
                HttpMethod.DELETE,
                null,
                Student.class);
//      проверяем с помощью полученного ResponseEntity студента его имя и возраст
        Assertions.assertThat(studentEntity.getBody().getName()).isEqualTo("DeletedTest");
        Assertions.assertThat(studentEntity.getBody().getAge()).isEqualTo(99);
//      возвращаем с помощью GET метода студента по его текущему id
        var deletedStudent = restTemplate.getForObject("/student/" + registeredStudentInTheDatabase.getId(), Student.class);
//      так как студента с таким id нет в таблице возвращается null, далее проверка на null
        Assertions.assertThat(deletedStudent).isNull();
    }

    //    @GetMapping("/{studentId}/faculty")
//    public Faculty facultyByStudent(@PathVariable long studentId)
    @Test
    void testGetFacultyByStudent() {
//       создаем исходные данные, с чем будем работать и проверять
        var originalStudent = student("StudentTest", 88);
//       регистрируем в базе данных факультетов наш тестируемый в будущем факультет
        var registeredFacultyInTheDatabase = restTemplate.postForObject("/faculty",
                faculty("FacultyTest", "Test-Color"), Faculty.class);
//       добавляем студенту зарегистрированный в базе данных факультет
        originalStudent.setFaculty(registeredFacultyInTheDatabase);
//       загружаем в базу данных студента с подключенным к нему факультетом
        var registeredStudentInTheDatabase = restTemplate.postForObject("/student", originalStudent, Student.class);

//        вызываем из базы данных факультет по id студента зарегистрированного в базе данных
        var result = restTemplate.getForObject("/student/" + registeredStudentInTheDatabase.getId() + "/faculty", Faculty.class);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo("FacultyTest");
        Assertions.assertThat(result.getColor()).isEqualTo("Test-Color");
//      очищаем базу данных от вновь внесенных студента и факультета
        restTemplate.delete("/student/" + registeredStudentInTheDatabase.getId());
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase.getId());
    }

    //    @GetMapping("/byAge")
//    public Collection<Student> byAge(@RequestParam int age)
    @Test
    void testFilterByAge() {
//       создаем исходные данные, с чем будем работать и проверять
//       регистрируем в базе данных новых студентов
        var s1 = restTemplate.postForObject("/student", student("Test1", 66), Student.class);
        var s2 = restTemplate.postForObject("/student", student("Test2", 67), Student.class);
        var s3 = restTemplate.postForObject("/student", student("Test3", 68), Student.class);
        var s4 = restTemplate.postForObject("/student", student("Test4", 69), Student.class);
        var s5 = restTemplate.postForObject("/student", student("Test5", 68), Student.class);
//      возвращаем параметрорезированную коллекцию ResponseEntity по студентам - ParameterizedTypeReference<Collection<Student>>
        var result = restTemplate.exchange("/student/byAge?age=68",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var students = result.getBody();
//      проверяем полученную коллекцию, студентов, что не = null, имеет размер 2 и в неё входят студенты s3,s5
        Assertions.assertThat(students).isNotNull();
        Assertions.assertThat(students.size()).isEqualTo(2);
        Assertions.assertThat(students).containsExactly(s3, s5);
//      очищаем базу данных от вновь внесенных студентов
        restTemplate.delete("/student/" + s1.getId());
        restTemplate.delete("/student/" + s2.getId());
        restTemplate.delete("/student/" + s3.getId());
        restTemplate.delete("/student/" + s4.getId());
        restTemplate.delete("/student/" + s5.getId());
    }

    //    @GetMapping("/byAgeBetween")
//    public Collection<Student> byAgeBetween(@RequestParam int min, @RequestParam int max)
    @Test
    void testFilterByAgeBetween() {
//       создаем исходные данные, с чем будем работать и проверять
//       регистрируем в базе данных новых студентов
        var s1 = restTemplate.postForObject("/student", student("Test1", 66), Student.class);
        var s2 = restTemplate.postForObject("/student", student("Test2", 67), Student.class);
        var s3 = restTemplate.postForObject("/student", student("Test3", 68), Student.class);
        var s4 = restTemplate.postForObject("/student", student("Test4", 69), Student.class);
        var s5 = restTemplate.postForObject("/student", student("Test5", 68), Student.class);
//      возвращаем параметрорезированную коллекцию ResponseEntity по студентам - ParameterizedTypeReference<Collection<Student>>
        var result = restTemplate.exchange("/student/byAgeBetween?min=67&max=69",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var students = result.getBody();
//      проверяем полученную коллекцию студентов, что не = null, имеет размер 4 и в неё входят студенты s2, s3, s4, s5
        Assertions.assertThat(students).isNotNull();
        Assertions.assertThat(students.size()).isEqualTo(4);
        Assertions.assertThat(students).containsExactly(s2, s3, s4, s5);
//      очищаем базу данных от вновь внесенных студентов
        restTemplate.delete("/student/" + s1.getId());
        restTemplate.delete("/student/" + s2.getId());
        restTemplate.delete("/student/" + s3.getId());
        restTemplate.delete("/student/" + s4.getId());
        restTemplate.delete("/student/" + s5.getId());
    }

    //    @GetMapping("/byAll")
//    public List<Student> byAll ()
    @Test
    void testByAll() {
//       создаем исходные данные, с чем будем работать и проверять
//       регистрируем в базе данных новых студентов
        var s1 = restTemplate.postForObject("/student", student("Test1", 66), Student.class);
        var s2 = restTemplate.postForObject("/student", student("Test2", 67), Student.class);
        var s3 = restTemplate.postForObject("/student", student("Test3", 68), Student.class);
        var s4 = restTemplate.postForObject("/student", student("Test4", 69), Student.class);
        var s5 = restTemplate.postForObject("/student", student("Test5", 68), Student.class);
//      возвращаем параметрорезированную коллекцию ResponseEntity по студентам - ParameterizedTypeReference<Collection<Student>>
        var result = restTemplate.exchange("/student/byAll",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var students = result.getBody();
//      проверяем полученную коллекцию студентов, что не = null, имеет размер 4 и в неё входят студенты s1, s2, s3, s4, s5
        Assertions.assertThat(students).isNotNull();
//        Assertions.assertThat(students.size()).isEqualTo(5);
        Assertions.assertThat(students.size()).isNotNull();
        Assertions.assertThat(students).contains(s1, s2, s3, s4, s5);
//      очищаем базу данных от вновь внесенных студентов
        restTemplate.delete("/student/" + s1.getId());
        restTemplate.delete("/student/" + s2.getId());
        restTemplate.delete("/student/" + s3.getId());
        restTemplate.delete("/student/" + s4.getId());
        restTemplate.delete("/student/" + s5.getId());
    }

    //    @GetMapping("/byFaculty/{id}")
//    public Collection<Student> byFaculty (@PathVariable long id)
    @Test
    void testByFacultyIdByStudents() {
//       создаем исходные данные, с чем будем работать и проверять
//       регистрируем в базе данных факультетов наш тестируемый в будущем факультет
        var registeredFacultyInTheDatabase = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty", "Test-Color"), Faculty.class);
//       создаем студентов, id у них ещё нет
        var s1 = student("Test1", 66);
        var s2 = student("Test2", 67);
        var s3 = student("Test3", 68);
        var s4 = student("Test4", 69);
        var s5 = student("Test5", 68);
//       добавляем всем студентам один и тот же факультет
        s1.setFaculty(registeredFacultyInTheDatabase);
        s2.setFaculty(registeredFacultyInTheDatabase);
        s3.setFaculty(registeredFacultyInTheDatabase);
        s4.setFaculty(registeredFacultyInTheDatabase);
        s5.setFaculty(registeredFacultyInTheDatabase);
//        добавляем студентов в базу данных и возвращаем их уже с id
        var saveStudentDb1 = restTemplate.postForObject("/student", s1, Student.class);
        var saveStudentDb2 = restTemplate.postForObject("/student", s2, Student.class);
        var saveStudentDb3 = restTemplate.postForObject("/student", s3, Student.class);
        var saveStudentDb4 = restTemplate.postForObject("/student", s4, Student.class);
        var saveStudentDb5 = restTemplate.postForObject("/student", s5, Student.class);
//      возвращаем параметрорезированную коллекцию ResponseEntity по студентам - ParameterizedTypeReference<Collection<Student>>
        var result = restTemplate.exchange("/student/byFaculty/" + registeredFacultyInTheDatabase.getId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var students = result.getBody();
//      проверяем полученную коллекцию студентов, что не = null, имеет размер 5 и в неё входят студенты s1 s2, s3, s4, s5
        Assertions.assertThat(students).isNotNull();
//        Assertions.assertThat(students.size()).isEqualTo(5);
        Assertions.assertThat(students.size()).isNotNull();
        Assertions.assertThat(students).contains(saveStudentDb1,
                saveStudentDb2,
                saveStudentDb3,
                saveStudentDb4,
                saveStudentDb5);
//      очищаем базу данных от вновь внесенных студентов
        restTemplate.delete("/student/" + saveStudentDb1.getId());
        restTemplate.delete("/student/" + saveStudentDb2.getId());
        restTemplate.delete("/student/" + saveStudentDb3.getId());
        restTemplate.delete("/student/" + saveStudentDb4.getId());
        restTemplate.delete("/student/" + saveStudentDb5.getId());
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase.getId());
    }

    // метод создания факультета из полей
    private static Faculty faculty(String name, String color) {
        var f = new Faculty();
        f.setName(name);
        f.setColor(color);
        return f;
    }

    // метод создания студента из полей
    private static Student student(String name, int age) {
        var s = new Student();
        s.setName(name);
        s.setAge(age);
        return s;
    }

}

