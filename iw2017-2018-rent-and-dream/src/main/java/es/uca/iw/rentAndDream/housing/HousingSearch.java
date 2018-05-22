package es.uca.iw.rentAndDream.housing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import es.uca.iw.rentAndDream.Utils.CitySearch;
import es.uca.iw.rentAndDream.cities.CityService;
import es.uca.iw.rentAndDream.countries.CountryService;
import es.uca.iw.rentAndDream.regions.RegionService;


public class HousingSearch extends VerticalLayout {
	
	private Binder<HousingSearch> binder = new Binder<HousingSearch>(HousingSearch.class);
	
	private CitySearch citySearch;

	
	private Integer _guests;
	private LocalDate _startDate;
	private LocalDate _endDate;
	private List<Housing> _results;	
	
	private DateField startDate;
	private DateField endDate;
	private ComboBox<Integer> guests;
	public Button searchButton;
	//Para mostrar el estado de validacion
	Label validationStatus = new Label();
	
	public HousingSearch(CityService cityService, RegionService regionService, CountryService countryService,
			HousingService housingService)
	{
		this.citySearch = new CitySearch(cityService, regionService, countryService);
		
		this._results = new ArrayList<Housing>();
		
		this.searchButton = new Button("Search");
		this.startDate = new DateField();
		this.endDate = new DateField();
		this.guests = new ComboBox<>(null, IntStream.range(1, 31).mapToObj(i -> i).collect(Collectors.toList()));
		
		guests.setPlaceholder("Number of guests");
		startDate.setValue(LocalDate.now());
		startDate.setPlaceholder("Entry Date");
		startDate.setRangeStart(LocalDate.now());
		endDate.setValue(LocalDate.now().plusDays(1));
		endDate.setPlaceholder("End date");
		endDate.setRangeStart(LocalDate.now().plusDays(1));

        //Realizamos las validaciones y bindeos
		  
        binder.forField(startDate)
        	.withValidator(new DateRangeValidator("The entry date is previous today", LocalDate.now(), null))
        	.asRequired("Is Required")
        	.bind(s -> this._startDate, (s, v) -> this._startDate = v);
        binder.forField(endDate)
        	.withValidator(new DateRangeValidator("The end date is the same day or previous at entry Date", 
        			startDate.getValue().plusDays(1), null))
        	.asRequired("Is Required")
        	.bind(s -> this._endDate, (s, v) -> this._endDate = v);
        binder.forField(guests)
        	.asRequired("Is Required")
        	.bind(s -> this._guests, (s, v) -> this._guests = v);
        
        binder.setStatusLabel(validationStatus);
        
        //Hacemos el autobind con todos los atributos de esta instancia
        binder.bindInstanceFields(this);
        
        searchButton.setEnabled(binder.isValid());
        
        
     
        //comprobamos la validacion de los componentes del formulario para permitir enviar o no
  		binder.addStatusChangeListener(event -> searchButton.setEnabled(binder.isValid() && citySearch.isValid()));
		citySearch.setChangeHandler(() -> {
			searchButton.setEnabled(citySearch.isValid() && binder.isValid());
		});
  		

		
		//Dandole funcionalidad al boton buscar
		this.searchButton.addClickListener(event -> 
		{
			try {
				binder.writeBean(this);
			} catch (ValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this._results = housingService.findByCityidAndAvailabilityAndGuest(citySearch.get_city(), _startDate, _endDate, _guests);
		});
		addComponents(citySearch, startDate, endDate, guests, searchButton);
	}

	
	@PostConstruct
	public void init()
	{
		//addComponents(citySearch, startDate, endDate, guests, searchButton);
	}
	
	
	public List<Housing> getResults()
	{
		return this._results;
	}
	
	public interface ChangeHandler {
		void onChange();
	}
	
	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		searchButton.addClickListener(e -> h.onChange());
	}
}