/*
 * Copyright © 2015 Integrated Knowledge Management (support@ikm.dev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ikm.komet.amplify.lidr.om;

import dev.ikm.komet.amplify.data.om.SemanticDetail;
import dev.ikm.komet.amplify.data.persistence.SemanticWriter;
import dev.ikm.komet.framework.panel.axiom.LogicalOperatorsForVertex;
import dev.ikm.komet.framework.view.ObservableView;
import dev.ikm.komet.framework.view.ViewProperties;
import dev.ikm.komet.framework.window.WindowSettings;
import dev.ikm.komet.preferences.KometPreferences;
import dev.ikm.komet.preferences.KometPreferencesImpl;
import dev.ikm.tinkar.common.id.IntIdSet;
import dev.ikm.tinkar.common.id.IntIds;
import dev.ikm.tinkar.common.id.PublicId;
import dev.ikm.tinkar.common.id.PublicIds;
import dev.ikm.tinkar.common.service.PrimitiveData;
import dev.ikm.tinkar.common.util.uuid.UuidUtil;
import dev.ikm.tinkar.component.Concept;
import dev.ikm.tinkar.component.graph.DiTree;
import dev.ikm.tinkar.component.graph.Vertex;
import dev.ikm.tinkar.coordinate.Coordinates;
import dev.ikm.tinkar.coordinate.language.LanguageCoordinateRecord;
import dev.ikm.tinkar.coordinate.navigation.NavigationCoordinateRecord;
import dev.ikm.tinkar.coordinate.navigation.calculator.NavigationCalculator;
import dev.ikm.tinkar.coordinate.navigation.calculator.NavigationCalculatorWithCache;
import dev.ikm.tinkar.coordinate.stamp.StampCoordinateRecord;
import dev.ikm.tinkar.coordinate.stamp.calculator.RelativePosition;
import dev.ikm.tinkar.coordinate.stamp.calculator.StampCalculator;
import dev.ikm.tinkar.entity.*;
import dev.ikm.tinkar.terms.ConceptFacade;
import dev.ikm.tinkar.terms.EntityFacade;
import dev.ikm.tinkar.terms.EntityProxy;
import dev.ikm.tinkar.terms.TinkarTerm;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.ikm.komet.amplify.lidr.om.LidrRecord.*;

public class DataModelHelper {
    public static final EntityProxy.Concept METHOD_TYPE = EntityProxy.Concept.make(null, UuidUtil.fromSNOMED("260686004"));
    public static final EntityProxy.Concept SYSTEM = EntityProxy.Concept.make(null, UuidUtil.fromSNOMED("246333005")); // UuidUtil.fromSNOMED("704327008"));
    public static final EntityProxy.Concept ANALYTE = EntityProxy.Concept.make(null, UUID.fromString("4d943091-fd91-33d8-9291-630ba22f37d5")); //UUID.fromString("8c9214df-511c-36ba-bd5d-f4d38ce25f2f"));
    public static final EntityProxy.Pattern SCALE_ROLETYPE = EntityProxy.Pattern.make(null, UuidUtil.fromSNOMED("370132008"));
    public static final EntityProxy.Pattern PROPERTY_ROLETYPE = EntityProxy.Pattern.make(null, UuidUtil.fromSNOMED("370130000"));
    public static final EntityProxy.Pattern COMPONENT_ROLETYPE = EntityProxy.Pattern.make(null, UuidUtil.fromSNOMED("246093002"));
    public static final EntityProxy.Pattern TIME_ASPECT_ROLETYPE = EntityProxy.Pattern.make(null, UuidUtil.fromSNOMED("370134009"));
    public static final EntityProxy.Pattern METHOD_TYPE_ROLETYPE = EntityProxy.Pattern.make(null, UuidUtil.fromSNOMED("260686004"));
    public static final EntityProxy.Pattern LIDR_RECORD_PATTERN = EntityProxy.Pattern.make(null, UUID.fromString("c3d52f47-0565-5cfb-9b0b-d7501a33b35d"));
    public static final EntityProxy.Pattern MANUFACTURED_BY = EntityProxy.Pattern.make(PublicIds.of("505db286-0c93-3b5e-bc89-ec5182280656"));
    public static final EntityProxy.Pattern DIAGNOSTIC_DEVICE_PATTERN = EntityProxy.Pattern.make(PublicIds.of("a507b3c7-eadb-5d54-84c0-c44f3155d0bc"));
    public static final EntityProxy.Pattern INSTRUMENT_EQUIPMENT_PATTERN = EntityProxy.Pattern.make(PublicIds.of("d7e1e67b-7ab6-5275-ae20-5412cd2d4731"));

    public static ObservableView viewPropertiesNode() {
        // TODO how do we get a viewProperties?
        KometPreferences appPreferences = KometPreferencesImpl.getConfigurationRootPreferences();
        KometPreferences windowPreferences = appPreferences.node("main-komet-window");
        WindowSettings windowSettings = new WindowSettings(windowPreferences);
        ViewProperties viewProperties = windowSettings.getView().makeOverridableViewProperties();
        return viewProperties.nodeView();
    }

    public static SpecimenRecord makeSpecimenRecord(PublicId specimenId) {
        Optional<Entity> specimenEntity = EntityService.get().getEntity(specimenId.asUuidArray());
        if (specimenEntity.isEmpty()) {
            throw new IllegalArgumentException("PublicId " + specimenId + " is not in database.");
        }
        return makeSpecimenRecord(specimenEntity.get());
    }

    public static SpecimenRecord makeSpecimenRecord(Entity specimenEntity) {
        return new SpecimenRecord(
                specimenEntity.publicId(),
                findConceptReferenceForRoleType(findLatestLogicalDefinition(specimenEntity).get(), SYSTEM).get().publicId(),
                findConceptReferenceForRoleType(findLatestLogicalDefinition(specimenEntity).get(), METHOD_TYPE).get().publicId()
        );
    }
    public static TargetRecord makeTargetRecord(PublicId targetId) {
        Optional<Entity> targetEntity = EntityService.get().getEntity(targetId.asUuidArray());
        if (targetEntity.isEmpty()) {
            throw new IllegalArgumentException("PublicId " + targetId + " is not in database.");
        }
        return makeTargetRecord(targetEntity.get());
    }

    public static TargetRecord makeTargetRecord(Entity targetEntity) {
        return new TargetRecord(
                targetEntity.publicId(),
                findConceptReferenceForRoleType(findLatestLogicalDefinition(targetEntity).get(), ANALYTE).get().publicId()
        );
    }
    public static ResultConformanceRecord makeResultConformanceRecord(PublicId resultConformanceId) {
        Optional<Entity> resultConformanceEntity = EntityService.get().getEntity(resultConformanceId.asUuidArray());
        if (resultConformanceEntity.isEmpty()) {
            throw new IllegalArgumentException("PublicId " + resultConformanceId + " is not in database.");
        }
        return makeResultConformanceRecord(resultConformanceEntity.get());
    }

    public static ResultConformanceRecord makeResultConformanceRecord(Entity resultConformanceEntity) {
        return new ResultConformanceRecord(
                resultConformanceEntity.publicId(),
                findConceptReferenceForRoleType(findLatestLogicalDefinition(resultConformanceEntity).get(), SCALE_ROLETYPE).get().publicId(),
                findConceptReferenceForRoleType(findLatestLogicalDefinition(resultConformanceEntity).get(), PROPERTY_ROLETYPE).get().publicId()
        );
    }

    public static boolean isLidrRecord(Entity entity) {
        if (entity instanceof SemanticEntity semantic) {
            return semantic.pattern().equals(LIDR_RECORD_PATTERN);
        }
        return false;
    }
    public static LidrRecord makeLidrRecord(PublicId lidrRecordId) {
        Optional<Entity> lidrRecordEntity = EntityService.get().getEntity(lidrRecordId.asUuidArray());
        if (lidrRecordEntity.isEmpty()) {
            throw new IllegalArgumentException("PublicId " + lidrRecordId + " is not in database.");
        }
        return makeLidrRecord(lidrRecordEntity.get());
    }

    public static LidrRecord makeLidrRecord(Entity lidrRecordEntity) {
        if (!isLidrRecord(lidrRecordEntity)) {
            throw new IllegalArgumentException("Entity is not associated with a LIDR Record: " + lidrRecordEntity);
        }
        SemanticEntityVersion lidrRecordSemanticVersion = (SemanticEntityVersion) lidrRecordEntity.versions().get(0);
        ImmutableList<Object> vals = lidrRecordSemanticVersion.fieldValues();

        PublicId testPerformedId = (PublicId) vals.get(IDX_TEST_PERFORMED);
        PublicId dataResultsTypeId = (PublicId) vals.get(IDX_DATA_RESULTS_TYPE);
        PublicId analyteId = (PublicId) vals.get(IDX_ANALYTES);
        Set<PublicId> targetIds = ((IntIdSet) vals.get(IDX_TARGETS)).mapToSet(PrimitiveData::publicId);
        Set<PublicId> specimenIds = ((IntIdSet) vals.get(IDX_SPECIMENS)).mapToSet(PrimitiveData::publicId);
        Set<PublicId> resultConformanceIds = ((IntIdSet) vals.get(IDX_RESULT_CONFORMANCES)).mapToSet(PrimitiveData::publicId);

        AnalyteRecord analyte = makeAnalyteRecord(analyteId);
        Set<TargetRecord> targets = targetIds.stream().map(DataModelHelper::makeTargetRecord).collect(Collectors.toSet());
        Set<SpecimenRecord> specimens = specimenIds.stream().map(DataModelHelper::makeSpecimenRecord).collect(Collectors.toSet());
        Set<ResultConformanceRecord> resultConformances = resultConformanceIds.stream().map(DataModelHelper::makeResultConformanceRecord).collect(Collectors.toSet());

        return new LidrRecord(lidrRecordEntity.publicId(), testPerformedId, dataResultsTypeId, analyte,
                targets, specimens, resultConformances);
    }

    public static AnalyteRecord makeAnalyteRecord(PublicId analyteId) {
        Optional<Entity> analyteEntity = EntityService.get().getEntity(analyteId.asUuidArray());
        if (analyteEntity.isEmpty()) {
            throw new IllegalArgumentException("PublicId " + analyteId + " is not in database.");
        }
        return makeAnalyteRecord(analyteEntity.get());
    }

    public static AnalyteRecord makeAnalyteRecord(Entity analyteEntity) {
        return new AnalyteRecord(
                analyteEntity.publicId(),
                findConceptReferenceForRoleType(findLatestLogicalDefinition(analyteEntity).get(), COMPONENT_ROLETYPE).get().publicId(),
                findConceptReferenceForRoleType(findLatestLogicalDefinition(analyteEntity).get(), TIME_ASPECT_ROLETYPE).get().publicId(),
                findConceptReferenceForRoleType(findLatestLogicalDefinition(analyteEntity).get(), METHOD_TYPE_ROLETYPE).get().publicId()
        );
    }


    private static NavigationCalculator defaultNavigationCalculator() {
        StampCoordinateRecord stampCoordinateRecord = Coordinates.Stamp.DevelopmentLatestActiveOnly();
        LanguageCoordinateRecord languageCoordinateRecord = Coordinates.Language.UsEnglishRegularName();
        NavigationCoordinateRecord navigationCoordinateRecord = Coordinates.Navigation.stated().toNavigationCoordinateRecord();
        return NavigationCalculatorWithCache.getCalculator(stampCoordinateRecord, Lists.immutable.of(languageCoordinateRecord), navigationCoordinateRecord);
    }

    public static Optional<Concept> findDeviceManufacturer(PublicId pubId) {
        return findDeviceManufacturer(viewPropertiesNode().calculator().navigationCalculator(), pubId);
    }

    public static Optional<Concept> findDeviceManufacturer(NavigationCalculator navCalc, PublicId pubId) {
        AtomicReference<Optional<Concept>> deviceManufacturer = new AtomicReference<>(Optional.empty());
        findLatestLogicalDefinition(navCalc, pubId).ifPresent((latestLogicalDefinition) -> {
            deviceManufacturer.set(findConceptReferenceForRoleType(latestLogicalDefinition, MANUFACTURED_BY.publicId()));
        });
        return deviceManufacturer.get();
    }

    public static Optional<DiTree<Vertex>> findLatestLogicalDefinition(PublicId pubId) {
//        return findLatestLogicalDefinition(viewPropertiesNode().calculator().navigationCalculator(), pubId);
        return findLatestLogicalDefinition(defaultNavigationCalculator(), pubId);
    }

    public static Optional<DiTree<Vertex>> findLatestLogicalDefinition(NavigationCalculator navCalc, PublicId pubId) {
        int componentNid = EntityService.get().nidForPublicId(pubId);
        StampCalculator stampCalculator = navCalc.stampCalculator();
        AtomicReference<StampEntity<StampEntityVersion>> latestStamp = new AtomicReference<>();
        AtomicReference<DiTree<Vertex>> latestLogicalDefinitionSemanticVersion = new AtomicReference<>();

        for (int navigationPatternNid : navCalc.navigationCoordinate().navigationPatternNids().toArray()) {
            int logicalDefintionPatternNid =
                    navigationPatternNid != TinkarTerm.STATED_NAVIGATION_PATTERN.nid() ?
                            TinkarTerm.EL_PLUS_PLUS_INFERRED_AXIOMS_PATTERN.nid() : TinkarTerm.EL_PLUS_PLUS_STATED_AXIOMS_PATTERN.nid();

            EntityService.get().forEachSemanticForComponentOfPattern(componentNid, logicalDefintionPatternNid, (semanticEntity) -> {
                stampCalculator.latest(semanticEntity)
                        .ifPresent((semanticEntityVersion) -> {
                            if (latestStamp.get() == null) {
                                latestStamp.set(semanticEntityVersion.stamp());
                                latestLogicalDefinitionSemanticVersion.set((DiTree) semanticEntityVersion.fieldValues().get(0));
                            } else {
                                if (RelativePosition.AFTER == stampCalculator.relativePosition(semanticEntityVersion.stampNid(), latestStamp.get().nid())) {
                                    latestStamp.set(semanticEntityVersion.stamp());
                                    latestLogicalDefinitionSemanticVersion.set((DiTree) semanticEntityVersion.fieldValues().get(0));
                                }
                            }
                        });
            });
        }
        return Optional.ofNullable(latestLogicalDefinitionSemanticVersion.get());
    }

    public static Optional<Concept> findConceptReferenceForRoleType(DiTree<Vertex> logicalDefinition, PublicId roleTypeToFind) {
        ImmutableList<Vertex> vertexList = logicalDefinition.vertexMap();
        for (Vertex vertex : vertexList) {
            if (LogicalOperatorsForVertex.ROLE.semanticallyEqual((EntityFacade) vertex.meaning())) {
                Concept roleTypeProperty = vertex.propertyAsConcept(TinkarTerm.ROLE_TYPE).get();
                if (roleTypeProperty.equals(roleTypeToFind)) {
                    Vertex manufacturerVertex = logicalDefinition.successors(vertex).get(0);
                    return manufacturerVertex.propertyAsConcept(TinkarTerm.CONCEPT_REFERENCE);
                }
            }
        }
        return Optional.empty();
    }
    public static boolean isDevice(NavigationCalculator navCalc, PublicId pubId) {
        PublicId deviceConceptPublicId = PublicIds.of(UUID.fromString("e0ac20ad-ce6f-3ee4-8c71-51b070aa5737"));
        int deviceComponentNid = EntityService.get().nidForPublicId(deviceConceptPublicId);


        // possible concept having device as a parent
        int componentNid = EntityService.get().nidForPublicId(pubId);

        StampCalculator stampCalculator = navCalc.stampCalculator();
        AtomicReference<StampEntity<StampEntityVersion>> latestStamp = new AtomicReference<>();
        AtomicReference<DiTree<Vertex>> latestInferredDefinitionSemanticVersion = new AtomicReference<>();

        for (int navigationPatternNid : navCalc.navigationCoordinate().navigationPatternNids().toArray()) {
            int logicalDefintionPatternNid =
                    navigationPatternNid != TinkarTerm.STATED_NAVIGATION.nid() ?
                            TinkarTerm.EL_PLUS_PLUS_INFERRED_AXIOMS_PATTERN.nid() : TinkarTerm.EL_PLUS_PLUS_STATED_AXIOMS_PATTERN.nid();

            EntityService.get().forEachSemanticForComponentOfPattern(componentNid, logicalDefintionPatternNid, (semanticEntity) -> {
                stampCalculator.latest(semanticEntity)
                        .ifPresent((semanticEntityVersion) -> {
                            if (latestStamp.get() == null) {
                                latestStamp.set(semanticEntityVersion.stamp());
                                latestInferredDefinitionSemanticVersion.set((DiTree) semanticEntityVersion.fieldValues().get(0));
                            } else {
                                if (RelativePosition.AFTER == stampCalculator.relativePosition(semanticEntityVersion.stampNid(), latestStamp.get().nid())) {
                                    latestStamp.set(semanticEntityVersion.stamp());
                                    latestInferredDefinitionSemanticVersion.set((DiTree) semanticEntityVersion.fieldValues().get(0));
                                }
                            }
                        });
            });
        }
        DiTree<Vertex> logicalDefinition = latestInferredDefinitionSemanticVersion.get();
        ImmutableList<Vertex> vertexList = logicalDefinition.vertexMap();
        for (Vertex vertex : vertexList) {
            if (LogicalOperatorsForVertex.CONCEPT.semanticallyEqual((EntityFacade) vertex.meaning())) {
                EntityFacade refConcept = (EntityFacade) vertex.propertyAsConcept(TinkarTerm.CONCEPT_REFERENCE).get();
                if (refConcept.nid() == deviceComponentNid) {
                    //Vertex manufacturerVertex = logicalDefinition.successors(vertex).get(0);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSubtype(PublicId pubId, PublicId superTypeId) {
        return isSubtype(viewPropertiesNode().calculator().navigationCalculator(), pubId, superTypeId);
    }

    public static boolean isSubtype(NavigationCalculator navCalc, PublicId pubId, PublicId superTypeId) {
        int deviceComponentNid = EntityService.get().nidForPublicId(superTypeId);

        AtomicReference<DiTree<Vertex>> logicalDefinition = new AtomicReference<>();
        findLatestLogicalDefinition(navCalc, pubId).ifPresent(logicalDefinition::set);
        ImmutableList<Vertex> vertexList = logicalDefinition.get().vertexMap();
        for (Vertex vertex : vertexList) {
            if (LogicalOperatorsForVertex.CONCEPT.semanticallyEqual((EntityFacade) vertex.meaning())) {
                EntityFacade refConcept = (EntityFacade) vertex.propertyAsConcept(TinkarTerm.CONCEPT_REFERENCE).get();
                if (refConcept.nid() == deviceComponentNid) {
                    return true;
                }
            }
        }
        return false;
    }
    public static PublicId write(LidrRecord lidrRecord, PublicId referencedComponentPublicId, PublicId stampEntity){
        // LIDR Record must be a Semantic referencing (pointing to) a Diagnostic Device Semantic
        // Diagnostic Device Semantics are expected to have 2 "child" semantics and therefore the structure will be like below:
        //
        //     **Device Concept**
        //          > Diagnostic Device Semantic
        //               > Instrument Equipment Semantic
        //               > LIDR Record Semantic
        //
        Optional<Entity> referenceComponent = EntityService.get().getEntity(referencedComponentPublicId.asUuidList());
        if (!referenceComponent.isPresent()) {
            throw new RuntimeException("Error reference component does not exist in database: " + referencedComponentPublicId);
        }
        PublicId lidrPublicId = lidrRecord.lidrRecordId()== null ? PublicIds.newRandom() : lidrRecord.lidrRecordId();
        PublicId diagDeviceSemanticId = writeDiagnosticDeviceSemantic(PublicIds.of("f8886532-38cf-3981-a4cd-a63fbec88c06"), referencedComponentPublicId, stampEntity);
        PublicId instrumentEquipmentSemanticId = writeInstrumentEquipmentSemantic(referencedComponentPublicId, diagDeviceSemanticId, stampEntity);

        // Create a semantic record representing a LIDR Record pattern
        // LIDR Record Semantic references (points to) the Diagnostic Device Semantic
        SemanticWriter writer = new SemanticWriter(stampEntity);
        Supplier<MutableList<Object>> fieldsSupplier = () -> {
            // Targets into IntLists
            IntIdSet targetIds = lidrRecord.targets() == null ? IntIds.set.empty() : IntIds.set.of(lidrRecord.targets(),
                    (dto) -> PrimitiveData.get().nidForPublicId(dto.targetId()));

            // Specimens into IntLists
            IntIdSet specimenIds = lidrRecord.specimens() == null ? IntIds.set.empty() : IntIds.set.of(lidrRecord.specimens(),
                    (dto) -> PrimitiveData.get().nidForPublicId(dto.specimenId()));

            // Results conformance into IntLists for
            IntIdSet resultConfIds = lidrRecord.resultConformances() == null ? IntIds.set.empty() : IntIds.set.of(lidrRecord.resultConformances(),
                    (resultConf) -> PrimitiveData.get().nidForPublicId(resultConf.resultConformanceId()));

            // Create pattern's field definitions
            MutableList<Object> lidrRecordFields = Lists.mutable.empty();

            ConceptFacade testPerformed = ConceptFacade.make(PrimitiveData.get().nidForPublicId(lidrRecord.testPerformedId()));
            lidrRecordFields.add(testPerformed);     // Test performed (concept)

            ConceptFacade resultType = ConceptFacade.make(PrimitiveData.get().nidForPublicId(lidrRecord.dataResultsTypeId()));
            lidrRecordFields.add(resultType);   // Result Type (concept)

            ConceptFacade analyte = ConceptFacade.make(PrimitiveData.get().nidForPublicId(lidrRecord.analyte().analyteId()));
            lidrRecordFields.add(analyte); // Analyte (concept)

            lidrRecordFields.add(targetIds);                        // Target (int set/ IntSet)
            lidrRecordFields.add(specimenIds);                      // Specimen (int set)
            lidrRecordFields.add(resultConfIds);

            return lidrRecordFields;
        };

        // Lidr Record semantic referencing the Diagnostic Device Semantic.
        writer.semantic(lidrPublicId, new SemanticDetail(LIDR_RECORD_PATTERN.publicId(), diagDeviceSemanticId, fieldsSupplier));
        return lidrPublicId;
    }

    public static PublicId writeDiagnosticDeviceSemantic(PublicId testOrderedId, PublicId referencedComponentId, PublicId stampId) {
        PublicId diagnosticDeviceSemanticId = PublicIds.newRandom();
        writeDiagnosticDeviceSemantic(diagnosticDeviceSemanticId, testOrderedId, referencedComponentId, stampId);
        return diagnosticDeviceSemanticId;
    }

    public static void writeDiagnosticDeviceSemantic(PublicId diagnosticDeviceSemanticId, PublicId testOrderedId, PublicId referencedComponentId, PublicId stampId) {
        // Create a semantic record representing a diagnostic device pattern
        // Diagnostic Device Semantic references (points to) the Device Concept
        SemanticWriter diagDeviceWriter = new SemanticWriter(stampId);
        Supplier<MutableList<Object>> diagFieldsSupplier = () -> {
            // Test Ordered for the Diagnostic Device Semantic Field
            ConceptFacade testOrdered = ConceptFacade.make(PrimitiveData.get().nidForPublicId(testOrderedId));
            return Lists.mutable.of(testOrdered);
        };
        // Diagnostic Device semantic referencing device concept.
        diagDeviceWriter.semantic(diagnosticDeviceSemanticId, new SemanticDetail(DIAGNOSTIC_DEVICE_PATTERN.publicId(), referencedComponentId, diagFieldsSupplier));
    }


    public static PublicId writeInstrumentEquipmentSemantic(PublicId equipmentID, PublicId referencedComponentId, PublicId stampId) {
        PublicId instrumentEquipmentSemanticId = PublicIds.newRandom();
        writeInstrumentEquipmentSemantic(instrumentEquipmentSemanticId, equipmentID, referencedComponentId, stampId);
        return instrumentEquipmentSemanticId;
    }

    public static void writeInstrumentEquipmentSemantic(PublicId instrumentEquipmentSemanticId, PublicId equipmentID, PublicId referencedComponentId, PublicId stampId) {
        // Create a semantic record representing a diagnostic device pattern
        // Diagnostic Device Semantic references (points to) the Device Concept
        SemanticWriter diagDeviceWriter = new SemanticWriter(stampId);
        Supplier<MutableList<Object>> diagFieldsSupplier = () -> {
            // Test Ordered for the Diagnostic Device Semantic Field
            ConceptFacade testOrdered = ConceptFacade.make(PrimitiveData.get().nidForPublicId(equipmentID));
            return Lists.mutable.of(testOrdered);
        };
        // Diagnostic Device semantic referencing device concept.
        diagDeviceWriter.semantic(instrumentEquipmentSemanticId, new SemanticDetail(INSTRUMENT_EQUIPMENT_PATTERN.publicId(), referencedComponentId, diagFieldsSupplier));
    }
}