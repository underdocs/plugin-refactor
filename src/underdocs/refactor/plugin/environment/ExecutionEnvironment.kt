package underdocs.refactor.plugin.environment

/**
 * The environment plugins operate in. Provides access to various
 * facilities of the Underdocs application.
 */
interface ExecutionEnvironment {
    val configuration: Configuration

    val linker: Linker

    val pipeline: Pipeline
}
