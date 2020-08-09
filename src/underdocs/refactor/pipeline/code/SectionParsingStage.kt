package underdocs.refactor.pipeline.code

import underdocs.dependencies.CodeElement
import underdocs.refactor.pipeline.stage.Stage
import underdocs.refactor.pipeline.stage.StageOutput
import underdocs.refactor.plugin.code.Section
import underdocs.refactor.plugin.code.SectionParser

class SectionParsingStage(private val plugins: List<SectionParser<Any>>): Stage<MarkdownProcessingStage.Output, SectionParsingStage.Output> {
    override suspend fun process(input: MarkdownProcessingStage.Output): StageOutput<Output> {
        TODO("Not yet implemented")
    }

    class Output(val element: CodeElement, sections: List<Section>)
}