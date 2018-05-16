package es.uca.iw.rentAndDream.users;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.uca.iw.rentAndDream.security.SecurityUtils;

@SpringComponent
@UIScope
public class UserRegisterEditor extends VerticalLayout {
	
	private final UserService service;
	
	/**
	 * The currently edited user
	 */
	User user;

	private Binder<User> binder = new Binder<>(User.class);
	
	/* Fields to edit properties in User entity */
	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");
	TextField username = new TextField("Username");
	TextField password = new TextField("Password");
	TextField email = new TextField("Email");
	DateField birthday = new DateField("Birthday");
	TextField dni = new TextField("Dni");
	TextField telephone = new TextField("Telephone");

	/* Action buttons */
	Button save = new Button("Register", FontAwesome.SAVE);
	
	/* Layout for buttons */
	CssLayout actions = new CssLayout(save);


	@Autowired
	public UserRegisterEditor(UserService service) {
		this.service = service;

		addComponents(firstName, lastName, username, password, email, birthday, dni, telephone, actions);
		
		// bind using naming convention
		binder.bindInstanceFields(this);
	
		// Configure and style components
		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to save
		save.addClickListener(e -> service.save(user));
		setVisible(false);
	
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editUser(User c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			user = service.findOne(c.getId());
		}
		else {
			user = c;
		}
		
		// Bind user properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(user);

		setVisible(true);

		// A hack to ensure the whole form is visible
		save.focus();
		// Select all text in firstName field automatically
		firstName.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when save
		// is clicked
		save.addClickListener(e -> h.onChange());
	}
}