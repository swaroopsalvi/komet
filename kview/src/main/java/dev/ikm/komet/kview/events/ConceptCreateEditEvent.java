package dev.ikm.komet.kview.events;

import dev.ikm.komet.framework.events.Evt;
import dev.ikm.komet.framework.events.EvtType;
import dev.ikm.komet.framework.view.ViewProperties;
import dev.ikm.komet.kview.mvvm.model.DescrName;
import dev.ikm.tinkar.common.id.PublicId;

public class ConceptCreateEditEvent extends Evt {

    public static final EvtType<ConceptCreateEditEvent> CONCEPT_EVENT = new EvtType<>(Evt.ANY, "CONCEPT_EVENT");
    public static final EvtType<ConceptCreateEditEvent> ADD_CONCEPT_FQN = new EvtType<>(CONCEPT_EVENT, "ADD_CONCEPT_FQN");
    public static final EvtType<ConceptCreateEditEvent> EDIT_CONCEPT_FQN = new EvtType<>(CONCEPT_EVENT, "EDIT_CONCEPT_FQN");
    public static final EvtType<ConceptCreateEditEvent> ADD_CONCEPT_OTHER_NAME = new EvtType<>(CONCEPT_EVENT, "ADD_CONCEPT_OTHER_NAME");
    public static final EvtType<ConceptCreateEditEvent> EDIT_CONCEPT_OTHER_NAME = new EvtType<>(CONCEPT_EVENT, "EDIT_CONCEPT_OTHER_NAME");

    private PublicId publicId;
    private ViewProperties viewProperties;
    private DescrName descrName;


    /**
     * Constructs a prototypical Event.
     *
     * @param source    the object on which the Event initially occurred
     * @param eventType
     * @param viewProperties
     * @throws IllegalArgumentException if source is null
     */
    public ConceptCreateEditEvent(Object source, EvtType eventType, ViewProperties viewProperties) {
        super(source, eventType);
        this.viewProperties = viewProperties;
        this.publicId = null;
        this.descrName = null;
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source    the object on which the Event initially occurred
     * @param eventType
     * @param viewProperties
     * @param publicId the public id that has to be edited.
     * @param descrName model for the concept
     * @throws IllegalArgumentException if source is null
     */
    public ConceptCreateEditEvent(Object source, EvtType eventType, ViewProperties viewProperties, PublicId publicId,  DescrName descrName) {
        super(source, eventType);
        this.viewProperties = viewProperties;
        this.publicId = publicId;
        this.descrName = descrName;
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source    the object on which the Event initially occurred
     * @param eventType
     * @param publicId the public id that has to be edited.
     * @throws IllegalArgumentException if source is null
     */
    public ConceptCreateEditEvent(Object source, EvtType eventType, PublicId publicId) {
        super(source, eventType);
        this.publicId = publicId;
        this.viewProperties = null;
        this.descrName = null;
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source    the object on which the Event initially occurred
     * @param eventType
     * @param descrName model for the concept
     * @throws IllegalArgumentException if source is null
     */
    public ConceptCreateEditEvent(Object source, EvtType eventType, DescrName descrName) {
        super(source, eventType);
        this.publicId = null;
        this.viewProperties = null;
        this.descrName = descrName;
    }

    public PublicId getPublicId() {
        return publicId;
    }

    public ViewProperties getViewProperties() {
        return viewProperties;
    }

    public void setViewProperties(ViewProperties viewProperties) {
        this.viewProperties = viewProperties;
    }

    public DescrName getDescrName() {
        return descrName;
    }
}
