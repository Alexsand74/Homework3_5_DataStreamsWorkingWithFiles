package Homework3_5.controller;

import Homework3_5.model.Faculty;
import Homework3_5.model.Student;
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
import org.springframework.http.ResponseEntity;
import java.util.Collection;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private StudentController facultyController;

    @Autowired
    private StudentRepository facultyRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
//      проверяем созданы ли ниже описанные сущности
        Assertions.assertThat(facultyController).isNotNull();
        Assertions.assertThat(facultyRepository).isNotNull();
    }

    //    @PostMapping
//    public Faculty add(@RequestBody Faculty faculty)
    @Test
    public void testPostFaculty() {
//       создаем исходные данные, с чем будем работать и проверять
        var originalFaculty = faculty("Алгебра", "Пурпурный");
        var result = restTemplate.postForObject("/faculty", originalFaculty, Faculty.class);
        System.out.println(result);
//        возвращаем с помощью метода restTemplate.postForObject() факультет и проверяем его
        Assertions.assertThat(result.getColor()).isEqualTo("Пурпурный");
        Assertions.assertThat(result.getName()).isEqualTo("Алгебра");
        Assertions.assertThat(result.getId()).isNotNull();
//      очищаем базу данных от вновь внесенных факультетов
        restTemplate.delete("/faculty/" + result.getId());
    }

    //    @GetMapping("/{id}")
//    public Faculty get(@PathVariable long id)
    @Test
    public void testFacultyId() {
//        создаем исходные данные, с чем будем работать и проверять
        var originalFaculty = faculty("Сопромат", "Лиловый");
        var registeredFacultyInTheDatabase = restTemplate.postForObject("/faculty", originalFaculty, Faculty.class);
//        вызываем из базы данных факультет с новым именем и цветом и пережнем id и проверяем его
        var result = restTemplate.getForObject("/faculty/" + registeredFacultyInTheDatabase.getId(), Faculty.class);
        Assertions.assertThat(result.getName()).isEqualTo("Сопромат");
        Assertions.assertThat(result.getColor()).isEqualTo("Лиловый");
//      очищаем базу данных от вновь внесенных факультетов
        restTemplate.delete("/faculty/" + result.getId());
    }

    //    @PutMapping
//    public Faculty update(@RequestBody Faculty faculty)
    @Test
    public void testPutFaculty() {
//        ПЕРВЫЙ СПОСОБ ПРОВЕРКИ МЕТОД restTemplate.put()
//        создаем исходные данные, с чем будем работать и проверять
        var originalFaculty = faculty("Кибернетика", "Оранжевый");
        var registeredFacultyInTheDatabase = restTemplate.postForObject("/faculty", originalFaculty, Faculty.class);
//        меняем у факультета который имеет уже id имя и цвет
        registeredFacultyInTheDatabase.setName("Литература");
        registeredFacultyInTheDatabase.setColor("Желтый");
//        заменяем имя и цвет факультета под его прежнем id уже в таблице базы данных (проверяем метод PUT)
        restTemplate.put("/faculty", registeredFacultyInTheDatabase);
//        вызываем из базы данных факультет с новым именем и цветом и пережнем id и проверяем его
        var result = restTemplate.getForObject("/faculty/" + registeredFacultyInTheDatabase.getId(), Faculty.class);
        Assertions.assertThat(result.getName()).isEqualTo("Литература");
        Assertions.assertThat(result.getColor()).isEqualTo("Желтый");

//        ВТОРОЙ СПОСОБ ПРОВЕРКИ МЕТОД restTemplate.exchange()
//        меняем у факультета который имеет уже id имя и цвет
        registeredFacultyInTheDatabase.setName("История");
        registeredFacultyInTheDatabase.setColor("Голубой");
//        заменяем имя и возраст факультета под его прежнем id уже в таблице базы данных (проверяем метод PUT)
        ResponseEntity<Faculty> facultyEntity = restTemplate.exchange("/faculty",
                HttpMethod.PUT,
                new HttpEntity<>(registeredFacultyInTheDatabase),
                Faculty.class);
//        возвращаем из метода restTemplate.exchange() - факультет с новым именем и цветом и пережнем id и проверяем его
        Assertions.assertThat(facultyEntity.getBody().getName()).isEqualTo("История");
        Assertions.assertThat(facultyEntity.getBody().getColor()).isEqualTo("Голубой");
//        очищаем базу данных от вновь внесенных факультетов
        restTemplate.delete("/faculty/" + facultyEntity.getBody().getId());
    }

    //    @DeleteMapping("/{id}")
//    public Faculty remove(@PathVariable long id)
    @Test
    public void testDeleteFaculty() {
//        создаем исходные данные, с чем будем работать и проверять
        var originalFaculty = faculty("Кибернетика", "Оранжевый");
        var registeredFacultyInTheDatabase = restTemplate.postForObject("/faculty", originalFaculty, Faculty.class);
//       удаляем факультет из базы данных и возвращаем его ResponseEntity
        ResponseEntity<Faculty> facultyEntity = restTemplate.exchange(
                "/faculty/" + registeredFacultyInTheDatabase.getId(),
                HttpMethod.DELETE,
                null,
                Faculty.class);
//      проверяем с помощью полученного ResponseEntity факультет, его имя и цвет
        Assertions.assertThat(facultyEntity.getBody().getName()).isEqualTo("Кибернетика");
        Assertions.assertThat(facultyEntity.getBody().getColor().equals("Оранжевый"));
//      возвращаем с помощью GET метода факультета по его текущему id
        var deletedFaculty = restTemplate.getForObject("/faculty/" + registeredFacultyInTheDatabase.getId(), Faculty.class);
//      так как факультета с таким id нет в таблице возвращается null, далее проверка на null
        Assertions.assertThat(deletedFaculty).isNull();
    }

    //    @GetMapping("/{facultyId}/students")
//    public Collection<Student> findByFacultyStudents(@PathVariable long facultyId)
    @Test
    void testStudentByFaculty() {
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
//       добавляем студентов в базу данных и возвращаем их уже с id
        var saveStudentDb1 = restTemplate.postForObject("/student", s1, Student.class);
        var saveStudentDb2 = restTemplate.postForObject("/student", s2, Student.class);
        var saveStudentDb3 = restTemplate.postForObject("/student", s3, Student.class);
        var saveStudentDb4 = restTemplate.postForObject("/student", s4, Student.class);
        var saveStudentDb5 = restTemplate.postForObject("/student", s5, Student.class);
//      возвращаем параметроризированную коллекцию ResponseEntity по студентам - ParameterizedTypeReference<Collection<Student>>
        var result = restTemplate.exchange("/faculty/" + registeredFacultyInTheDatabase.getId() + "/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var students = result.getBody();
//      проверяем полученную коллекцию студентов, что не = null, имеет размер 5 и в неё входят студенты s1, s2, s3, s4, s5
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

    //    @GetMapping("/{name}/studentsOfFaculty")
//    public Collection<Student> findByFaculty(@PathVariable String name)
    @Test
    void testFindByFaculty() {
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
//      возвращаем параметроризированную коллекцию ResponseEntity по студентам - ParameterizedTypeReference<Collection<Student>>
        var result = restTemplate.exchange("/faculty/" + registeredFacultyInTheDatabase.getName() + "/studentsOfFaculty",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var students = result.getBody();
//      проверяем полученную коллекцию студентов, что не = null, имеет размер 5 и в неё входят студенты s1, s2, s3, s4, s5
        Assertions.assertThat(students).isNotNull();
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

//    @GetMapping("/byNameOrColor")
//    public Collection<Faculty> byNameOrColor(
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) String color)

    @Test
    void testByNameOrColor() {
//      создаем факультеты и грузим их в базу данных
        var registeredFacultyInTheDatabase1 = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty1", "Test-Color1"), Faculty.class);
        var registeredFacultyInTheDatabase2 = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty2", "Test-Color2"), Faculty.class);
        var registeredFacultyInTheDatabase3 = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty1", "Test-Color2"), Faculty.class);
        var registeredFacultyInTheDatabase4 = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty2", "Test-Color1"), Faculty.class);
        var registeredFacultyInTheDatabase5 = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty3", "Test-Color3"), Faculty.class);
//      возвращаем параметроризированную коллекцию ResponseEntity по факультетам - ParameterizedTypeReference<Collection<Faculty>>
        var result = restTemplate.exchange("/faculty/byNameOrColor?name=Test-Faculty1&color=Test-Color2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var faculties = result.getBody();
//      проверяем полученную коллекцию факультетов, что не = null, имеет размер 5 и в неё входят registeredFacultyInTheDatabase1,..2,..3
        Assertions.assertThat(faculties).isNotNull();
        Assertions.assertThat(faculties.size()).isNotNull();
        Assertions.assertThat(faculties).contains(registeredFacultyInTheDatabase1,
                registeredFacultyInTheDatabase2,
                registeredFacultyInTheDatabase3);
//      очищаем базу данных от вновь внесенных факультетов
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase1.getId());
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase2.getId());
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase3.getId());
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase4.getId());
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase5.getId());

    }

    //    @GetMapping("/byAll")
//    public List<Faculty> byAll ()
    @Test
    void testByAll() {
//      создаем факультеты и грузим их в базу данных
        var registeredFacultyInTheDatabase1 = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty1", "Test-Color1"), Faculty.class);
        var registeredFacultyInTheDatabase2 = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty2", "Test-Color2"), Faculty.class);
        var registeredFacultyInTheDatabase3 = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty1", "Test-Color2"), Faculty.class);
        var registeredFacultyInTheDatabase4 = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty2", "Test-Color1"), Faculty.class);
        var registeredFacultyInTheDatabase5 = restTemplate.postForObject("/faculty",
                faculty("Test-Faculty3", "Test-Color3"), Faculty.class);
//      проверяем полученную коллекцию факультетов, что не = null, имеет размер 5 и в неё входят registeredFacultyInTheDatabase1,..2,..3,..4,..5
        var result = restTemplate.exchange("/faculty/byAll",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
                });
//      преобразуем ResponseEntity коллекцию в коллекцию студентов
        var faculties = result.getBody();
//      проверяем полученную коллекцию факультетов, что не = null, имеет размер 5 и в неё входят registeredFacultyInTheDatabase1,..2,..3,..4
        Assertions.assertThat(faculties).isNotNull();
        Assertions.assertThat(faculties.size()).isNotNull();
        Assertions.assertThat(faculties).contains(registeredFacultyInTheDatabase1,
                registeredFacultyInTheDatabase2,
                registeredFacultyInTheDatabase3,
                registeredFacultyInTheDatabase4,
                registeredFacultyInTheDatabase5);
//      очищаем базу данных от вновь внесенных факультетов
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase1.getId());
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase2.getId());
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase3.getId());
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase4.getId());
        restTemplate.delete("/faculty/" + registeredFacultyInTheDatabase5.getId());

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
