package Homework3_5.controller;
import Homework3_5.model.Faculty;
import net.minidev.json.JSONObject;
import Homework3_5.model.Student;
import Homework3_5.repository.AvatarRepository;
import Homework3_5.repository.FacultyRepository;
import Homework3_5.repository.StudentRepository;
import Homework3_5.service.AvatarService;
import Homework3_5.service.FacultyService;
import Homework3_5.service.StudentService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest
class StudentControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StudentRepository studentRepository;
    @MockBean
    AvatarRepository avatarRepository;
    @MockBean
    FacultyRepository facultyRepository;

    @SpyBean
    StudentService studentService;
    @SpyBean
    AvatarService avatarService;
    @SpyBean
    FacultyService facultyService;

    private final ObjectMapper objectMapper = new ObjectMapper();  // когда хотим получить на выходе коллекцию
    @Test
    public void contextLoads() {
//      проверяем созданы ли ниже описанные сущности
        Assertions.assertThat(studentRepository).isNotNull();
        Assertions.assertThat(avatarRepository).isNotNull();
        Assertions.assertThat(facultyRepository).isNotNull();
    }
    //    @PostMapping
//    public Student add(@RequestBody Student student)
    @Test
    public void testPostStudent() throws Exception {
        Long id = 1L;
        String name = "Harry";
        int age = 182;
//     отправляемые данные, создаем studentObject и student, вызывая их методы создания
        JSONObject studentObject = jsonStudent(name, age);
        Student student = student(id, name, age);
//    подстановка вместо методов результатов которые возвращают Mocks
//    когда (вызываем метод (studentRepository.save))
//                             в него передадим любой ((any) класс Student)
//                                                тогда возвращаем нами созданного здесь (студента)
        when(studentRepository.save(any(Student.class))).thenReturn(student);
//    настраиваем в mockMvc, post - запрос
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
//                   далее ответ
                        .accept(MediaType.APPLICATION_JSON))
//               проверки, в начале проверяем статус выполнения запроса на - 200 Ok
                .andExpect(status().isOk())
//                далее проверки внутри JSON объекта полей id, name, age
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    //    @GetMapping("/{id}")
//    public Student get(@PathVariable long id)
    @Test
    void testGetStudentId() throws Exception {
        Long id = 1L;
        String name = "Harry";
        int age = 182;
        Student student = student(id, name, age);

//        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    //    @PutMapping
//    public Student update(@RequestBody Student student)
    @Test
    public void testPutStudent() throws Exception {
        Long id = 1L;
        String name = "Harry";
        int age = 182;

        JSONObject studentObject = jsonStudent(name, age);
        studentObject.put("id", id);
        Student student = student(id, name, age);

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    //    @DeleteMapping("/{id}")
//    public Student remove(@PathVariable long id)
    @Test
    public void testDeleteStudent() throws Exception {
        Long id = 2L;
        String name = "Ron";
        int age = 190;

        Student student = student(id, name, age);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    //    @GetMapping("/{studentId}/faculty")
//    public Faculty facultyByStudent(@PathVariable long studentId)
    @Test
    void testGetFacultyByStudent() throws Exception {
        Long idFaculty = 1L;
        String colorFaculty = "Red";
        String nameFaculty = "Test-Faculty";

        Faculty faculty = new Faculty();
        faculty.setId(idFaculty);
        faculty.setColor(colorFaculty);
        faculty.setName(nameFaculty);

        Long id = 2L;
        String name = "Harry";
        int age = 182;

        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);
        student.setFaculty(faculty);

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + faculty.getId() + "/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idFaculty))
                .andExpect(jsonPath("$.name").value(nameFaculty))
                .andExpect(jsonPath("$.color").value(colorFaculty));
    }

    //    @GetMapping("/byAge")
//    public Collection<Student> byAge(@RequestParam int age)
    @Test
    void testFilterByAge() throws Exception {

        Collection students = newStudentsCollection();

        when(studentRepository.findByAge(anyInt())).thenReturn(students);

        var studentEntity = mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/byAge?age=17")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(students)));

        assertThat(studentEntity.toString()).isNotNull();
    }

//    @GetMapping("/byAgeBetween")
//    public Collection<Student> byAgeBetween(@RequestParam int min, @RequestParam int max)
      @Test
    public void testFilterByAgeBetween() throws Exception {

          Collection students = newStudentsCollection();

        when(studentRepository.findAllByAgeBetween(anyInt(), anyInt())).thenReturn(students);

    mockMvc.perform(MockMvcRequestBuilders
                    .get("/student/byAgeBetween?min=15&max=19")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(students)));
}

//    @GetMapping("/byAll")
//    public List<Student> byAll ()
@Test
    public void testByAll() throws Exception {

          Collection students = newStudentsCollection();

    when(studentRepository.findAll()).thenReturn((List<Student>) students);

    mockMvc.perform(MockMvcRequestBuilders
                    .get("/student/byAll")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                    .json(objectMapper.writeValueAsString((List<Student>)students)));
}

//    @GetMapping("/byFaculty/{id}")
//    public Collection<Student> byFaculty (@PathVariable long id)
@Test
void testByFacultyIdByStudents() throws Exception {
    Collection students = newStudentsCollection();

    when(studentRepository.findStudentsByFaculty_Id(anyLong())).thenReturn(students);
    mockMvc.perform(MockMvcRequestBuilders
                    .get("/student/byFaculty/1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                    .json(objectMapper.writeValueAsString(students)));
}

    private static Collection newStudentsCollection(){
    Long id = 1L;
    String name = "Harry";
    int age = 16;
    Student student1 = student(id, name, age);
    Student student2 = student(id + 1, name + 1, age + 1);
    Student student3 = student(id + 2, name + 2, age + 2);
    Student student4 = student(id + 3, name + 3, age + 1);
    Student student5 = student(id + 4, name + 4, age + 1);
    Collection students = new ArrayList();
    students.add(student2);
    students.add(student4);
    students.add(student5);
    return students;
}

//  метод создания студента из полей
    private static Student student(Long id, String name, int age) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);
        return student;
    }

//  метод создания JSONObject из полей студента
    private static JSONObject jsonStudent(String name, int age) {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("age", age);
        return obj;
    }

}

