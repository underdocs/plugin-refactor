package underdocs.refactor.plugin.code

import underdocs.dependencies.CodeElement
import underdocs.dependencies.J2HTMLDOMContent
import underdocs.refactor.plugin.StepOutput

interface SectionRenderer<T> {
    val type: T

    fun renderSection(context: Context): StepOutput<J2HTMLDOMContent>

    class Context(val element: CodeElement)
}