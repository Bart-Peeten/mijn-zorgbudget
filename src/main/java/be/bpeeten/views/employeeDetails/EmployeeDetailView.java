package be.bpeeten.views.employeeDetails;

import be.bpeeten.data.entity.Person;
import be.bpeeten.data.service.PersonService;
import be.bpeeten.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

@PageTitle("Werknemer Detail")
@Route(value = "employee-detail", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class EmployeeDetailView extends VerticalLayout {

    private Person currentEmployee;
    private Grid<Person> grid;
    private Button closeBtn;
    private LocalDateTime startDateTime;
    private LocalTime endTimeValue;
    private final PersonService personService;

    public EmployeeDetailView(PersonService personService) {
        setSpacing(false);

        this.personService = personService;

        currentEmployee = ComponentUtil.getData(UI.getCurrent(), Person.class);

        VerticalLayout layout = new VerticalLayout(createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(createHeaderWorkBar(), layout);
    }

    private Component createGrid() {
        grid = new Grid<>(Person.class, false);
        grid.addColumn("startDateTime").setAutoWidth(true);
        grid.addColumn("endTimeValue").setAutoWidth(true);

        grid.setItems(currentEmployee);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
//        grid.addItemClickListener(event -> {
//            String item = event.getItem().getFirstName();
//        });

        return grid;
    }

    private HorizontalLayout createHeaderWorkBar() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        HorizontalLayout horizontalLabelLayout = new HorizontalLayout();
        HorizontalLayout horizontalBtnLayout = new HorizontalLayout();
        horizontalBtnLayout.setWidth("50%");
        horizontalLabelLayout.setWidth("50%");
        horizontalLayout.setWidth("100% ");

        Label firstName = new Label(currentEmployee.getFirstName());
        Label lastName = new Label(currentEmployee.getLastName());
        horizontalLabelLayout.add(firstName, lastName);

        Button logHoursBtn = new Button("Log uren");
        logHoursBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        logHoursBtn.addClickListener(e -> showTimeLogDialog().open());
        horizontalBtnLayout.setAlignItems(Alignment.END);
        horizontalBtnLayout.add(logHoursBtn);

        horizontalLayout.add(horizontalLabelLayout, horizontalBtnLayout);

        return horizontalLayout;
    }

    private Dialog showTimeLogDialog() {
        Dialog dialog = new Dialog();

        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePicker.setLabel("Selecteer een start datum en uur");
        dateTimePicker.setLocale(Locale.GERMAN);

        TimePicker timePicker = new TimePicker("Selecteer een eind uur");
        timePicker.setLocale(Locale.GERMAN);

        VerticalLayout dateTimePickerLayout = new VerticalLayout();
        dateTimePickerLayout.add(dateTimePicker, timePicker);

        closeBtn = new Button("Save");
        closeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeBtn.addClickListener(e -> {
            saveDateTimeToEmployee(dateTimePicker.getValue(), timePicker.getValue());
            startDateTime = dateTimePicker.getValue();
            endTimeValue = timePicker.getValue();
            dialog.close();
            refreshGrid();
        });

        dialog.add(dateTimePickerLayout);
        dialog.getFooter().add(closeBtn);

        return dialog;
    }

    private void saveDateTimeToEmployee(LocalDateTime startDateTime, LocalTime endTimeValue) {
        currentEmployee.setStartDateTime(startDateTime);
        currentEmployee.setEndTimeValue(endTimeValue);

        getPersonService().update(currentEmployee);
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    public PersonService getPersonService() {
        return personService;
    }
}
