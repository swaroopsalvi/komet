package dev.ikm.komet.kview.klentities;

import dev.ikm.komet.framework.observable.ObservableEntity;
import dev.ikm.komet.layout.component.KlComponentPane;
import dev.ikm.komet.layout.component.multi.KlMultiComponentPane;
import javafx.collections.ObservableList;

public class BaseDefaultMultiComponentPane implements KlMultiComponentPane<ObservableEntity> {
    /**
     * Retrieves the list of observable entities associated with this pane.
     *
     * @return an ObservableList of ObservableEntity objects, representing the entities and their versions contained within this pane.
     */
    @Override
    public ObservableList<ObservableEntity> entities() {
        return null;
    }

    /**
     * Retrieves the list of single pane components associated with this multi-component pane.
     *
     * @return an ObservableList of KlComponentPane objects, representing the individual component panes contained within this multi-component pane.
     */
    @Override
    public ObservableList<KlComponentPane<ObservableEntity>> klComponents() {
        return null;
    }
}
