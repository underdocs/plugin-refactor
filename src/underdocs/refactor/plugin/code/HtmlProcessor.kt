package underdocs.refactor.plugin.code

import underdocs.dependencies.CodeElement
import underdocs.dependencies.J2HTMLDOMContent
import underdocs.refactor.plugin.StepOutput

interface HtmlProcessor {
    fun beforeHtmlRender(beforeContext: BeforeContext): StepOutput<J2HTMLDOMContent> =
        StepOutput.skip()

    fun afterHtmlRender(afterContext: AfterContext): StepOutput<String> =
        StepOutput.skip()

    class BeforeContext(val element: CodeElement, val dom: J2HTMLDOMContent)

    class AfterContext(val element: CodeElement, val dom: String)
}