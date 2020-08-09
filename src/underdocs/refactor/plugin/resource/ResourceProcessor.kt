package underdocs.refactor.plugin.resource

import underdocs.dependencies.Resource
import underdocs.refactor.plugin.StepOutput

interface ResourceProcessor {
    fun processResource(context: Context): StepOutput<Resource>

    class Context(val resource: Resource)
}