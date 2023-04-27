package be.bpeeten.data.repositories;

import be.bpeeten.data.entity.Person;
import be.bpeeten.data.entity.WorkedHour;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkHourRepository extends JpaRepository<WorkedHour, Long>, JpaSpecificationExecutor<WorkedHour> {

//    @Query("SELECT w FROM WorkedHour AS w LEFT JOIN Person AS p ")
    List<WorkedHour> findWorkedHourByPersons(Person person, Pageable page);

    List<WorkedHour> findWorkedHourByPersons(Person person);

}
