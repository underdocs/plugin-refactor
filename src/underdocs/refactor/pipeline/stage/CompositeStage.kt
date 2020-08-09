package underdocs.refactor.pipeline.stage

class CompositeStage<In, Mid, Out>(
    private val first: Stage<In, Mid>,
    private val second: Stage<Mid, Out>
) : Stage<In, Out> {
    override suspend fun process(input: In): StageOutput<Out> {
        val firstOutput = first.process(input)

        return if (firstOutput === StageOutput.abortProcessing<Mid>()) {
            StageOutput.abortProcessing()
        } else {
            second.process(firstOutput.result)
        }
    }
}
