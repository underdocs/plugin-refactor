package underdocs.refactor.plugin.code

import underdocs.dependencies.CodeElement
import underdocs.dependencies.MarkdownDocument
import underdocs.refactor.plugin.StepOutput

interface MarkdownProcessor {
    fun beforeMarkdownParse(beforeContext: BeforeContext): StepOutput<String> =
        StepOutput.skip()

    fun afterMarkdownParse(afterContext: AfterContext): StepOutput<MarkdownDocument> =
        StepOutput.skip()

    class BeforeContext(val element: CodeElement, val document: String)

    class AfterContext(val element: CodeElement, val document: MarkdownDocument)
}