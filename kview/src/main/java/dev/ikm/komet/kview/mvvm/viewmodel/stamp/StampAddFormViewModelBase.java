package dev.ikm.komet.kview.mvvm.viewmodel.stamp;

import static dev.ikm.komet.kview.mvvm.viewmodel.stamp.StampFormViewModelBase.Properties.AUTHOR;
import static dev.ikm.komet.kview.mvvm.viewmodel.stamp.StampFormViewModelBase.Properties.CLEAR_RESET_BUTTON_TEXT;
import static dev.ikm.komet.kview.mvvm.viewmodel.stamp.StampFormViewModelBase.Properties.CURRENT_STAMP;
import static dev.ikm.komet.kview.mvvm.viewmodel.stamp.StampFormViewModelBase.Properties.FORM_TIME_TEXT;
import static dev.ikm.komet.kview.mvvm.viewmodel.stamp.StampFormViewModelBase.Properties.FORM_TITLE;
import static dev.ikm.komet.kview.mvvm.viewmodel.stamp.StampFormViewModelBase.Properties.IS_STAMP_VALUES_THE_SAME_OR_EMPTY;
import static dev.ikm.komet.kview.mvvm.viewmodel.stamp.StampFormViewModelBase.Properties.MODULE;
import static dev.ikm.komet.kview.mvvm.viewmodel.stamp.StampFormViewModelBase.Properties.PATH;
import static dev.ikm.komet.kview.mvvm.viewmodel.stamp.StampFormViewModelBase.Properties.STATUS;
import static dev.ikm.komet.kview.mvvm.viewmodel.stamp.StampFormViewModelBase.Properties.TIME;
import dev.ikm.komet.framework.controls.TimeUtils;
import dev.ikm.komet.framework.observable.ObservableField;
import dev.ikm.komet.framework.observable.ObservableStamp;
import dev.ikm.komet.framework.observable.ObservableStampVersion;
import dev.ikm.komet.framework.view.ViewProperties;
import dev.ikm.tinkar.component.Stamp;
import dev.ikm.tinkar.entity.EntityVersion;
import dev.ikm.tinkar.entity.StampEntity;
import dev.ikm.tinkar.entity.StampEntityVersion;
import dev.ikm.tinkar.terms.ConceptFacade;
import dev.ikm.tinkar.terms.EntityFacade;
import org.eclipse.collections.api.list.ImmutableList;

import java.util.Optional;
import java.util.UUID;

public abstract class StampAddFormViewModelBase extends StampFormViewModelBase {

    public StampAddFormViewModelBase(Type type) {
        super(type);

        // Add Properties
        addProperty(CURRENT_STAMP, (Stamp) null);

        addProperty(CLEAR_RESET_BUTTON_TEXT, "RESET");
    }

    @Override
    protected String getClearOrResetConfirmationMsg() {
        return "Are you sure you want to reset the form? All entered data will be lost.";
    }

    @Override
    protected String getClearOrResetConfirmationTitle() {
        return "Confirm Reset Form";
    }

    @Override
    protected void doUpdate(EntityFacade entity, UUID topic, ViewProperties viewProperties) {
        // entityFocusProperty from DetailsNode often calls init with a null entity.
        if (entity == null || entity == this.entityFacade) {
            return; // null entity or the entity hasn't changed
        } else {
            this.entityFacade = entity;
        }

        loadStamp();
        loadStampValuesFromDB();

        setPropertyValue(FORM_TIME_TEXT, TimeUtils.toDateString(getPropertyValue(TIME)));

        save(true);
    }

    protected void loadStamp() {
        EntityVersion latestVersion = viewProperties.calculator().latest(entityFacade).get();
        StampEntity stampEntity = latestVersion.stamp();

        setPropertyValue(CURRENT_STAMP, stampEntity);
    }

    protected void loadStampValuesFromDB() {
        StampEntity stampEntity = getPropertyValue(Properties.CURRENT_STAMP);
        ObservableStamp observableStamp = ObservableStamp.get(stampEntity.nid());
        ObservableStampVersion observableStampVersion = observableStamp.lastVersion();
        ImmutableList<ObservableField> stampFields = observableStampVersion.fields();

        Optional<StampEntityVersion> stampEntityVersionOptional = latestCommitedVersionStamp(stampEntity);

        stampEntityVersionOptional.ifPresentOrElse(stampEntityVersion -> {
            setPropertyValue(STATUS, stampEntityVersion.state());
            setPropertyValue(TIME, stampEntityVersion.time());
            setPropertyValue(AUTHOR, stampEntityVersion.author());
            setPropertyValue(MODULE, stampEntityVersion.module());
            setPropertyValue(PATH, stampEntityVersion.path());
        }, () -> {
            setPropertyValue(STATUS, null);
            setPropertyValue(TIME, null);
            setPropertyValue(AUTHOR, null);
            setPropertyValue(MODULE, null);
            setPropertyValue(PATH, null);
        });

    }

    private Optional<StampEntityVersion> latestCommitedVersionStamp(StampEntity stampEntity) {
        if (stampEntity.versions().size() == 1 && ((StampEntityVersion) stampEntity.versions().get(0)).time() != Long.MAX_VALUE) {
            return Optional.of((StampEntityVersion)stampEntity.versions().get(0));
        } else {
            StampEntityVersion latest = null;
            for(StampEntityVersion version :  (ImmutableList<StampEntityVersion>) stampEntity.versions()) {
                if (version.time() == Long.MIN_VALUE) {
                    return Optional.of(version);
                }
                if (latest == null) {
                    latest = version;
                } else if (version.time() != Long.MAX_VALUE && latest.time() < version.time()) {
                    latest = version;
                }
            }
            if(latest.time() == Long.MAX_VALUE){
                return Optional.empty();
            }
            return Optional.of(latest);
        }
    }

    @Override
    protected boolean updateIsStampValuesChanged() {
        StampEntity stampEntity = getPropertyValue(CURRENT_STAMP);

        boolean same = stampEntity.state() == getPropertyValue(STATUS)
                && stampEntity.path() == getPropertyValue(PATH)
                && stampEntity.module() == getPropertyValue(MODULE);

        setPropertyValue(IS_STAMP_VALUES_THE_SAME_OR_EMPTY, same);

        if (same) {
            setPropertyValue(FORM_TITLE, "Latest " + type.getTextDescription() + " Version");
            setPropertyValue(FORM_TIME_TEXT, TimeUtils.toDateString(getPropertyValue(TIME)));
            setPropertyValue(AUTHOR, stampEntity.author());
        } else {
            setPropertyValue(FORM_TITLE, "New " + type.getTextDescription() + " Version");
            setPropertyValue(FORM_TIME_TEXT, "Uncommitted");
            ConceptFacade authorConcept = viewProperties.nodeView().editCoordinate().getAuthorForChanges();
            setPropertyValue(AUTHOR, authorConcept);
        }

        return same;
    }
}