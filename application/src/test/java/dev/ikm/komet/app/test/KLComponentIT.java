package dev.ikm.komet.app.test;

import dev.ikm.komet.framework.observable.ObservableEntity;
import dev.ikm.komet.framework.observable.ObservableField;
import dev.ikm.komet.framework.observable.ObservableSemantic;
import dev.ikm.komet.framework.observable.ObservableSemanticSnapshot;
import dev.ikm.tinkar.common.service.CachingService;
import dev.ikm.tinkar.common.service.PrimitiveData;
import dev.ikm.tinkar.common.service.ServiceKeys;
import dev.ikm.tinkar.common.service.ServiceProperties;
import dev.ikm.tinkar.coordinate.Coordinates;
import dev.ikm.tinkar.coordinate.view.ViewCoordinateRecord;
import dev.ikm.tinkar.coordinate.view.calculator.ViewCalculator;
import dev.ikm.tinkar.coordinate.view.calculator.ViewCalculatorWithCache;
import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.EntityService;
import dev.ikm.tinkar.terms.TinkarTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.UUID;

public class KLComponentIT {
    private static final Logger LOG = LoggerFactory.getLogger(KLComponentIT.class);

    public static void main(String [] args){
        setUpBefore();
        ObservableField[] fieldArray = new ObservableField[2];
        Entity entity = EntityService.get().getEntityFast(UUID.fromString("7a145080-81e2-45c5-9ac1-76aae23888ab"));

        ObservableEntity observableEntityPattern = ObservableEntity.get(entity);


        ViewCoordinateRecord viewCoordinateRecord = Coordinates.View.DefaultView();
        ViewCalculator viewCalculator = new ViewCalculatorWithCache(viewCoordinateRecord);
        viewCalculator.forEachSemanticVersionForComponentOfPattern(entity.nid(), TinkarTerm.DESCRIPTION_PATTERN.nid(),
                (semanticEntityVersion,  entityVersion1, patternEntityVersion) -> {
                    ObservableSemantic observableEntitySemantic = ObservableEntity.get(semanticEntityVersion.nid());
                    ObservableSemanticSnapshot observableSemanticSnapshot =



        });
        /**
         * TODO Load fieldDefinitions using the Pattern
         *
        FieldDefinitionRecord fieldDefinitionRecord = new FieldDefinitionRecord();
        fieldArray[0] = new ObservableField(new FieldRecord(...));
*       **/
 /*       for(int i=0 ; i < fieldArray.length ; i ++){
            KlField klField =   new KLStringFieldImplementation(fieldArray[i]);

        }*/

    }

    //@BeforeAll
    public static void setUpBefore() {
        LOG.info("Clear caches");
            File dataStore = new File(System.getProperty("user.home") + "/Solor/September2024_ConnectathonDataset_v1");
        CachingService.clearAll();
        LOG.info("Setup Ephemeral Suite: " + LOG.getName());
        LOG.info(ServiceProperties.jvmUuid());
        ServiceProperties.set(ServiceKeys.DATA_STORE_ROOT, dataStore);
        PrimitiveData.selectControllerByName("Open SpinedArrayStore");

        PrimitiveData.start();

    }

}
