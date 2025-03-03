package dev.ikm.komet.kview.klentities;

import dev.ikm.komet.framework.observable.ObservableConcept;
import dev.ikm.komet.framework.view.ObservableView;
import dev.ikm.komet.layout.KlGadget;
import dev.ikm.komet.layout.KlWidget;
import dev.ikm.komet.layout.component.KlConceptPane;
import dev.ikm.komet.layout.component.KlConceptPaneFactory;
import dev.ikm.komet.layout.context.KlContextFactory;
import dev.ikm.komet.layout.preferences.KlPreferencesFactory;
import dev.ikm.komet.preferences.KometPreferences;

public class KlConceptComponentFactory implements KlConceptPaneFactory<KlConceptPane, ObservableConcept> {

    protected final ObservableConcept observableConcept;
    protected final ObservableView observableView;

    public KlConceptComponentFactory(ObservableConcept observableConcept, ObservableView observableView) {
        this.observableConcept = observableConcept;
        this.observableView = observableView;
    }

    /**
     * Retrieves the class type of the observable entity class that this factory creates components for.
     * Enables runtime access to the generic entity class that would otherwise be erased.
     *
     * @return A {@link Class} object representing the type of the observable entity (OE).
     */
    @Override
    public Class<ObservableConcept> entityType() {
        return ObservableConcept.class;
    }

    /**
     * Creates an instance of type T using the provided KlPreferencesFactory.
     *
     * @param preferencesFactory an instance of KlPreferencesFactory used to provide
     *                           necessary preferences for creating the object.
     * @return an instance of type T created using the given preferencesFactory.
     */
    @Override
    public KlConceptPane create(KlPreferencesFactory preferencesFactory) {
        return new DefaultKlConceptComponentPane(true);
    }

    /**
     * Creates an instance of type T using the provided KlPreferencesFactory
     * and KlContextFactory. This method utilizes the preferences and context
     * configurations to instantiate the desired object.
     *
     * @param preferencesFactory an instance of KlPreferencesFactory used to
     *                           provide necessary preferences for creating the object.
     * @param contextFactory     an instance of KlContextFactory used to provide
     *                           the contextual information required for object creation.
     * @return an instance of type T created using the given preferencesFactory
     * and contextFactory.
     */
    @Override
    public KlConceptPane createWithContext(KlPreferencesFactory preferencesFactory, KlContextFactory contextFactory) {
        return null;
    }

    /**
     * Restores an instance of type T using the provided preferences.
     *
     * @param preferences an instance of KometPreferences that contains the
     *                    configuration or state required to restore the object.
     * @return an instance of type T restored using the given preferences.
     */
    @Override
    public KlConceptPane restore(KometPreferences preferences) {
        return null;
    }

    /**
     * Retrieves the KlGadget interface of the KlGadget produced by the factory.
     *
     * @return A {@link Class} object representing the class type of the field
     * interface extending {@link KlWidget}.
     */
    @Override
    public Class<KlConceptPane> klInterfaceClass() {
        return KlConceptPane.class;
    }

    /**
     * Retrieves the concrete class of the KlGadget
     * that is produced by the factory.
     *
     * @return A {@link Class} object representing the class type of the implementation
     * of {@link KlGadget} associated with this factory.
     */
    @Override
    public Class<? extends KlConceptPane> klImplementationClass() {
        return DefaultKlConceptComponentPane.class;
    }
}
