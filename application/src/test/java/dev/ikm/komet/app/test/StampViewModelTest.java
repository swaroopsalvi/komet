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
package dev.ikm.komet.app.test;

import static dev.ikm.komet.kview.mvvm.model.DataModelHelper.fetchDescendentsOfConcept;
import static dev.ikm.komet.kview.mvvm.viewmodel.StampViewModel.MODULES_PROPERTY;
import static dev.ikm.komet.kview.mvvm.viewmodel.StampViewModel.PATHS_PROPERTY;
import static dev.ikm.komet.preferences.JournalWindowPreferences.MAIN_KOMET_WINDOW;
import static dev.ikm.tinkar.coordinate.stamp.StampFields.MODULE;
import static dev.ikm.tinkar.coordinate.stamp.StampFields.PATH;
import static dev.ikm.tinkar.coordinate.stamp.StampFields.STATUS;
import static dev.ikm.tinkar.coordinate.stamp.StampFields.TIME;
import dev.ikm.komet.framework.view.ViewProperties;
import dev.ikm.komet.framework.window.WindowSettings;
import dev.ikm.komet.kview.mvvm.viewmodel.StampViewModel;
import dev.ikm.komet.preferences.KometPreferences;
import dev.ikm.komet.preferences.KometPreferencesImpl;
import dev.ikm.tinkar.terms.State;
import dev.ikm.tinkar.terms.TinkarTerm;
import org.carlfx.cognitive.validator.ValidationMessage;

public class StampViewModelTest {
    public static void main(String[] args) {
        // TODO this throws an error because database is not started.
        KometPreferences appPreferences = KometPreferencesImpl.getConfigurationRootPreferences();
        KometPreferences windowPreferences = appPreferences.node(MAIN_KOMET_WINDOW);

        WindowSettings windowSettings = new WindowSettings(windowPreferences);
        ViewProperties viewProperties = windowSettings.getView().makeOverridableViewProperties();


        StampViewModel stampViewModel = new StampViewModel();
        stampViewModel.setPropertyValue(STATUS, State.ACTIVE)
                .setPropertyValue(TIME, System.currentTimeMillis())
                .setPropertyValue(MODULE, 0)
                .setPropertyValue(PATH, 0)
                .addProperty(MODULES_PROPERTY, fetchDescendentsOfConcept(viewProperties, TinkarTerm.MODULE.publicId()), true)
                .addProperty(PATHS_PROPERTY, fetchDescendentsOfConcept(viewProperties, TinkarTerm.PATH.publicId()), true);
        log("--------------");
        log("Creation stampViewModel \n" + stampViewModel);
        log("--------------");
        stampViewModel.validate();
        log("Validation stampViewModel \n" + stampViewModel);
        stampViewModel.invalidate();
        log("Invalidation stampViewModel \n" + stampViewModel);

        log(" Number of errors: " + stampViewModel.getValidationMessages().size());

        for (ValidationMessage vMsg : stampViewModel.getValidationMessages()) {
            vMsg.interpolate(stampViewModel);
            System.out.println("msg Type: %s errorcode: %s, msg: %s".formatted(vMsg.messageType(), vMsg.errorCode(), vMsg.interpolate(stampViewModel)) );
        }

        log("--------------");
        log(" Assuming there are no errors, the save will commit changes from view props to model values");
        log(" After save -> \n" + stampViewModel);
    }
    public static void log(String message) {
        System.out.println(message);
    }
}
