package underdocs.refactor.pipeline.code

import underdocs.dependencies.CodeElement
import underdocs.dependencies.MarkdownDocument
import underdocs.refactor.pipeline.stage.Stage
import underdocs.refactor.pipeline.stage.StageOutput
import underdocs.refactor.plugin.code.MarkdownProcessor

class MarkdownProcessingStage(private val plugins: List<MarkdownProcessor>):
    Stage<MarkdownProcessingStage.Input, MarkdownProcessingStage.Output> {
    override suspend fun process(input: Input): StageOutput<Output> {
        // Execute the plugins
        TODO()
    }

    class Input(val element: CodeElement)

    class Output(val element: CodeElement, val document: MarkdownDocument)
}
