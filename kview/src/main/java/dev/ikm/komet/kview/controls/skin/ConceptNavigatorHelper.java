package dev.ikm.komet.kview.controls.skin;

import dev.ikm.komet.kview.controls.KLConceptNavigatorTreeCell;

/**
 * Helper class to access internal methods of controls package.
 */
public class ConceptNavigatorHelper {

    private static ConceptNavigatorAccessor accessor;

    public interface ConceptNavigatorAccessor {
        void markCellDirty(KLConceptNavigatorTreeCell treeCell);
        void unselectItem(KLConceptNavigatorTreeCell treeCell);
    }

    public static void markCellDirty(KLConceptNavigatorTreeCell treeCell) {
        accessor.markCellDirty(treeCell);
    }

    public static void unselectItem(KLConceptNavigatorTreeCell treeCell) {
        accessor.unselectItem(treeCell);
    }

    public static void setConceptNavigatorAccessor(ConceptNavigatorAccessor accessor) {
        ConceptNavigatorHelper.accessor = accessor;
    }

}
