package dev.ikm.komet.kview.klentities;

import dev.ikm.komet.framework.Identicon;
import dev.ikm.komet.framework.observable.ObservableConcept;
import dev.ikm.komet.framework.observable.ObservableEntity;
import dev.ikm.komet.kview.controls.KLComponentControl;
import dev.ikm.komet.kview.controls.KLReadOnlyComponentControl;
import dev.ikm.komet.layout.component.KlConceptPane;
import dev.ikm.tinkar.terms.EntityProxy;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public class DefaultKlConceptComponentPane //extends BaseDefaultComponentPane
        implements KlConceptPane {

    public static final String REFERENCE_COMPONENT = "Reference Component";

    protected ObjectProperty<Node> klWidget = new SimpleObjectProperty<>();

    ObservableEntity observableEntity;

    public DefaultKlConceptComponentPane(boolean isEditable) {
        Node node;
        this.observableEntity =  observableEntity;
        EntityProxy entity = observableConcept().toProxy();
        if(isEditable){
            KLComponentControl klComponentControl = new KLComponentControl();
            // title
            klComponentControl.setTitle(REFERENCE_COMPONENT);

            klComponentControl.setEntity(entity);
            klComponentControl.entityProperty().subscribe(newEntity -> {
                ObservableConcept newObservableConcept = ObservableEntity.get(newEntity.nid());
                conceptProperty().setValue(newObservableConcept);
            });
            node = klComponentControl;
        } else {
            KLReadOnlyComponentControl klReadOnlyComponentControl = new KLReadOnlyComponentControl();
            //title
            klReadOnlyComponentControl.setTitle(REFERENCE_COMPONENT);
            // text
            klReadOnlyComponentControl.setText(entity.description());

            // icon
            klReadOnlyComponentControl.setIcon(Identicon.generateIdenticonImage(conceptProperty().get().publicId()));

            // Listen and update when EntityProxy changes
            conceptProperty().subscribe(newEntity -> {
                klReadOnlyComponentControl.setText(newEntity.description());
                klReadOnlyComponentControl.setIcon(Identicon.generateIdenticonImage(componentProperty().get().publicId()));
            });

            node = klReadOnlyComponentControl;
        }

        setKlWidget(node);
    }


    /**
     * Provides access to the JavaFX {@code ObjectProperty} holding the observable entity
     * associated with this component pane. This property allows for retrieving and observing
     * changes to the underlying component.
     *
     * @return the {@code ObjectProperty} encapsulating the observable component of type {@code OE}
     */
    @Override
    public ObjectProperty<ObservableConcept> componentProperty() {
        return this.conceptProperty();
    }

    // -- klWidget
    protected void setKlWidget(Node klWidget) { this.klWidget.set(klWidget); }
}
