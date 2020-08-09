package underdocs.refactor.plugin.page

import underdocs.dependencies.Page

interface OutputWriter {
    fun write(context: Context)

    class Context(val page: Page, val contents: String)
}