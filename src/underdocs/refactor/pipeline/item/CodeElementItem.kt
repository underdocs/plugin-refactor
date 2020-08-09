package underdocs.refactor.pipeline.item

import underdocs.dependencies.CodeElement

class CodeElementItem(val element: CodeElement): PipelineItem {
    override val fileSystemPath: String
        get() = TODO("Not yet implemented")

    override fun accept(visitor: PipelineItemVisitor) {
        TODO("Not yet implemented")
    }
}