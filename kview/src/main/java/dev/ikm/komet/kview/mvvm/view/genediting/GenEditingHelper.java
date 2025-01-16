package dev.ikm.komet.kview.mvvm.view.genediting;

import static dev.ikm.komet.kview.mvvm.model.DataModelHelper.obtainObservableField;
import dev.ikm.komet.framework.observable.ObservableField;
import dev.ikm.komet.framework.view.ViewProperties;
import dev.ikm.komet.kview.klfields.generic.AbstractKlFieldFactory;
import dev.ikm.komet.layout.component.version.field.KlFieldFactory;
import dev.ikm.tinkar.coordinate.stamp.calculator.Latest;
import dev.ikm.tinkar.entity.FieldRecord;
import dev.ikm.tinkar.entity.SemanticEntityVersion;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to have common methods to avoid code redundancy.
 */

public class GenEditingHelper {

    private static final Logger LOG = LoggerFactory.getLogger(GenEditingHelper.class);

    /**
     *
     * @param editable specify if the field can be edited.
     * @param fieldRecord the fieldRecord to be displayed
     * @param viewProperties View Properties for the screen
     * @param semanticEntityVersionLatest Latest semantic entity version
     * @return Node that is created.
     */
    public static Node createNode( boolean editable, FieldRecord fieldRecord,
            ViewProperties viewProperties, Latest<SemanticEntityVersion> semanticEntityVersionLatest){
        LOG.info("---> dataType() " + fieldRecord.dataType().description());
        String dataType = fieldRecord.dataType().description();
        ObservableField observableFields = obtainObservableField(viewProperties, semanticEntityVersionLatest, fieldRecord);
        KlFieldFactory<?> klFieldFactory = AbstractKlFieldFactory.getKlFieldFactoryInstance(dataType);
        return klFieldFactory.create(observableFields, viewProperties.nodeView(), editable).klWidget();
    }

}
