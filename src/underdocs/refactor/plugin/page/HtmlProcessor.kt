package underdocs.refactor.plugin.page

import underdocs.dependencies.J2HTMLDOMContent
import underdocs.dependencies.Page
import underdocs.refactor.plugin.StepOutput

interface HtmlProcessor {
    fun beforeHtmlRender(beforeContext: BeforeContext): StepOutput<J2HTMLDOMContent> =
        StepOutput.skip()

    fun afterHtmlRender(afterContext: AfterContext): StepOutput<String> =
        StepOutput.skip()

    class BeforeContext(val page: Page, val dom: J2HTMLDOMContent)

    class AfterContext(val page: Page, val dom: String)
}