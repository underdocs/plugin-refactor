package underdocs.refactor.plugin.code

import underdocs.dependencies.J2HTMLDOMContent
import underdocs.refactor.plugin.StepOutput
import underdocs.refactor.plugin.environment.ExecutionEnvironment
import underdocs.refactor.plugin.environment.Linkable

class ExampleHtmlProcessor(private val env: ExecutionEnvironment) : HtmlProcessor {
    override fun beforeHtmlRender(beforeContext: HtmlProcessor.BeforeContext): StepOutput<J2HTMLDOMContent> {
        // Replace the hrefs in all proper anchors
        env.linker.sitePathOf(object : Linkable {})

        return StepOutput(beforeContext.dom)
    }
}