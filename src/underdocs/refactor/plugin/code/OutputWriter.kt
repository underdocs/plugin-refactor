package underdocs.refactor.plugin.code

import underdocs.dependencies.CodeElement

interface OutputWriter {
    fun write(context: Context)

    class Context(val element: CodeElement, val contents: String)
}