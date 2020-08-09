package underdocs.refactor.plugin.code

import underdocs.dependencies.CodeElement
import underdocs.dependencies.MarkdownDocument
import underdocs.refactor.plugin.StepOutput

interface SectionParser<T> {
    val type: T

    fun parseSection(context: Context): StepOutput<T>

    class Context(val element: CodeElement, val document: MarkdownDocument)
}
