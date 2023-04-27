package be.bpeeten.data.service;

import be.bpeeten.data.entity.Person;
import be.bpeeten.data.entity.WorkedHour;
import be.bpeeten.data.repositories.WorkHourRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkHourService {

    private WorkHourRepository workHourRepository;

    public WorkHourService(WorkHourRepository workHourRepository) {
        this.workHourRepository = workHourRepository;
    }

    public Page<WorkedHour> list(Pageable pageable, Specification<WorkedHour> filter) {
        return workHourRepository.findAll(filter, pageable);
    }

    public List<WorkedHour> list(Pageable pageable, Person person) {
        return workHourRepository.findWorkedHourByPersons(person, pageable);
    }

    public void save(WorkedHour workedHour) {
        getWorkHourRepository().save(workedHour);
    }

    public List<WorkedHour> getWorkHoursFor(Person person) {
        return getWorkHourRepository().findWorkedHourByPersons(person);
    }

    public WorkHourRepository getWorkHourRepository() {
        return workHourRepository;
    }

}
