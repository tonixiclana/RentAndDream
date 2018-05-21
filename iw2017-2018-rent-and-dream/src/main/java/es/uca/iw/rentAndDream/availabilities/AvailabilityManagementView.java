package es.uca.iw.rentAndDream.availabilities;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = AvailabilityManagementView.VIEW_NAME)
public class AvailabilityManagementView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "availabilityManagementView";

	private Grid<Availability> grid;
	private TextField filter;
	private Button addNewBtn;

	private AvailabilityEditor editor;

	
	private final AvailabilityService service;

	@Autowired
	public AvailabilityManagementView(AvailabilityService service, AvailabilityEditor editor) {
		this.service = service;
		this.editor = editor;
		this.grid = new Grid<>(Availability.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New Availability", FontAwesome.PLUS);
	    
	}
	
	@PostConstruct
	void init() {
		
		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		
		addComponents(actions, grid, editor);

		//grid.setHeight(300, Unit.PIXELS);
		grid.setSizeFull();
		grid.setColumns("id", "startDate", "endDate", "price");

		filter.setPlaceholder("Filter by name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> listAvailability(e.getValue()));

		// Connect selected User to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editAvailability(e.getValue());
		});

		// Instantiate and edit new User the new button is clicked
		addNewBtn.addClickListener(e -> editor.editAvailability(new Availability(null, null, 0f, null)));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listAvailability(filter.getValue());
		});

		// Initialize listing
		listAvailability(null);

	}

	private void listAvailability(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			grid.setItems(service.findAll());
		} else {
			//grid.setItems(service.findByNameStartsWithIgnoreCase(filterText));
		}
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}