package be.bpeeten.data.repositories;

import be.bpeeten.data.entity.WorkedHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkHourRepository extends JpaRepository<WorkedHour, Long>, JpaSpecificationExecutor<WorkedHour> {

}
