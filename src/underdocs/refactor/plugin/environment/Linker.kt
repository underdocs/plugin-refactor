package underdocs.refactor.plugin.environment

/**
 * Items that can be linked to. For example, each code element is
 * linkable.
 */
interface Linkable

/**
 * Linkers can create links to and between Linkable items. This interface is necessary
 * for creating proper anchors throughout the site and determining the output location
 * of various items.
 */
interface Linker {
    /**
     * Returns the file system location of the specified linkable item.
     */
    fun fileSystemPathOf(linkable: Linkable): String

    /**
     * Returns the site path (or site URL) to the linkable.
     */
    fun sitePathOf(linkable: Linkable): String

    /**
     * Calculates the relative path between to linkable items on the site.
     */
    fun relativeSitePathBetween(from: Linkable, to: Linkable): String
}