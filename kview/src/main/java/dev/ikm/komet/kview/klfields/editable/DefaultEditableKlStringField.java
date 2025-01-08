package dev.ikm.komet.kview.klfields.editable;

import dev.ikm.komet.framework.observable.ObservableField;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import org.carlfx.cognitive.loader.FXMLMvvmLoader;
import org.carlfx.cognitive.loader.JFXNode;

public class DefaultEditableKlStringField implements EditableKlStringField {
    private ObservableField<String> field = new ObservableField<>(null, false);
    private Node node;

    @Override
    public ObservableField<String> field() {
        return field;
    }

    @Override
    public Node klWidget() {
        if (node == null) {
            JFXNode<Pane, Void> jfxNode = FXMLMvvmLoader.make(this.getClass().getResource("/dev/ikm/komet/kview/controls/editable-value-field.fxml"));
            Pane componentRow = jfxNode.node();
            // update field's meaning title label
            Label fieldMeaning = (Label) componentRow.lookup(".semantic-field-type-label");
            TextField fieldValue = (TextField) componentRow.lookup(".semantic-field-value");
            fieldValue.textProperty().bindBidirectional(field().valueProperty());
            field().fieldProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // when text changes update UI
                    fieldMeaning.setTooltip(new Tooltip(newValue.purpose().description()));
                    fieldMeaning.setText(newValue.meaning().description());
                    fieldValue.setText(newValue.value());
                }
            });

            fieldMeaning.setTooltip(new Tooltip(field().field().purpose().description()));
            fieldMeaning.setText(field().field().meaning().description());
            fieldValue.setText(field().field().value());

            node = componentRow;
        }
        return node;
    }
}
