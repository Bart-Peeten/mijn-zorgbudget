package be.bpeeten.views.addUsers;

import be.bpeeten.data.Role;
import be.bpeeten.data.entity.User;
import be.bpeeten.data.service.UserService;
import be.bpeeten.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.security.RolesAllowed;
import java.util.Set;

@PageTitle("Voeg gebruiker toe")
@Route(value = "add-user", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class AddUserView extends Div {

    private TextField username = new TextField("User name");
    private TextField name = new TextField("Name");
    private PasswordField password = new PasswordField("Password");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<User> binder = new Binder<>(User.class);

    public AddUserView(UserService userService) {
        addClassName("voegwerknemertoe-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.bindInstanceFields(this);
        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            User user = binder.getBean();
            String hashedPassword = BCrypt.hashpw(password.getValue(), BCrypt.gensalt());
            user.setHashedPassword(hashedPassword);
            user.setRoles(Set.of(Role.ADMIN));
            userService.update(user);
            Notification.show(user.getClass().getSimpleName() + " details stored.");
            clearForm();
        });
    }

    private void clearForm() {
        binder.setBean(new User());
        password.clear();
    }

    private Component createTitle() {
        return new H3("User information");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(username, name, password);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }
}
