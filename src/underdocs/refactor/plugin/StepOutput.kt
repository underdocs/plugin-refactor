package underdocs.refactor.plugin

class StepOutput<out T>(val result: T?) {
    companion object {
        private val ABORT_PROCESSING = StepOutput(null)
        private val SKIP = StepOutput(null)

        fun <T> abortProcessing(): StepOutput<T> {
            @Suppress("UNCHECKED_CAST")
            return ABORT_PROCESSING as StepOutput<T>
        }

        fun <T> skip(): StepOutput<T> {
            @Suppress("UNCHECKED_CAST")
            return SKIP as StepOutput<T>
        }
    }
}
