package dev.ikm.komet.kview.mvvm.view.properties;

import static dev.ikm.komet.kview.mvvm.viewmodel.StampViewModel2.StampProperties.*;

import dev.ikm.komet.framework.observable.ObservableConcept;
import dev.ikm.komet.framework.observable.ObservableConceptSnapshot;
import dev.ikm.komet.framework.observable.ObservableEntity;
import dev.ikm.komet.framework.view.*;
import dev.ikm.komet.kview.mvvm.viewmodel.*;
import dev.ikm.tinkar.terms.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.carlfx.cognitive.loader.*;


public class StampAddController {

    @FXML
    private Button submitButton;

    @FXML
    private Button resetButton;

    @FXML
    private ComboBox<ConceptFacade> statusComboBox;

    @FXML
    private ComboBox<ConceptFacade> moduleComboBox;

    @FXML
    private ComboBox<ConceptFacade> pathComboBox;

    @InjectViewModel
    private StampViewModel2 stampViewModel;


    @FXML
    public void initialize() {

        initModuleComboBox();
        initPathComboBox();
        initStatusComboBox();

        BooleanProperty isStampValuesTheSame = stampViewModel.getProperty(IS_STAMP_VALUES_THE_SAME);
        submitButton.disableProperty().bind(isStampValuesTheSame);
        resetButton.disableProperty().bind(isStampValuesTheSame);

    }

    private ViewProperties getViewProperties() {
        return stampViewModel.getViewProperties();
    }

    private void initStatusComboBox() {
        statusComboBox.setItems(stampViewModel.getObservableList(STATUSES));
        statusComboBox.setConverter(createStringConverter());
        statusComboBox.valueProperty().bindBidirectional(stampViewModel.getProperty(STATUS));
        statusComboBox.valueProperty().subscribe((oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                EntityFacade entityFacade = stampViewModel.getEntityFacade();
                ObservableConcept observableConcept = ObservableEntity.get(entityFacade.nid());
                ObservableConceptSnapshot observableConceptSnapshot = observableConcept.getSnapshot(getViewProperties().calculator());
                observableConceptSnapshot.getLatestVersion().ifPresent( observableConceptVersion -> {
                    observableConceptVersion.stateProperty().set(State.fromConcept(newValue));
                });
            }
        });
    }

    private void initPathComboBox() {
        pathComboBox.setItems(stampViewModel.getObservableList(PATHS));
        pathComboBox.setConverter(createStringConverter());
        pathComboBox.valueProperty().bindBidirectional(stampViewModel.getProperty(PATH));
        pathComboBox.valueProperty().subscribe(( oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                EntityFacade entityFacade = stampViewModel.getEntityFacade();
                ObservableConcept observableConcept = ObservableEntity.get(entityFacade.nid());
                ObservableConceptSnapshot observableConceptSnapshot = observableConcept.getSnapshot(getViewProperties().calculator());
                observableConceptSnapshot.getLatestVersion().ifPresent( observableConceptVersion -> {
                    observableConceptVersion.pathProperty().set(newValue);
                });
            }

        });
    }

    private void initModuleComboBox() {
        moduleComboBox.setItems(stampViewModel.getObservableList(MODULES));
        moduleComboBox.setConverter(createStringConverter());
        moduleComboBox.valueProperty().bindBidirectional(stampViewModel.getProperty(MODULE));
        moduleComboBox.valueProperty().subscribe((oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                EntityFacade entityFacade = stampViewModel.getEntityFacade();
                ObservableConcept observableConcept = ObservableEntity.get(entityFacade.nid());
                ObservableConceptSnapshot observableConceptSnapshot = observableConcept.getSnapshot(getViewProperties().calculator());
                observableConceptSnapshot.getLatestVersion().ifPresent( observableConceptVersion -> {
                    observableConceptVersion.moduleProperty().set(newValue);
                });
            }
        });
    }

    private StringConverter<ConceptFacade> createStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(ConceptFacade conceptFacade) {
                return conceptFacade == null ? null : getViewProperties().calculator().getPreferredDescriptionTextWithFallbackOrNid(conceptFacade.nid());
            }

            @Override
            public ConceptFacade fromString(String s) {
                return null;
            }
        };
    }


    public StampViewModel2 getStampViewModel() { return stampViewModel; }

    @FXML
    public void cancelForm(ActionEvent actionEvent) {
        stampViewModel.cancel();
    }

    @FXML
    public void resetForm(ActionEvent actionEvent) { stampViewModel.resetForm(actionEvent); }

    public void submit(ActionEvent actionEvent) { stampViewModel.save(); }
}
