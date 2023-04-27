package be.bpeeten.views.employeeDetails;

import be.bpeeten.data.entity.Person;
import be.bpeeten.data.entity.WorkedHour;
import be.bpeeten.data.service.WorkHourService;
import be.bpeeten.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.security.RolesAllowed;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@PageTitle("Werknemer Detail")
@Route(value = "employee-detail", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class EmployeeDetailView extends Div {

    private Grid<WorkedHour> grid;
    private Filters filters;
    private LocalDate selectedDate;
    private Person employee;
    private final transient WorkHourService workHourService;
    private LocalTime selectedStartTime;
    private LocalTime selectedEndTime;
    public static final String VISIBLE = "visible";

    public EmployeeDetailView(WorkHourService workHourService) {
        this.workHourService = workHourService;
        setSizeFull();
        addClassNames("werknemers-view");

        employee = ComponentUtil.getData(UI.getCurrent(), Person.class);

        filters = new Filters(this::refreshGrid);
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createHeaderWorkBar(), createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        add(layout);
    }

    private Component createHeaderWorkBar() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        Button addWorkHourBtn = new Button("Voeg uren toe");
        addWorkHourBtn.getElement().getStyle().set("margin-left", "20px");
        addWorkHourBtn.getElement().getStyle().set("margin-top", "10px");
        addWorkHourBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addWorkHourBtn.addClickListener(e -> {
            Dialog dataPicker = getDataPicker();
            dataPicker.open();
        });

        horizontalLayout.add(addWorkHourBtn);

        return horizontalLayout;
    }

    private Dialog getDataPicker() {
        DatePicker datePicker = new DatePicker();
        TimePicker startTimePicker = new TimePicker("selecteer een start uur");
        TimePicker endTimePicker = new TimePicker("selecteer een eind uur");
        Button saveButton = new Button("Save");
        Dialog dialog = new Dialog();
        datePicker.setLabel("Selecteer een dag");
        datePicker.setLocale(Locale.GERMANY);

        saveButton.addClickListener(event -> {
            WorkedHour workedHour = new WorkedHour();
            workedHour.setWorkDay(datePicker.getValue());
            workedHour.setStartTime(startTimePicker.getValue());
            workedHour.setEndTime(endTimePicker.getValue());
            workedHour.setPersons(List.of(this.employee));

            workHourService.save(workedHour);

            List<WorkedHour> hours = workHourService.getWorkHoursFor(employee);
            refreshGrid();

            dialog.close();
        });

        VerticalLayout dateTimePickerLayout = new VerticalLayout();
        dateTimePickerLayout.add(datePicker, startTimePicker, endTimePicker, saveButton);

        dialog.add(dateTimePickerLayout);

        return dialog;
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains(VISIBLE)) {
                filters.removeClassName(VISIBLE);
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName(VISIBLE);
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    public static class Filters extends Div implements Specification<WorkedHour> {

        private final DatePicker startDate = new DatePicker("Gewerkte dagen");
        private final DatePicker endDate = new DatePicker();

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);

            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                startDate.clear();
                endDate.clear();
                onSearch.run();
            });
            Button searchBtn = new Button("Zoek");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(createDateRangeFilter(), actions);
        }

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("Van");

            endDate.setPlaceholder("Tot");

            // For screen readers
            setAriaLabel(startDate, "From date");
            setAriaLabel(endDate, "To date");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }

        private void setAriaLabel(DatePicker datePicker, String label) {
            datePicker.getElement().executeJs("const input = this.inputElement;" //
                    + "input.setAttribute('aria-label', $0);" //
                    + "input.removeAttribute('aria-labelledby');", label);
        }

        @Override
        public Predicate toPredicate(Root<WorkedHour> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate.getValue() != null) {
                String databaseColumn = "workDay";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(databaseColumn),
                        criteriaBuilder.literal(startDate.getValue())));
            }
            if (endDate.getValue() != null) {
                String databaseColumn = "startTime";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.literal(endDate.getValue()),
                        root.get(databaseColumn)));
            }
            if (endDate.getValue() != null) {
                String databaseColumn = "endTime";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.literal(endDate.getValue()),
                        root.get(databaseColumn)));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private Component createGrid() {
        grid = new Grid<>(WorkedHour.class, false);
        grid.addColumn("workDay").setAutoWidth(true);
        grid.addColumn("startTime").setAutoWidth(true);
        grid.addColumn("endTime").setAutoWidth(true);

        grid.setItems(query -> workHourService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                employee).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        grid.addItemClickListener(event -> {
            ComponentUtil.setData(UI.getCurrent(), WorkedHour.class, event.getItem());
            UI.getCurrent().navigate("employee-detail");
        });

        return grid;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

}
