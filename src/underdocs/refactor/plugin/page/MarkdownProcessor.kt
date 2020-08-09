package underdocs.refactor.plugin.page

import underdocs.dependencies.MarkdownDocument
import underdocs.dependencies.Page
import underdocs.refactor.plugin.StepOutput

interface MarkdownProcessor {
    fun beforeMarkdownParse(beforeContext: BeforeContext): StepOutput<String> =
        StepOutput.skip()

    fun afterMarkdownParse(afterContext: AfterContext): StepOutput<MarkdownDocument> =
        StepOutput.skip()

    class BeforeContext(val page: Page, val document: String)

    class AfterContext(val page: Page, val document: MarkdownDocument)
}