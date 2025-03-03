package dev.ikm.komet.kview.klentities;

import dev.ikm.komet.layout.component.KlComponentPane;
import dev.ikm.komet.layout.component.KlGenericComponentPane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public abstract class BaseDefaultComponentPane<T extends KlComponentPane> implements KlGenericComponentPane {
    public static final String REFERENCE_COMPONENT = "Reference Component";

    protected ObjectProperty<Node> klWidget = new SimpleObjectProperty<>();

}
