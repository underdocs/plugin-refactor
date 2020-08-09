package underdocs.refactor.plugin.resource

import underdocs.dependencies.Resource

interface OutputWriter {
    fun write(context: Context)

    class Context(resource: Resource)
}