package be.bpeeten.views.users;

import be.bpeeten.data.entity.User;
import be.bpeeten.data.service.UserService;
import be.bpeeten.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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
import java.util.ArrayList;
import java.util.List;

@PageTitle("Gebruikers")
@Route(value = "users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UserView extends Div {

    private Grid<User> grid;

    private Filters filters;
    private final UserService userService;

    public UserView(UserService userService) {
        this.userService= userService;
        setSizeFull();
        addClassNames("werknemers-view");

        filters = new Filters(() -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
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
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    public static class Filters extends Div implements Specification<User> {

        private final TextField name = new TextField("Name");

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            name.setPlaceholder("Name");

            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                onSearch.run();
            });
            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Button addUserBtn = new Button("Add");
            addUserBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addUserBtn.addClickListener(e -> UI.getCurrent().navigate("add-user"));

            Div actions = new Div(addUserBtn, resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(name, actions);
        }

        @Override
        public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!name.isEmpty()) {
                String lowerCaseFilter = name.getValue().toLowerCase();
                Predicate nameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        lowerCaseFilter + "%");
                Predicate usernameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
                        lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(nameMatch, usernameMatch));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private Component createGrid() {
        grid = new Grid<>(User.class, false);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("username").setAutoWidth(true);
        grid.addColumn("roles").setAutoWidth(true);

        grid.setItems(query -> userService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        grid.addItemClickListener(event -> {
            String item = event.getItem().getName();
            Notification.show("Navigating to details of: " + item);
            UI.getCurrent().navigate("employee-detail");
        });

        return grid;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

}
