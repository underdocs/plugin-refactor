package underdocs.refactor.pipeline.item

interface PipelineItem {
    val fileSystemPath: String

    fun accept(visitor: PipelineItemVisitor)
}
