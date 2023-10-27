package Homework3_5.service;
import Homework3_5.model.Faculty;
import Homework3_5.repository.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository repository;

    public FacultyService(FacultyRepository repository) {
        this.repository = repository;
    }

    public Faculty add(Faculty faculty) {
        return repository.save(faculty);
    }

    public Faculty get(long id) {
        return repository.findById(id).orElse(null);
    }
    public Faculty getName (String name) {
        return repository.findFirstByNameIgnoreCase(name);
    }
        public Faculty remove(long id) {
        var entity = repository.findById(id).orElse(null);
        if (entity != null) {
            repository.delete(entity);
            return entity;
        }
        return null;
    }
    public Faculty update(Faculty faculty) {
        return repository.findById(faculty.getId())
                .map(entity -> repository.save(faculty))
                .orElse(null);
    }
    public Collection<Faculty> filterByNameOrColor(String name, String color) {
        return repository.findAllByNameOrColorIgnoreCase(name, color);
    }
    public List<Faculty> returnAllStudents() {
        return repository.findAll();
    }
}
