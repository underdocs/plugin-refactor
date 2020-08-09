package underdocs.refactor.plugin.lifecycle

interface LifecycleAware {
    fun beforeProcessing()

    fun afterProcessing()
}
