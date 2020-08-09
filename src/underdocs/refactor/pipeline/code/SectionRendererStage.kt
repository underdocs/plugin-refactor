package underdocs.refactor.pipeline.code

import underdocs.dependencies.CodeElement
import underdocs.dependencies.J2HTMLDOMContent
import underdocs.refactor.pipeline.stage.Stage
import underdocs.refactor.pipeline.stage.StageOutput
import underdocs.refactor.plugin.code.SectionRenderer

class SectionRendererStage(private val plugins: List<SectionRenderer<Any>>):
    Stage<SectionParsingStage.Output, SectionRendererStage.Output> {
    override suspend fun process(input: SectionParsingStage.Output): StageOutput<Output> {
        TODO("Not yet implemented")
    }

    class Output(val element: CodeElement, renderedSection: List<J2HTMLDOMContent>)
}