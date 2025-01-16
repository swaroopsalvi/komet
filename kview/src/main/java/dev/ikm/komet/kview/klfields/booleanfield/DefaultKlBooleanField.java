package dev.ikm.komet.kview.klfields.booleanfield;

import dev.ikm.komet.framework.observable.ObservableField;
import dev.ikm.komet.framework.view.ObservableView;
import dev.ikm.komet.kview.controls.KLBooleanControl;
import dev.ikm.komet.kview.controls.KLReadOnlyStringControl;
import dev.ikm.komet.kview.klfields.BaseDefaultKlField;
import dev.ikm.komet.layout.component.version.field.KlBooleanField;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public class DefaultKlBooleanField extends BaseDefaultKlField<Boolean> implements KlBooleanField {

    public DefaultKlBooleanField(ObservableField<Boolean> observableBooleanField, ObservableView observableView, boolean isEditable) {
        super(observableBooleanField, observableView, isEditable);

        Node node;
        if (isEditable) {
            KLBooleanControl klBooleanControl = new KLBooleanControl(getTitle());
            ObjectProperty<Boolean> booleanObjectProperty = observableBooleanField.valueProperty();
            booleanObjectProperty.addListener((obs, ov, nv) -> klBooleanControl.setText(String.valueOf(nv)));
            node = klBooleanControl;
        } else {
            KLReadOnlyStringControl readOnlyStringControl = new KLReadOnlyStringControl();
            readOnlyStringControl.setText(String.valueOf(observableBooleanField.value()));
            readOnlyStringControl.setTitle(getTitle());
            node = readOnlyStringControl;
        }

        setKlWidget(node);
    }
}
