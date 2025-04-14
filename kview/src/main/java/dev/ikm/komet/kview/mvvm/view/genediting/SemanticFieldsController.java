/*
 * Copyright © 2015 Integrated Knowledge Management (support@ikm.dev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ikm.komet.kview.mvvm.view.genediting;


import static dev.ikm.komet.kview.events.genediting.GenEditingEvent.PUBLISH;
import static dev.ikm.komet.kview.klfields.KlFieldHelper.calculteHashValue;
import static dev.ikm.komet.kview.klfields.KlFieldHelper.generateHashValue;
import static dev.ikm.komet.kview.klfields.KlFieldHelper.retrieveCommittedLatestVersion;
import static dev.ikm.komet.kview.mvvm.viewmodel.FormViewModel.CURRENT_JOURNAL_WINDOW_TOPIC;
import static dev.ikm.komet.kview.mvvm.viewmodel.FormViewModel.VIEW_PROPERTIES;
import static dev.ikm.komet.kview.mvvm.viewmodel.GenEditingViewModel.SEMANTIC;
import static dev.ikm.tinkar.provider.search.Indexer.FIELD_INDEX;
import static dev.ikm.tinkar.terms.TinkarTerm.ANONYMOUS_CONCEPT;
import static dev.ikm.tinkar.terms.TinkarTerm.INTEGER_FIELD;
import dev.ikm.komet.framework.events.EvtBusFactory;
import dev.ikm.komet.framework.observable.ObservableField;
import dev.ikm.komet.framework.view.ViewProperties;
import dev.ikm.komet.kview.events.genediting.GenEditingEvent;
import dev.ikm.komet.kview.klfields.KlFieldHelper;
import dev.ikm.tinkar.common.id.IntIds;
import dev.ikm.tinkar.common.service.TinkExecutor;
import dev.ikm.tinkar.coordinate.stamp.calculator.Latest;
import dev.ikm.tinkar.coordinate.stamp.calculator.StampCalculator;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.EntityVersion;
import dev.ikm.tinkar.entity.FieldDefinitionForEntity;
import dev.ikm.tinkar.entity.PatternEntityVersion;
import dev.ikm.tinkar.entity.SemanticEntityVersion;
import dev.ikm.tinkar.entity.SemanticVersionRecord;
import dev.ikm.tinkar.entity.StampRecord;
import dev.ikm.tinkar.entity.transaction.CommitTransactionTask;
import dev.ikm.tinkar.entity.transaction.Transaction;
import dev.ikm.tinkar.terms.EntityFacade;
import dev.ikm.tinkar.terms.TinkarTerm;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import org.carlfx.cognitive.loader.InjectViewModel;
import org.carlfx.cognitive.viewmodel.ValidationViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SemanticFieldsController {

    private static final Logger LOG = LoggerFactory.getLogger(SemanticFieldsController.class);

    @FXML
    private VBox editFieldsVBox;
    @FXML
    private Button cancelButton;

    @FXML
    private Button clearFormButton;

    @FXML
    private Button submitButton;

    @InjectViewModel
    private ValidationViewModel semanticFieldsViewModel;

    private List<ObservableField<?>> observableFields = new ArrayList<>();

    private boolean updateStampVersions;

    private List<Node> nodes = new ArrayList<>();

    private int committedHash;

    private void enableDisableSubmitButton(Object value){
        if (value == null || value.toString().isEmpty()){
            submitButton.setDisable(true);
        } else {
            enableDisableSubmitButton();
        }
    }

    private void enableDisableSubmitButton(){
        //Disable submit button if any of the fields are blank.
        boolean disabled = checkForEmptyFields();
        if(!disabled){
            int uncommittedHash = calculteHashValue(observableFields);
            disabled = (committedHash == uncommittedHash);
        }
        submitButton.setDisable(disabled);
    }

    /**
     * This method checks for empty/blank/null fields
     * @return invalid
     */
    private boolean checkForEmptyFields() {
        AtomicBoolean invalid = new AtomicBoolean(false);
        observableFields.forEach(observableField -> {
            if (!invalid.get()) {
                invalid.set((observableField.value() == null || observableField.value().toString().isEmpty()));
            }
        });
        return invalid.get();
    }

    private void processCommittedValues() {
        EntityFacade semantic = semanticFieldsViewModel.getPropertyValue(SEMANTIC);
        Latest<SemanticEntityVersion> semanticEntityVersionLatest = retrieveCommittedLatestVersion(semantic,getViewProperties());
        committedHash = generateHashValue(semanticEntityVersionLatest, getViewProperties());
    }

    private void fieldPropertyChangeListner(){
        // This flag is used to avoid unnecessary calling for
        // method when value for other listeners is updated.
        // It is similar to refreshProperty in Observable interface.
        if (updateStampVersions) {
            updateStampVersionsNidsForAllFields();
        }
    };

    @FXML
    private void initialize() {
        // clear all semantic details.
        editFieldsVBox.setSpacing(8.0);
        editFieldsVBox.getChildren().clear();
        updateStampVersions = true;
        submitButton.setDisable(true);
        EntityFacade semantic = semanticFieldsViewModel.getPropertyValue(SEMANTIC);
        if (semantic != null) {
            StampCalculator stampCalculator = getViewProperties().calculator().stampCalculator();
            Latest<SemanticEntityVersion> semanticEntityVersionLatest = stampCalculator.latest(semantic.nid());
            if (semanticEntityVersionLatest.isPresent()) {
                //Set the hascode for the committed values.
                processCommittedValues();

                // Populate the Semantic Details
                // Displaying editable controls and populating the observable fields array list.
                observableFields.addAll(KlFieldHelper
                        .generateObservableFieldsAndNodes(getViewProperties(),
                                nodes,
                                semanticEntityVersionLatest, true));
                editFieldsVBox.getChildren().clear();
                observableFields.forEach(observableField -> {
                 observableField.valueProperty()
                                        .addListener(observable -> {
                                            enableDisableSubmitButton(observableField.valueProperty().get());
                                            if (!submitButton.isDisable()) {
                                                observableField.writeToDataBase();
                                            }
                                        });
                    //Add listener for fieldProperty of each field to check when data is modified.
                    observableField.fieldProperty().addListener(observable -> fieldPropertyChangeListner());
                });

            } else {
                // TODO Add a new semantic based on a pattern (blank fields).
            }
        }

        // subscribe to changes... if the FIELD_INDEX is -1 or unset, then the user clicked the
        //  pencil icon and wants to edit all the fields
        // if the FIELD_INDEX is >= 0 then the user chose the context menu of a single field
        //  to edit that field
        semanticFieldsViewModel.getObjectProperty(FIELD_INDEX).subscribe(fieldIndex -> {
            int fieldIdx = (int)fieldIndex;
            editFieldsVBox.getChildren().clear();

            // single field to edit
            if (fieldIdx >= 0) {
                editFieldsVBox.getChildren().add(nodes.get(fieldIdx));
            } else {
                // all fields to edit
                for (int i = 0; i < nodes.size(); i++) {
                    editFieldsVBox.getChildren().add(nodes.get(i));
                    if (i < nodes.size() - 1) {
                        editFieldsVBox.getChildren().add(createSeparator());
                    }
                }
            }
        });
    }

    /***
     * This method updates stamps for all the fields to avoid contradictions.
     * An alternate approach could be to use Semantic contradictions
     * for each field and pick up the latest value for each contradiction?
     */
    private void updateStampVersionsNidsForAllFields() {
        EntityFacade semantic = semanticFieldsViewModel.getPropertyValue(SEMANTIC);
        StampCalculator stampCalculator = getViewProperties().calculator().stampCalculator();
        Latest<SemanticEntityVersion> semanticEntityVersionLatest = stampCalculator.latest(semantic.nid());
        updateStampVersions = false;
        semanticEntityVersionLatest.ifPresent(ver -> {
            int latestStampNid = ver.stamp().nid();
            observableFields.forEach(observableField -> {
                 //Update the stampNid with the latest stamp nid value.
                observableField.fieldProperty().set(observableField.field().withVersionStampNid(latestStampNid));
            });
        });
        updateStampVersions = true;
    }

    private static Separator createSeparator() {
        Separator separator = new Separator();
        separator.getStyleClass().add("field-separator");
        return separator;
    }

    public ViewProperties getViewProperties() {
        return semanticFieldsViewModel.getPropertyValue(VIEW_PROPERTIES);
    }

    @FXML
    private void cancel(ActionEvent actionEvent) {
        actionEvent.consume();
        // if previous state was closed cancel will close properties bump out.
        // else show
    }

    @FXML
    private void clearForm(ActionEvent actionEvent) {
        EntityFacade semantic = semanticFieldsViewModel.getPropertyValue(SEMANTIC);
        Latest<SemanticEntityVersion>  latestCommitted =  retrieveCommittedLatestVersion(semantic, getViewProperties());
        latestCommitted.ifPresent(semanticEntityVersion -> {
            if(semanticEntityVersion.committed()){
                updateFieldValues(semanticEntityVersion);
            } else {
                EntityFacade pattern = EntityFacade.make(semanticEntityVersion.pattern().nid());
                Latest<PatternEntityVersion> patternEntityVersionLatest = getViewProperties().calculator().latest(pattern.nid());
                updateFieldValues(patternEntityVersionLatest.get());
            }
        });

        actionEvent.consume();
    }

    private void updateFieldValues(EntityVersion entityVersion) {
        if(entityVersion instanceof PatternEntityVersion patternEntityVersion) {
            for (int i = 0; i < patternEntityVersion.fieldDefinitions().size(); i++) {
                Object object = null;
                FieldDefinitionForEntity fieldDefinitionForEntity = patternEntityVersion.fieldDefinitions().get(i);
                if (fieldDefinitionForEntity.dataTypeNid() == TinkarTerm.COMPONENT_FIELD.nid()) {
                    object = ANONYMOUS_CONCEPT;
                } else if (fieldDefinitionForEntity.dataTypeNid() == TinkarTerm.STRING_FIELD.nid()
                        || fieldDefinitionForEntity.dataTypeNid() == TinkarTerm.STRING.nid()) {
                    object = "";
                } else if (fieldDefinitionForEntity.dataTypeNid() == INTEGER_FIELD.nid()) {
                    object = 0;
                } else if (fieldDefinitionForEntity.dataTypeNid() == TinkarTerm.FLOAT_FIELD.nid()) {
                    object = 0.0F;
                } else if (fieldDefinitionForEntity.dataTypeNid() == TinkarTerm.BOOLEAN_FIELD.nid()) {
                    object = false;
                } else if (fieldDefinitionForEntity.dataTypeNid() == TinkarTerm.COMPONENT_ID_LIST_FIELD.nid()) {
                    object = IntIds.list.empty();
                } else if (fieldDefinitionForEntity.dataTypeNid() == TinkarTerm.COMPONENT_ID_SET_FIELD.nid()) {
                    object = IntIds.set.empty();
                }
                ObservableField observableField = observableFields.get(i);
                observableField.valueProperty().setValue(object);
            }
        } else if (entityVersion instanceof SemanticEntityVersion semanticEntityVersion) {
            for(int i = 0; i < semanticEntityVersion.fieldValues().size(); i++){
                Object object = semanticEntityVersion.fieldValues().get(i);
                ObservableField observableField = observableFields.get(i);
                observableField.valueProperty().setValue(object);
            }

        }
    }

    @FXML
    public void submit(ActionEvent actionEvent) {
        cancelButton.requestFocus();
        //create new list for passing to the event.
        List<ObservableField<?>> list = new ArrayList<>(observableFields);

        //Get the semantic need to pass along with event for loading values across Opened Semantics.
        EntityFacade semantic = semanticFieldsViewModel.getPropertyValue(SEMANTIC);
        StampCalculator stampCalculator = getViewProperties().calculator().stampCalculator();
        Latest<SemanticEntityVersion> semanticEntityVersionLatest = stampCalculator.latest(semantic.nid());
        semanticEntityVersionLatest.ifPresent(semanticEntityVersion -> {
            StampRecord stamp = Entity.getStamp(semanticEntityVersion.stampNid());
            SemanticVersionRecord version = Entity.getVersionFast(semantic.nid(), stamp.nid());
            Transaction.forVersion(version).ifPresentOrElse(transaction -> {
                commitTransactionTask(transaction);
                //EventBus implementation changes to refresh the details area if commit successful
                EvtBusFactory.getDefaultEvtBus().publish(semanticFieldsViewModel.getPropertyValue(CURRENT_JOURNAL_WINDOW_TOPIC), new GenEditingEvent(actionEvent.getSource(), PUBLISH, list, semantic.nid()));
            }, () -> {
                //TODO this is a temp alert / workaround till we figure how to reload transactions across multiple restarts of app.
                LOG.error("Unable to commit: Transaction for the given version does not exist.");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Transaction for current changes does not exist.", ButtonType.OK);
                alert.setHeaderText("Unable to Commit transaction.");
                alert.showAndWait();
            });
        });
        processCommittedValues();
        enableDisableSubmitButton();
    }

    private void commitTransactionTask(Transaction transaction) {
        CommitTransactionTask commitTransactionTask = new CommitTransactionTask(transaction);
        TinkExecutor.threadPool().submit(commitTransactionTask);
    }
}
