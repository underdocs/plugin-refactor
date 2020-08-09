package underdocs.refactor.pipeline.item

interface PipelineItemVisitor {
    fun accept(item: CodeElementItem) = Unit
}