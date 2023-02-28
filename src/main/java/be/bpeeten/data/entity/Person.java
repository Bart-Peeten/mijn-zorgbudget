package be.bpeeten.data.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Person extends AbstractEntity {

    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Double workedHours;
    private String password;
    private String role;

    @ManyToMany
    private List<WorkedHour> workedHour;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public Double getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(Double workedHours) {
        this.workedHours = workedHours;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<WorkedHour> getWorkedHour() {
        return workedHour;
    }

    public void setWorkedHour(List<WorkedHour> workedHour) {
        this.workedHour = workedHour;
    }
}