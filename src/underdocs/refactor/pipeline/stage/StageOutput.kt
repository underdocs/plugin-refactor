package underdocs.refactor.pipeline.stage

class StageOutput<out T>(val result: T) {
    companion object {
        private val ABORT_PROCESSING = StageOutput(null)

        fun <T> abortProcessing(): StageOutput<T> {
            @Suppress("UNCHECKED_CAST")
            return ABORT_PROCESSING as StageOutput<T>
        }
    }
}