package Homework3_5.controller;

import Homework3_5.model.Faculty;
import Homework3_5.repository.StudentRepository;
import net.bytebuddy.asm.MemberSubstitution;
import org.apache.naming.java.javaURLContextFactory;
import org.assertj.core.api.Assertions;
import org.h2.expression.Parameter;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Index;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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
        System.out.println(" -----------ЭТО ПОРТ------------>" + port);
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
        int variableAge = 68;
//       создаем исходные данные, с чем будем работать и проверять
//       регистрируем в базе данных новых студентов
        creatingStudentDatabase();
        var s1 = restTemplate.postForObject("/student", student("Test" + variableAge, variableAge ), Student.class);
//      возвращаем параметрорезированную коллекцию ResponseEntity по студентам - ParameterizedTypeReference<Collection<Student>
        var result = restTemplate.exchange("/student/byAge?age=" + variableAge,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var students = result.getBody();
//      проверяем полученную коллекцию, студентов, что не = null, имеет размер 2 и в неё входят студенты s3,s5
        Assertions.assertThat(students).isNotNull();
        Assertions.assertThat(students.size()).isEqualTo(2);
        Assertions.assertThat(students).contains(s1,listStudents.get(2));
//      очищаем базу данных от вновь внесенных студентов
        restTemplate.delete("/student/" + s1.getId());
        removeStudentsFromBase();

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
        creatingStudentDatabase();
//      возвращаем параметрорезированную коллекцию ResponseEntity по студентам - ParameterizedTypeReference<Collection<Student>>
        var result = restTemplate.exchange("/student/byAll",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var students = result.getBody();
//      проверяем полученную коллекцию студентов, что не = null, имеет размер 10 и в неё входят студенты
        Assertions.assertThat(students).isNotNull();
          Assertions.assertThat(students.size()).isNotNull();
         Assertions.assertThat(students.size()).isEqualTo(listStudents.size());
        Assertions.assertThat(students).isEqualTo(listStudents);
        Assertions.assertThat(students).contains( listStudents.get(1),
                                                  listStudents.get(2),
                                                  listStudents.get(3),
                                                  listStudents.get(4),
                                                  listStudents.get(5),
                                                  listStudents.get(6),
                                                  listStudents.get(7),
                                                  listStudents.get(8),
                                                  listStudents.get(9));
//      очищаем базу данных от вновь внесенных студентов
        removeStudentsFromBase();
    }

    //    @GetMapping("/byFaculty/{id}")
//    public Collection<Student> byFaculty (@PathVariable long id)
    @Test
    void testByFacultyIdByStudents() {
//       создаем исходные данные, с чем будем работать и проверять
        creatingStudentDatabase();

//      возвращаем параметрорезированную коллекцию ResponseEntity по студентам - ParameterizedTypeReference<Collection<Student>>
        var result = restTemplate.exchange("/student/byFaculty/" + listStudents.get(0).getFaculty().getId(),
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
        Assertions.assertThat(students.size()).isEqualTo(listStudents.size());
        Assertions.assertThat(students).isEqualTo(listStudents);
        Assertions.assertThat(students).contains( listStudents.get(2),
                                                  listStudents.get(5),
                                                  listStudents.get(6),
                                                  listStudents.get(8),
                                                  listStudents.get(9));

//      очищаем базу данных от вновь внесенных студентов
        removeStudentsFromBase();
    }

    @Test
    void testTotalCountOfStudents () {

        creatingStudentDatabase();

       var result = restTemplate.getForEntity("/student/totalCount", Object.class);
        var studentsCounter = result.getBody();

        Assertions.assertThat(studentsCounter).isNotNull();
        Assertions.assertThat(studentsCounter).isEqualTo(10);

        removeStudentsFromBase ();
    }



    @Test
    void testAverageAgeOfStudents () {
        creatingStudentDatabase();

        var result = restTemplate
                .getForEntity("/student/averageAge", Object.class)
                .getBody();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(70.5);

        removeStudentsFromBase ();
    }

    @Test
    void testLastStudents () {
        creatingStudentDatabase();

        int count = 5;
//      возвращаем параметрорезированную коллекцию ResponseEntity по студентам - ParameterizedTypeReference<Collection<Student>>
                var result = restTemplate.exchange("/student/lastStudents/" + count,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var students = result.getBody();
//      проверяем полученную коллекцию студентов, что не = null, имеет размер 5 и в неё входят студенты s1 s2, s3, s4, s5
        Assertions.assertThat(students).isNotNull();
        Assertions.assertThat(students.size()).isEqualTo(count);

//        Assertions.assertThat(students).contains((Student)tempListStudents.listIterator());
        Assertions.assertThat(students).contains(listStudents.get(9),
                                                 listStudents.get(8),
                                                 listStudents.get(7),
                                                 listStudents.get(6),
                                                 listStudents.get(5));

        removeStudentsFromBase ();
    }

//    СОЗДАЕМ И ЗАПОЛНЯЕМ БАЗУ ДАННЫХ СТУДЕНТАМИ
List<Student> listStudents = new ArrayList<>();
    private void creatingStudentDatabase () {
//       создаем исходные данные, с чем будем работать и проверять
//       регистрируем в базе данных факультетов наш тестируемый в будущем факультет
        var registeredFacultyInTheDatabase = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty", "Test-Color"), Faculty.class);

//       создаем студентов, id у них ещё нет и формируем лист из студентов
        int v = 65;
        String s = "s";
        String test = "Test";
           for (int i = 1; i < 11; i++){
//               создаем временного студента
               Student tempStudent = student(test + i, 65 + i);
//                временному студенту присваиваем факультет
               tempStudent.setFaculty(registeredFacultyInTheDatabase);
//                добавляем временного студента в лист студентов
               listStudents.add(tempStudent);
        }
//           загружаем лист студентов в базу сразу же достаем студента уже с его id что в базе и ложим в тот же лист
           for (int i = 0; i < 10; i++){
               listStudents.set( i, (restTemplate.postForObject("/student", listStudents.get(i), Student.class)));
               System.out.println(listStudents.get(i));
        }
    }

// УДАЛЕНИЕ ВСЕХ СТУДЕНТОВ ИЗ БАЗЫ ДАННЫХ
    private void removeStudentsFromBase() {
        for (int i = 0; i < 10; i++){
            Student tempStudent = listStudents.get(i);
            restTemplate.delete("/student/" + tempStudent.getId());
        }
    }
    // МЕТОД СОЗДАНИЯ ФАКАУЛЬТЕТА ИЗ ПОЛЕЙ
    private static Faculty faculty(String name, String color) {
        var f = new Faculty();
        f.setName(name);
        f.setColor(color);
        return f;
    }

    // МЕТОД СОЗДАНИЯ СТУДЕНТА
    private static Student student(String name, int age) {
        var s = new Student();
        s.setName(name);
        s.setAge(age);
        return s;
    }

}

