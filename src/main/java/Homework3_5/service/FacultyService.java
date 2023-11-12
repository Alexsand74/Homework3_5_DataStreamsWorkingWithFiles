package Homework3_5.service;
import Homework3_5.model.Faculty;
import Homework3_5.repository.FacultyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class FacultyService {
    private final static Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository repository;

    public FacultyService(FacultyRepository repository) {
                this.repository = repository;
    }

    public Faculty add(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        return repository.save(faculty);
    }

    public Faculty get(long id) {
        logger.info("Was invoked method for getting faculty by id = {}", id);
        return repository.findById(id).orElse(null);
    }
    public Faculty getName (String name) {
        logger.info("The method of getting the faculty by name = {}", name);
        return repository.findFirstByNameIgnoreCase(name);
    }
        public Faculty remove(long id) {
        logger.info("Was invoked method for removing faculty by id = {}", id);
        var entity = repository.findById(id).orElse(null);
        if (entity != null) {
            repository.delete(entity);
            return entity;
        }
        return null;
    }
    public Faculty update(Faculty faculty) {
        logger.info("The method to update the faculty by name = {}", faculty);
        return repository.findById(faculty.getId())
                .map(entity -> repository.save(faculty))
                .orElse(null);
    }
    public Collection<Faculty> filterByNameOrColor(String name, String color) {
        logger.info("The method was called to display a faculty from the database by name = {} or color = {}", name, color);
        return repository.findAllByNameOrColorIgnoreCase(name, color);
    }
    public List<Faculty> returnAllFaculty() {
        logger.info("The method was called, we display all faculties from the database ");
        return repository.findAll();
    }
}
