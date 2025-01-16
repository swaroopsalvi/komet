package dev.ikm.komet.kview.klfields.generic;

import dev.ikm.komet.kview.klfields.booleanfield.KlBooleanFieldFactory;
import dev.ikm.komet.kview.klfields.floatfield.KlFloatFieldFactory;
import dev.ikm.komet.kview.klfields.integerfield.KlIntegerFieldFactory;
import dev.ikm.komet.kview.klfields.stringfield.KlStringFieldFactory;
import dev.ikm.komet.layout.component.version.field.KlFieldFactory;

public class AbstractKlFieldFactory {

    public static KlFieldFactory getKlFieldFactoryInstance(String conceptFacade){
        return switch (conceptFacade){
            case "String" -> new KlStringFieldFactory();
            case "Integer field" -> new KlIntegerFieldFactory();
            case "Float field" -> new KlFloatFieldFactory();
            case "Boolean field" -> new KlBooleanFieldFactory();
            default -> null;
        };

    }
}
