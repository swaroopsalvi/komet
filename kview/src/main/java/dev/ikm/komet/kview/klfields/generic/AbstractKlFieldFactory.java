package dev.ikm.komet.kview.klfields.generic;

import dev.ikm.komet.kview.klfields.booleanfield.KlBooleanFieldFactory;
import dev.ikm.komet.kview.klfields.floatfield.KlFloatFieldFactory;
import dev.ikm.komet.kview.klfields.integerfield.KlIntegerFieldFactory;
import dev.ikm.komet.kview.klfields.stringfield.KlStringFieldFactory;
import dev.ikm.komet.layout.component.version.field.KlFieldFactory;
import dev.ikm.tinkar.common.service.PluggableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AbstractKlFieldFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractKlFieldFactory.class);
    public static KlFieldFactory getKlFieldFactoryInstance(String conceptFacade){
        Class clasz = switch (conceptFacade){
            case "String" -> KlStringFieldFactory.class;
            case "Integer field" -> KlIntegerFieldFactory.class;
            case "Float field" -> KlFloatFieldFactory.class;
            case "Boolean field" -> KlBooleanFieldFactory.class;
            default -> null;
        };
        //   ServiceLoader<KlFieldFactory<?>> serviceLoader = PluggableService.load(clasz);
        Optional optional = PluggableService.load(clasz).findFirst();
        if(optional.isPresent()){
            return (KlFieldFactory) optional.get();
        } else {
            LOG.error(" Case not handled: " +  conceptFacade);
            throw new UnsupportedOperationException("Unsupported KL Field.");
        }
    }
}
