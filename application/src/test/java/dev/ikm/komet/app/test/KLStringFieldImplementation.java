package dev.ikm.komet.app.test;

import dev.ikm.komet.framework.observable.ObservableField;
import dev.ikm.komet.layout.component.version.field.KlStringField;
import javafx.scene.Node;

public class KLStringFieldImplementation implements KlStringField{

    ObservableField<String> field;
    public KLStringFieldImplementation(ObservableField observableField) {
        this.field = observableField;
    }

    /**
     * @return
     */
    @Override
    public ObservableField<String> field() {
        return field;

    }

    public Node makeFieldNode() {
        return sceneGraphNode();
    }



    /**
     * @param <SGN>
     * @return
     */
    @Override
    public <SGN extends Node> SGN sceneGraphNode() {
        return KlStringField.super.sceneGraphNode();
    }
}
