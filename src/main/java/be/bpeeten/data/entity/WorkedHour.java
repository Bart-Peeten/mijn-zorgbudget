package be.bpeeten.data.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Entity
public class WorkedHour extends AbstractEntity {

    private Date workDay;
    private Time startTime;
    private Time endTime;

    @ManyToMany
    private List<Person> persons;

    public Date getWorkDay() {
        return workDay;
    }

    public void setWorkDay(Date workDay) {
        this.workDay = workDay;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
