package underdocs.refactor.pipeline.stage

interface Stage<In, Out> {
    suspend fun process(input: In): StageOutput<Out>

    fun <NewOut> then(nextStage: Stage<Out, NewOut>): Stage<In, NewOut> {
        return CompositeStage<In, Out, NewOut>(this, nextStage)
    }
}
