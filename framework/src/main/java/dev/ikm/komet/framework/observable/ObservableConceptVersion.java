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
package dev.ikm.komet.framework.observable;

import dev.ikm.tinkar.coordinate.stamp.calculator.StampCalculator;
import dev.ikm.tinkar.entity.*;
import dev.ikm.tinkar.entity.transaction.Transaction;
import dev.ikm.tinkar.terms.TinkarTerm;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;

public final class ObservableConceptVersion extends ObservableVersion<ConceptVersionRecord> implements ConceptEntityVersion {
    ObservableConceptVersion(ConceptVersionRecord conceptVersionRecord) {
        super(conceptVersionRecord);
    }

    @Override
    protected ConceptVersionRecord withStampNid(int stampNid) {
        return version().withStampNid(stampNid);
    }

    @Override
    public ConceptVersionRecord getVersionRecord() {
        return version();
    }


    @Override
    protected void addListeners() {
        ConceptVersionRecord newVersion = null;
        stateProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                if (version().uncommitted()) {
                    Transaction.forVersion(version()).ifPresentOrElse(transaction -> {
                        StampEntity newStamp = transaction.getStamp(newValue, version().time(), version().authorNid(), version().moduleNid(), version().pathNid());
                        versionProperty.set(withStampNid(newStamp.nid()));
                    }, () -> {
                        Transaction t = Transaction.make();
                        // newStamp already written to the entity store.
                        StampEntity<?> newStamp = t.getStampForEntities(version().state(), version().authorNid(), newValue.nid(), version().pathNid(), entity());
                        // Create new version...
                        versionProperty.set(withStampNid(newStamp.nid()));
                    });
                } else {
                    Transaction t = Transaction.make();
                    // newStamp already written to the entity store.
                    StampEntity<?> newStamp = t.getStampForEntities(newValue, version().authorNid(), version().moduleNid(), version().pathNid(), entity());
                    // Create new version...
                    versionProperty.set(withStampNid(newStamp.nid()));
                }
            }

        });

        timeProperty.addListener((observable, oldValue, newValue) -> {
            // TODO when to update the chronology with new record? At commit time? Automatically with reactive stream for commits?
            if (version().uncommitted()) {
                Transaction.forVersion(version()).ifPresentOrElse(transaction -> {
                    StampEntity newStamp = transaction.getStamp(version().state(), newValue.longValue(), version().authorNid(), version().moduleNid(), version().pathNid());
                    versionProperty.set(withStampNid(newStamp.nid()));
                }, () -> {
                    throw new IllegalStateException("No transaction for uncommitted version: " + version());
                });
            } else {
                //Are we allowed to change the time and have it autosave ?
                throw new IllegalStateException("Version is already committed, cannot change value.");
            }
        });

        authorProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue !=null ){
                if (version().uncommitted()) {
                    Transaction.forVersion(version()).ifPresentOrElse(transaction -> {
                        StampEntity newStamp = transaction.getStamp(version().state(), version().time(), newValue.nid(), version().moduleNid(), version().pathNid());
                        versionProperty.set(withStampNid(newStamp.nid()));
                    }, () -> {
                        Transaction t = Transaction.make();
                        // newStamp already written to the entity store.
                        StampEntity<?> newStamp = t.getStampForEntities(version().state(), version().authorNid(), newValue.nid(), version().pathNid(), entity());
                        // Create new version...
                        versionProperty.set(withStampNid(newStamp.nid()));
                    });
                } else {
                    Transaction t = Transaction.make();
                    // newStamp already written to the entity store.
                    StampEntity<?> newStamp = t.getStampForEntities(version().state(), newValue.nid(), version().moduleNid(), version().pathNid(), entity());
                    // Create new version...
                    versionProperty.set(withStampNid(newStamp.nid()));
                }
            }

        });

        moduleProperty.addListener((observable, oldValue, newValue) -> {
            if( newValue != null){
                if (version().uncommitted()) {
                    Transaction.forVersion(version()).ifPresentOrElse(transaction -> {
                        StampEntity newStamp = transaction.getStamp(version().state(), version().time(), version().authorNid(), newValue.nid(), version().pathNid());
                        versionProperty.set(withStampNid(newStamp.nid()));
                    }, () -> {
                        Transaction t = Transaction.make();
                        // newStamp already written to the entity store.
                        StampEntity<?> newStamp = t.getStampForEntities(version().state(), version().authorNid(), newValue.nid(), version().pathNid(), entity());
                        // Create new version...
                        versionProperty.set(withStampNid(newStamp.nid()));
                    });
                } else {
                    Transaction t = Transaction.make();
                    // newStamp already written to the entity store.
                    StampEntity<?> newStamp = t.getStampForEntities(version().state(), version().authorNid(), newValue.nid(), version().pathNid(), entity());
                    // Create new version...
                    versionProperty.set(withStampNid(newStamp.nid()));
                }
            }

        });

        pathProperty.addListener((observable, oldValue, newValue) -> {
            if( newValue != null){
                if (version().uncommitted()) {
                    Transaction.forVersion(version()).ifPresentOrElse(transaction -> {
                        StampEntity newStamp = transaction.getStamp(version().state(), version().time(), version().authorNid(), version().moduleNid(), newValue.nid());
                        versionProperty.set(withStampNid(newStamp.nid()));
                    }, () -> {
                        Transaction t = Transaction.make();
                        // newStamp already written to the entity store.
                        StampEntity<?> newStamp = t.getStampForEntities(version().state(), version().authorNid(), newValue.nid(), version().pathNid(), entity());
                        // Create new version...
                        versionProperty.set(withStampNid(newStamp.nid()));
                    });
                } else {
                    Transaction t = Transaction.make();
                    // newStamp already written to the entity store.
                    StampEntity<?> newStamp = t.getStampForEntities(version().state(), version().authorNid(), version().moduleNid(), newValue.nid(), entity());
                    // Create new version...
                    versionProperty.set(withStampNid(newStamp.nid()));
                }
            }
        });

    }



    @Override
    public ImmutableMap<FieldCategory, ObservableField> getObservableFields() {
        MutableMap<FieldCategory, ObservableField> fieldMap = Maps.mutable.empty();

        int firstStamp = StampCalculator.firstStampTimeOnly(this.entity().stampNids());

        for (FieldCategory field : FieldCategorySet.conceptVersionFields()) {
            switch (field) {
                case PUBLIC_ID_FIELD -> {
                    //TODO temporary until we get a pattern for concept fields...
                    //TODO get right starter set entities. Temporary incorrect codes for now.
                    Object value = this.publicId();
                    int dataTypeNid = TinkarTerm.IDENTIFIER_VALUE.nid();
                    int purposeNid = TinkarTerm.IDENTIFIER_VALUE.nid();
                    int meaningNid = TinkarTerm.IDENTIFIER_VALUE.nid();
                    Entity<EntityVersion> idPattern = Entity.getFast(TinkarTerm.IDENTIFIER_PATTERN.nid());
                    int patternVersionStampNid = StampCalculator.firstStampTimeOnly(idPattern.stampNids());
                    int patternNid = TinkarTerm.IDENTIFIER_PATTERN.nid();
                    int indexInPattern = 0;

                    FieldDefinitionRecord fdr = new FieldDefinitionRecord(dataTypeNid, purposeNid, meaningNid,
                            patternVersionStampNid, patternNid, indexInPattern);

                    fieldMap.put(field, new ObservableField(new FieldRecord(value, this.nid(), firstStamp, fdr)));
                }
                case STATUS_FIELD -> {
                    //TODO temporary until we get a pattern for concept fields...
                    //TODO get right starter set entities. Temporary incorrect codes for now.
                    Object value = this.state();
                    int dataTypeNid = TinkarTerm.COMPONENT_FIELD.nid();
                    int purposeNid = TinkarTerm.COMPONENT_FOR_SEMANTIC.nid();
                    int meaningNid = TinkarTerm.COMPONENT_FOR_SEMANTIC.nid();
                    Entity<EntityVersion> stampPattern = Entity.getFast(TinkarTerm.STAMP_PATTERN.nid());
                    int patternVersionStampNid = StampCalculator.firstStampTimeOnly(stampPattern.stampNids());
                    int patternNid = TinkarTerm.STAMP_PATTERN.nid();
                    int indexInPattern = 0;

                    FieldDefinitionRecord fdr = new FieldDefinitionRecord(dataTypeNid, purposeNid, meaningNid,
                            patternVersionStampNid, patternNid, indexInPattern);

                    fieldMap.put(field, new ObservableField(new FieldRecord(value, this.nid(), firstStamp, fdr)));
                }
                case TIME_FIELD -> {
                    //TODO temporary until we get a pattern for concept fields...
                    //TODO get right starter set entities. Temporary incorrect codes for now.
                    Object value = this.time();
                    int dataTypeNid = TinkarTerm.LONG_FIELD.nid();
                    int purposeNid = TinkarTerm.TIME_FOR_VERSION.nid();
                    int meaningNid = TinkarTerm.TIME_FOR_VERSION.nid();
                    Entity<EntityVersion> stampPattern = Entity.getFast(TinkarTerm.STAMP_PATTERN.nid());
                    int patternVersionStampNid = StampCalculator.firstStampTimeOnly(stampPattern.stampNids());
                    int patternNid = TinkarTerm.STAMP_PATTERN.nid();
                    int indexInPattern = 1;

                    FieldDefinitionRecord fdr = new FieldDefinitionRecord(dataTypeNid, purposeNid, meaningNid,
                            patternVersionStampNid, patternNid, indexInPattern);

                    fieldMap.put(field, new ObservableField(new FieldRecord(value, this.nid(), firstStamp, fdr)));
                }
                case AUTHOR_FIELD -> {
                    //TODO temporary until we get a pattern for concept fields...
                    //TODO get right starter set entities. Temporary incorrect codes for now.
                    Object value = this.authorNid();
                    int dataTypeNid = TinkarTerm.COMPONENT_FIELD.nid();
                    int purposeNid = TinkarTerm.AUTHOR_FOR_VERSION.nid();
                    int meaningNid = TinkarTerm.AUTHOR_FOR_VERSION.nid();
                    Entity<EntityVersion> stampPattern = Entity.getFast(TinkarTerm.STAMP_PATTERN.nid());
                    int patternVersionStampNid = StampCalculator.firstStampTimeOnly(stampPattern.stampNids());
                    int patternNid = TinkarTerm.STAMP_PATTERN.nid();
                    int indexInPattern = 2;

                    FieldDefinitionRecord fdr = new FieldDefinitionRecord(dataTypeNid, purposeNid, meaningNid,
                            patternVersionStampNid, patternNid, indexInPattern);

                    fieldMap.put(field, new ObservableField(new FieldRecord(value, this.nid(), firstStamp, fdr)));

                }
                case MODULE_FIELD -> {
                    //TODO temporary until we get a pattern for concept fields...
                    //TODO get right starter set entities. Temporary incorrect codes for now.
                    Object value = this.moduleNid();
                    int dataTypeNid = TinkarTerm.COMPONENT_FIELD.nid();
                    int purposeNid = TinkarTerm.MODULE_FOR_VERSION.nid();
                    int meaningNid = TinkarTerm.MODULE_FOR_VERSION.nid();
                    Entity<EntityVersion> stampPattern = Entity.getFast(TinkarTerm.STAMP_PATTERN.nid());
                    int patternVersionStampNid = StampCalculator.firstStampTimeOnly(stampPattern.stampNids());
                    int patternNid = TinkarTerm.STAMP_PATTERN.nid();
                    int indexInPattern = 3;

                    FieldDefinitionRecord fdr = new FieldDefinitionRecord(dataTypeNid, purposeNid, meaningNid,
                            patternVersionStampNid, patternNid, indexInPattern);

                    fieldMap.put(field, new ObservableField(new FieldRecord(value, this.nid(), firstStamp, fdr)));
                }
                case PATH_FIELD -> {
                    //TODO temporary until we get a pattern for concept fields...
                    //TODO get right starter set entities. Temporary incorrect codes for now.
                    Object value = this.pathNid();
                    int dataTypeNid = TinkarTerm.COMPONENT_FIELD.nid();
                    int purposeNid = TinkarTerm.PATH_FOR_VERSION.nid();
                    int meaningNid = TinkarTerm.PATH_FOR_VERSION.nid();
                    Entity<EntityVersion> stampPattern = Entity.getFast(TinkarTerm.STAMP_PATTERN.nid());
                    int patternVersionStampNid = StampCalculator.firstStampTimeOnly(stampPattern.stampNids());
                    int patternNid = TinkarTerm.STAMP_PATTERN.nid();
                    int indexInPattern = 4;

                    FieldDefinitionRecord fdr = new FieldDefinitionRecord(dataTypeNid, purposeNid, meaningNid,
                            patternVersionStampNid, patternNid, indexInPattern);

                    fieldMap.put(field, new ObservableField(new FieldRecord(value, this.nid(), firstStamp, fdr)));
                }
            }
        }

        return fieldMap.toImmutable();
    }

}
