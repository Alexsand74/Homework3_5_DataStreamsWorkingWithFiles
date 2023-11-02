package Homework3_5.controller;

import Homework3_5.model.Faculty;
import Homework3_5.model.Student;
import Homework3_5.repository.FacultyRepository;
import Homework3_5.service.FacultyService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FacultyController.class)
public class FacultyControllerMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FacultyRepository facultyRepository;
    @SpyBean
    private FacultyService facultyService;
    @InjectMocks
    private FacultyController facultyController;

    private ObjectMapper objectMapper = new ObjectMapper();
    @Test
    public void contextLoads() {
//      проверяем созданы ли ниже описанные сущности
        Assertions.assertThat(facultyController).isNotNull();
        Assertions.assertThat(facultyRepository).isNotNull();
    }

    //    @PostMapping
//    public Faculty add(@RequestBody Faculty faculty)
    @Test
    public void testPostFaculty() throws Exception {
        Long id = 1L;
        String name = "Алгебра";
        String color = "Пурпурный";
//     отправляемые данные, создаем facultyObject и faculty, вызывая их методы создания
        JSONObject facultyObject = jsonFaculty(name, color);
        Faculty faculty = createFaculty(id, name, color);
//    подстановка вместо методов результатов которые возвращают Mocks
//    когда (вызываем метод (studentRepository.save))
//                             в него передадим любой ((any) класс Faculty)
//                                                тогда возвращаем нами созданного здесь (faculty)
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
//    настраиваем в mockMvc, post - запрос
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
//                   далее ответ
                        .accept(MediaType.APPLICATION_JSON))
//               проверки, в начале проверяем статус выполнения запроса на - 200 Ok
                .andExpect(status().isOk())
//                далее проверки внутри JSON объекта полей id, name, age
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    //    @GetMapping("/{id}")
//    public Faculty get(@PathVariable long id)
    @Test
    public void testFacultyId() throws Exception{
        Long id = 1L;
        String name = "Алгебра";
        String color = "Пурпурный";
//     отправляемые данные, создаем facultyObject и faculty, вызывая их методы создания
        JSONObject facultyObject = jsonFaculty(name, color);
        Faculty faculty = createFaculty(id, name, color);
//    подстановка вместо методов результатов которые возвращают Mocks
//    когда (вызываем метод (studentRepository.findById))
//                             в него передадим любой ((any) класс Long)
//                                                тогда возвращаем нами созданного здесь (faculty)
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));
//    настраиваем в mockMvc, post - запрос
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/1")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
//                   далее ответ
                        .accept(MediaType.APPLICATION_JSON))
//               проверки, в начале проверяем статус выполнения запроса на - 200 Ok
                .andExpect(status().isOk())
//                далее проверки внутри JSON объекта полей id, name, age
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    //    @PutMapping
//    public Faculty update(@RequestBody Faculty faculty)
    @Test
    public void testPutFaculty() throws Exception {
        Long id = 1L;
        String name = "Алгебра";
        String color = "Пурпурный";
//     отправляемые данные, создаем facultyObject и faculty, вызывая их методы создания
        JSONObject facultyObject = jsonFaculty(name, color);
//    для метода put нужно, что бы facultyObject имел и id, добавляем его
        facultyObject.put("id", id);
        Faculty faculty = createFaculty(id, name, color);
//    подстановка вместо методов результатов которые возвращают Mocks
//    когда (вызываем метод (studentRepository.findById))
//                             в него передадим любой ((any) класс Long)
//                                                тогда возвращаем нами созданного здесь (faculty)
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
//    настраиваем в mockMvc, post - запрос
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
//                   далее ответ
                        .accept(MediaType.APPLICATION_JSON))
//               проверки, в начале проверяем статус выполнения запроса на - 200 Ok
                .andExpect(status().isOk())
//                далее проверки внутри JSON объекта полей id, name, age
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    //    @DeleteMapping("/{id}")
//    public Faculty remove(@PathVariable long id)
    @Test
    public void testDeleteFaculty () throws Exception {
        Long id = 1L;
        String name = "Алгебра";
        String color = "Пурпурный";
//     отправляемые данные, создаем facultyObject и faculty, вызывая их методы создания
        JSONObject facultyObject = jsonFaculty(name, color);
//    для метода put нужно, что бы facultyObject имел и id, добавляем его
        facultyObject.put("id", id);
        Faculty faculty = createFaculty(id, name, color);
//    подстановка вместо методов результатов которые возвращают Mocks
//    когда (вызываем метод (studentRepository.findById))
//                             в него передадим любой ((any) класс Long)
//                                                тогда возвращаем нами созданного здесь (faculty)
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
//    настраиваем в mockMvc, post - запрос
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/2")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
//                   далее ответ
                        .accept(MediaType.APPLICATION_JSON))
//               проверки, в начале проверяем статус выполнения запроса на - 200 Ok
                .andExpect(status().isOk())
//                далее проверки внутри JSON объекта полей id, name, age
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    //    @GetMapping("/{facultyId}/students")
//    public Collection<Student> findByFacultyStudents(@PathVariable long facultyId)
    @Test
    void testStudentByFaculty() throws Exception{
        Long id = 1L;
        String name = "Алгебра";
        String color = "Пурпурный";
//     отправляемые данные, создаем facultyObject и faculty, вызывая их методы создания
        JSONObject facultyObject = jsonFaculty(name, color);
//    для метода put нужно, что бы facultyObject имел и id, добавляем его
        facultyObject.put("id", id);
        Faculty faculty = createFaculty(id, name, color);

//        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));
//        when(facultyRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(faculty));
        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/faculty/1")
                        .get("/faculty/"+ 1L +"/students")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
//                   далее ответ
                        .accept(MediaType.APPLICATION_JSON))
//               проверки, в начале проверяем статус выполнения запроса на - 200 Ok
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    //    @GetMapping("/{name}/studentsOfFaculty")
//    public Collection<Student> findByFaculty(@PathVariable String name)
    @Test
    void testFindByFaculty() throws Exception {
        Long id = 1L;
        String name = "Алгебра";
        String color = "Пурпурный";
//     отправляемые данные, создаем facultyObject и faculty, вызывая их методы создания
        JSONObject facultyObject = jsonFaculty(name, color);
//    для метода put нужно, что бы facultyObject имел и id, добавляем его
        facultyObject.put("id", id);

        Faculty faculty = createFaculty(id, name, color);

        Student student1 = student(1L, "Коля1", 16);
        student1.setFaculty(faculty);
        Student student2 = student(2L, "Коля2", 17);
        student2.setFaculty(faculty);
        Student student3 = student(3L, "Коля3", 18);
        student3.setFaculty(faculty);
//    подстановка вместо методов результатов которые возвращают Mocks
        when(facultyRepository.findFirstByNameIgnoreCase(name)).thenReturn(faculty);

//    настраиваем в mockMvc, post - запрос
        var facultyEntity = mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/"+ name + "/studentsOfFaculty")
                        .content(facultyObject.toString())
//                        .contentType(MediaType.APPLICATION_JSON)
//                   далее ответ
                        .accept(MediaType.APPLICATION_JSON))
//               проверки, в начале проверяем статус выполнения запроса на - 200 Ok
                  .andExpect(status().isOk());

           assertThat(facultyEntity.toString()).isNotNull();
           System.out.println(facultyEntity);
    }

//    @GetMapping("/byNameOrColor")
//    public Collection<Faculty> byNameOrColor(
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) String color)

    @Test
    void testByNameOrColor() throws Exception {

            long id1 = 1L;
            String name1 = "Слизерин";
            String color1 = "Желтый";
            String color1IgnoreCase = "желтый";
            long id2 = 2L;
            String name2 = "Гриффиндор";
            String color2 = "Красный";
            String name2IgnoreCase = "гриффиндор";

            Faculty faculty1 = createFaculty(id1,name1,color1); // строим объектное представление
            Faculty faculty2 = createFaculty(id2,color2,name2); // строим объектное представление

            when(facultyRepository.findAllByNameOrColorIgnoreCase(name2IgnoreCase,color1IgnoreCase))
                           .thenReturn( Set.of(faculty1, faculty2)); // т.к. мок на репозитории, то говорим, что хотим
                                                                     // получить, когда будет вызван метод
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/faculty/byNameOrColor") // сюда будет запрос
                            .queryParam("name", name2IgnoreCase)
                            .queryParam("color", color1IgnoreCase)
                            .contentType(MediaType.APPLICATION_JSON) // получаем json
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())// что ожидаем
                    .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(List.of(faculty1, faculty2))));
    }

    //    @GetMapping("/byAll")
//    public List<Faculty> byAll ()
    @Test
    void testByAll() throws Exception {
        long id1 = 1L;
        String name1 = "Слизерин";
        String color1 = "Желтый";

        long id2 = 2L;
        String name2 = "Гриффиндор";
        String color2 = "Красный";

        long id3 = 3L;
        String name3 = "Когтевран";
        String color3 = "Синий";

        Faculty faculty1 = createFaculty(id1,name1,color1); // строим объектное представление
        Faculty faculty2 = createFaculty(id2,color2,name2);
        Faculty faculty3 = createFaculty(id3,color3,name3);

        when(facultyRepository.findAll()).thenReturn(List.of(faculty1, faculty2, faculty3)); // т.к. мок на репозитории, то говорим, что хотим
                                                                                             // получить, когда будет вызван метод

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/byAll") // сюда будет запрос
                         .contentType(MediaType.APPLICATION_JSON) // получаем json
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())// что ожидаем
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(List.of(faculty1, faculty2, faculty3))));
    }

    //  метод создания студента из полей
    private static Student student(Long id, String name, int age) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);
        return student;
    }

    //  метод создания студента из полей
    private static Faculty createFaculty(Long id, String name, String color) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);
        return faculty;
    }

    //  метод создания JSONObject из полей студента
    private static JSONObject jsonFaculty (String name, String color) {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("color", color);
        return obj;
    }
}
