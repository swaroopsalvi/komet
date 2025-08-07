package dev.ikm.komet.kview.mvvm.view.properties;

import dev.ikm.komet.framework.observable.ObservableConcept;
import dev.ikm.komet.framework.observable.ObservableConceptVersion;
import dev.ikm.komet.framework.observable.ObservableEntity;
import dev.ikm.komet.framework.view.ViewProperties;
import dev.ikm.komet.kview.mvvm.viewmodel.StampViewModel2;
import dev.ikm.tinkar.coordinate.stamp.calculator.Latest;
import dev.ikm.tinkar.coordinate.view.calculator.ViewCalculator;
import dev.ikm.tinkar.entity.ConceptEntity;
import dev.ikm.tinkar.entity.transaction.Transaction;
import dev.ikm.tinkar.terms.ConceptFacade;
import dev.ikm.tinkar.terms.EntityFacade;
import dev.ikm.tinkar.terms.State;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.carlfx.cognitive.loader.InjectViewModel;

import static dev.ikm.komet.kview.mvvm.viewmodel.FormViewModel.VIEW_PROPERTIES;
import static dev.ikm.komet.kview.mvvm.viewmodel.StampViewModel2.StampProperties.*;
import static dev.ikm.tinkar.terms.State.fromConceptNid;


public class StampAddController {

    @FXML
    private ComboBox<State> statusComboBox;

    @FXML
    private ComboBox<ConceptFacade> moduleComboBox;

    @FXML
    private ComboBox<ConceptFacade> pathComboBox;

    private ViewProperties viewProperties;

    @InjectViewModel
    private StampViewModel2 stampViewModel;
    private Latest<ObservableConceptVersion> observableConceptVersionLatest;
    @FXML
    public void initialize() {

//        EntityFacade entityFacade = stampViewModel.getPropertyValue(ENTITY);
//        viewProperties = stampViewModel.getPropertyValue(VIEW_PROPERTIES);
//        observableConceptVersionLatest = new Latest<>(); //observableConcept.getSnapshot(getViewProperties().calculator()).getLatestVersion();
//
//        if (entityFacade != null) {
//            ObservableConcept observableConcept = ObservableEntity.get(entityFacade.nid());
//            observableConceptVersionLatest = observableConcept.getSnapshot(viewProperties.calculator()).getLatestVersion();
//        }

        //get the modules from view model
        ObservableList<State> statues = stampViewModel.getObservableList(STATUSES);
        statues.addListener((ListChangeListener<State>) c -> {
            initializeStateComboBox(statusComboBox, statues);
        });

        ObservableList<ConceptFacade> modules = stampViewModel.getObservableList(MODULES);
        modules.addListener((ListChangeListener<ConceptFacade>) c -> {
            initializeComboBox(moduleComboBox, modules, MODULE);

        });

        ObservableList<ConceptFacade> paths = stampViewModel.getObservableList(PATHS);
        paths.addListener((ListChangeListener<ConceptFacade>) c -> {
            initializeComboBox(pathComboBox, paths, PATH);
        });




    }

    private void initializeStateComboBox(ComboBox<State> comboBox, ObservableList<State> selectionOptions) {
        EntityFacade entityFacade = stampViewModel.getPropertyValue(ENTITY);
        if (entityFacade == null) {
            return;
        }
        viewProperties = stampViewModel.getPropertyValue(VIEW_PROPERTIES);
        ObservableConcept observableConcept = ObservableEntity.get(entityFacade.nid());
        observableConceptVersionLatest = observableConcept.getSnapshot(viewProperties.calculator()).getLatestVersion();

        //Set the display to String
        comboBox.getItems().clear();
        comboBox.getItems().addAll(selectionOptions);
        comboBox.setConverter((new StringConverter<State>() {
            @Override
            public String toString(State state) {
                viewProperties = stampViewModel.getPropertyValue(VIEW_PROPERTIES);
                ViewCalculator viewCalculator = viewProperties.calculator();

                return state.name();

            }
            @Override
            public State fromString(String s) {
                return null;
            }
        }));

        //get the modules from view model
//        ObservableList<ConceptFacade> observableConceptFacade = stampViewModel.getObservableList(stampProperties);
        //add listener to modules.
//        observableConceptFacade.addListener((ListChangeListener<ConceptFacade>) c -> {

        //get the selected value for the module and set it to comboBox value.
        State conceptStampProperty = stampViewModel.getPropertyValue(STATUS);
        comboBox.setValue(conceptStampProperty);
//        });

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                observableConceptVersionLatest = observableConcept.getSnapshot(viewProperties.calculator()).getLatestVersion();

                    observableConceptVersionLatest.ifPresent(observableConceptVersion -> observableConceptVersion.stateProperty().set(fromConceptNid(newValue.nid())));
            }
        });
    }


    private void initializeComboBox(ComboBox<ConceptFacade> comboBox, ObservableList<ConceptFacade> selectionOptions, StampViewModel2.StampProperties stampProperty
    ) {

        EntityFacade entityFacade = stampViewModel.getPropertyValue(ENTITY);
        if (entityFacade == null) {
            return;
        }
        viewProperties = stampViewModel.getPropertyValue(VIEW_PROPERTIES);
        ObservableConcept observableConcept = ObservableEntity.get(entityFacade.nid());
        observableConceptVersionLatest = observableConcept.getSnapshot(viewProperties.calculator()).getLatestVersion();

        //Set the display to String
        comboBox.getItems().clear();
        comboBox.getItems().addAll(selectionOptions);
        comboBox.setConverter((new StringConverter<ConceptFacade>() {
            @Override
            public String toString(ConceptFacade conceptFacade) {
                viewProperties = stampViewModel.getPropertyValue(VIEW_PROPERTIES);
                ViewCalculator viewCalculator = viewProperties.calculator();
                if(conceptFacade == null){
                    return conceptFacade.toString();
                }else {
                    return viewCalculator.getRegularDescriptionText(conceptFacade).get();
                }
            }
            @Override
            public ConceptFacade fromString(String s) {
                return null;
            }
        }));

        //get the modules from view model
//        ObservableList<ConceptFacade> observableConceptFacade = stampViewModel.getObservableList(stampProperties);
        //add listener to modules.
//        observableConceptFacade.addListener((ListChangeListener<ConceptFacade>) c -> {

        //get the selected value for the module and set it to comboBox value.
        ConceptEntity conceptStampProperty = stampViewModel.getPropertyValue(stampProperty);
        comboBox.setValue(conceptStampProperty);
//        });

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                observableConceptVersionLatest = observableConcept.getSnapshot(viewProperties.calculator()).getLatestVersion();
                if( stampProperty == MODULE){
                    observableConceptVersionLatest.ifPresent(observableConceptVersion -> observableConceptVersion.moduleProperty().set(newValue));
                } else if (stampProperty == PATH) {
                    observableConceptVersionLatest.ifPresent(observableConceptVersion -> observableConceptVersion.pathProperty().set(newValue));
                }else if( stampProperty == STATUS){
                    observableConceptVersionLatest.ifPresent(observableConceptVersion -> observableConceptVersion.stateProperty().set(fromConceptNid(newValue.nid())));
                }
            }
        });
    }

//    private void initStatusComboBox() {
//        ObservableList<State> statuses = stampViewModel.getObservableList(STATUSES);
//        statuses.addListener((ListChangeListener<State>) c -> {
//            List<String> statusesStrings = statuses.stream()
//                                                   .map(this::toFirstLetterCapitalized)
//                                                   .collect(Collectors.toList());
//            Collections.sort(statusesStrings, NaturalOrder.getObjectComparator());
//            statusComboBox.getItems().setAll(statusesStrings);
//        });
//
//        ObjectProperty<State> statusProperty = stampViewModel.getProperty(STATUS);
//        statusProperty.subscribe(state -> {
//            if (state != null) {
//                statusComboBox.setValue(toFirstLetterCapitalized(state));
//            }
//        });
//        observableConceptVersionLatest.ifPresent(observableConceptVersion -> {
//           observableConceptVersion.stateProperty().bind(statusProperty);
//        });
//
//    }
//
//    private void initPathComboBox() {
//        ObservableList<ConceptEntity> paths = stampViewModel.getObservableList(PATHS);
//        paths.addListener((ListChangeListener<ConceptEntity>) c -> {
//            List<String> pathStrings = paths.stream()
//                    .map(EntityFacade::description)
//                    .collect(Collectors.toList());
//            Collections.sort(pathStrings, NaturalOrder.getObjectComparator());
//            pathComboBox.getItems().setAll(pathStrings);
//        });
//
//        ObjectProperty<ConceptEntity> pathProperty = stampViewModel.getProperty(PATH);
//        pathProperty.subscribe(conceptEntity -> {
//            if (conceptEntity != null) {
//                pathComboBox.setValue(conceptEntity.description());
//            }
//        });
//
//        observableConceptVersionLatest.ifPresent(observableConceptVersion -> {
//            observableConceptVersion.pathProperty().bind(pathProperty);
//        });
//    }
//
//    private void initModuleComboBox() {
//
//        //Set the display to String
//        moduleComboBox.setConverter((new StringConverter<ConceptFacade>() {
//            @Override
//            public String toString(ConceptFacade conceptFacade) {
//                ViewCalculator viewCalculator = viewProperties.calculator();
//                return viewCalculator.getRegularDescriptionText(conceptFacade).get();
//            }
//            @Override
//            public ConceptFacade fromString(String s) {
//                return null;
//            }
//        }));
//
//        //get the modules from view model
//        ObservableList<ConceptFacade> modules = stampViewModel.getObservableList(MODULES);
//        //add listener to modules.
//        modules.addListener((ListChangeListener<ConceptFacade>) c -> {
//            viewProperties =  stampViewModel.getPropertyValue(VIEW_PROPERTIES);
//            moduleComboBox.getItems().clear();
//            moduleComboBox.getItems().addAll(modules);
//            //get the selected value for the module and set it to comboBox value.
//            ConceptEntity moduleConceptEntity = stampViewModel.getPropertyValue(MODULE);
//            moduleComboBox.setValue(moduleConceptEntity);
//        });
//
//        moduleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
//            EntityFacade entityFacade = stampViewModel.getPropertyValue(ENTITY);
//            if(newValue != null && entityFacade != null) {
//                ObservableConcept observableConcept = ObservableEntity.get(entityFacade.nid());
//                observableConceptVersionLatest = observableConcept.getSnapshot(viewProperties.calculator()).getLatestVersion();
//                observableConceptVersionLatest.ifPresent(observableConceptVersion -> observableConceptVersion.moduleProperty().set(newValue));
//            }
//        });
//    }

//    private String toFirstLetterCapitalized(State status) {
//        String statusString = status.toString();
//        return statusString.substring(0, 1).toUpperCase() + statusString.substring(1).toLowerCase();
//    }

    public void cancel(ActionEvent actionEvent) {

    }

    public void clearForm(ActionEvent actionEvent) {

    }

    public void confirm(ActionEvent actionEvent) {
        observableConceptVersionLatest.ifPresent(observableConceptVersion -> {
            Transaction.forVersion(observableConceptVersion.getVersionRecord()).ifPresentOrElse(transaction -> {
                int stampCount = transaction.commit();
                System.out.println("Transaction committed successfully "  + stampCount);
            }, () -> {
                throw new RuntimeException("Transaction for stamp does not exist " + observableConceptVersion.time());
            });
        });

    }
}
