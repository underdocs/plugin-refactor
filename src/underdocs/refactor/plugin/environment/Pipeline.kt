package underdocs.refactor.plugin.environment

import underdocs.refactor.pipeline.item.PipelineItem

/**
 * Abstraction of the processing pipeline. Plugins can
 * submit additional items for processing.
 */
interface Pipeline {
    /**
     * Submits the specified item for processing.
     */
    fun submitItem(item: PipelineItem)
}
