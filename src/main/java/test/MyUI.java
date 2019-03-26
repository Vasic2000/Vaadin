package test;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import javafx.scene.control.SelectionMode;

import java.lang.reflect.Array;
import java.util.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        List<Person> people = Arrays.asList(
                new Person("name1", 1990),
                new Person("name2", 1991),
                new Person("no_name", -2000)
        );

        Grid<Person> grid = new Grid<>();
        grid.setItems(people);
        grid.addColumn(Person::getName).setCaption("Name");
        grid.addColumn(Person::getBirthYear).setCaption("BirthYear");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.addSelectionListener(new SelectionListener<Person>() {
            @Override
            public void selectionChange(SelectionEvent<Person> event) {
                Set<Person> selected = event.getAllSelectedItems();
                Notification.show(selected.size() + " item selected");
            }
        });

        final TextField name = new TextField();
        final TextField year = new TextField();
        Button add = new Button("Add");
        Button remove = new Button("Delete");
        Button edit = new Button("Edit");

        name.setCaption("name: ");
        year.setCaption("year: ");

        add.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent e) {
                Person person = new Person(name.getValue(), Integer.parseInt(year.getValue()));
                List<Person> gridItems = (List<Person>) ((ListDataProvider) grid.getDataProvider()).getItems();
                List<Person> peopleAdd = new ArrayList<>(gridItems);
                peopleAdd.add(person);
                grid.setItems(peopleAdd);
            }
        });

        remove.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent e) {
                List<Person> gridItems = (List<Person>) ((ListDataProvider) grid.getDataProvider()).getItems();
                List<Person> peopleToRemove= new ArrayList<>(grid.getSelectedItems());
                List<Person> peopleToStay = new ArrayList<>();

                for(Person p : peopleToRemove)
                    System.out.println(p.getName());

                for(Person it : gridItems) {
                    if(!peopleToRemove.contains(it))
                        peopleToStay.add(it);
                }
                grid.setItems(peopleToStay);
            }
        });


        edit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent e) {
                List<Person> gridItems = (List<Person>) ((ListDataProvider) grid.getDataProvider()).getItems();
                List<Person> peopleToEdit= new ArrayList<>(grid.getSelectedItems());
                List<Person> newTable = new ArrayList<>();
                Person person = new Person(name.getValue(), Integer.parseInt(year.getValue()));
                for(Person it : gridItems) {
                    if(!peopleToEdit.contains(it))
                        newTable.add(it);
                    else newTable.add(person);
                }
                grid.setItems(newTable);
            }
        });


        layout.addComponent(grid);
        layout.addComponents(name, year, add, remove, edit);

        setContent(layout);

    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
