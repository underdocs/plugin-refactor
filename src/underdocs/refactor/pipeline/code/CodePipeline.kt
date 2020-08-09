package underdocs.refactor.pipeline.code

import underdocs.refactor.pipeline.item.CodeElementItem

class CodePipeline {
    private val stages =
        MarkdownProcessingStage(emptyList())
            .then(SectionParsingStage(emptyList()))
            .then(SectionRendererStage(emptyList()))

    suspend fun process(items: List<CodeElementItem>) {

    }
}