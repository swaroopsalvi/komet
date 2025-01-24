package dev.ikm.komet.kview.klfields.componentfield;

import dev.ikm.komet.framework.Identicon;
import dev.ikm.komet.framework.observable.ObservableField;
import dev.ikm.komet.framework.view.ObservableView;
import dev.ikm.komet.kview.controls.KLComponentSetControl;
import dev.ikm.komet.kview.controls.KLReadOnlyComponentControl;
import dev.ikm.komet.kview.klfields.BaseDefaultKlField;
import dev.ikm.komet.layout.component.version.field.KlComponentSetField;
import dev.ikm.tinkar.common.id.IntIdSet;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.EntityVersion;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

import java.util.Set;

public class DefaultKlComponentSetField extends BaseDefaultKlField<Set<Entity<EntityVersion>>> implements KlComponentSetField<Entity<EntityVersion>, EntityVersion> {

    public DefaultKlComponentSetField(ObservableField<Set<Entity<EntityVersion>>> observableComponentSetField, ObservableView observableView, boolean isEditable) {
        super(observableComponentSetField, observableView, isEditable);
        Node node;
        if (isEditable) {
            KLComponentSetControl klComponentSetControl = new KLComponentSetControl();
//            klComponentSetControl.entitiesProperty().bindBidirectional(observableComponentSetField.valueProperty());
            klComponentSetControl.setTitle(getTitle());

            Set<Entity<EntityVersion>> entities = field().value();
            klComponentSetControl.getEntitiesList().addAll(entities);

            node = klComponentSetControl;
        } else {
            ObjectProperty<Set<Entity<EntityVersion>>> observablePropertySet = observableComponentSetField.valueProperty();
            IntIdSet entities = (IntIdSet) observablePropertySet.get();
            KLReadOnlyComponentControl readOnlyComponentControl = new KLReadOnlyComponentControl();
            entities.forEach(entity -> {
                readOnlyComponentControl.setTitle(entity.entityDataType().name());
                readOnlyComponentControl.setText(entity.description());
                readOnlyComponentControl.setIcon(Identicon.generateIdenticonImage(entity.publicId()));

            });
            node = readOnlyComponentControl;





        }
        setKlWidget(node);
    }


}
