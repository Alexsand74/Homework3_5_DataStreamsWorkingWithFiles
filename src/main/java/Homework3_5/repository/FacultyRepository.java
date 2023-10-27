package Homework3_5.repository;
import Homework3_5.model.Faculty;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Collection<Faculty> findAllByColor(String color);
    Collection<Faculty> findAllByNameOrColorIgnoreCase(String name, String color);
    Faculty findFirstByNameIgnoreCase (String name);
}


