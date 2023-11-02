package Homework3_5.controller;

import Homework3_5.model.Faculty;
import Homework3_5.model.Student;
import Homework3_5.service.FacultyService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService service;

    public FacultyController(FacultyService service) {
        this.service = service;
    }

    @PostMapping
    public Faculty add(@RequestBody Faculty faculty) {
        return service.add(faculty);
    }

    @GetMapping("/{id}")
    public Faculty get(@PathVariable long id) {
        return service.get(id);
    }

    @PutMapping
    public Faculty update(@RequestBody Faculty faculty) {
        return service.update(faculty);
    }

    @DeleteMapping("/{id}")
    public Faculty remove(@PathVariable long id) {
        return service.remove(id);
    }

    @GetMapping("/{facultyId}/students")
    public Collection<Student> findByFacultyStudents(@PathVariable long facultyId) {
        return service.get(facultyId).getStudents();
    }
    @GetMapping("/{name}/studentsOfFaculty")
    public Collection<Student> findByFaculty(@PathVariable String name) {
        return  service.getName(name).getStudents();
    }

    @GetMapping("/byNameOrColor")
    public Collection<Faculty> byNameOrColor(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color
    ) {
        return service.filterByNameOrColor(name, color);
    }
    @GetMapping("/byAll")
    public List<Faculty> byAll () {
        return service.returnAllStudents();
    }
}

