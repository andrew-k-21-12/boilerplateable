package io.github.andrewk2112.boilerplateable.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

// Some facts about KSP:
// - it is not tied to JVM and provides relatively high build performance;
// - the original sources are kept as read-only - KSP doesn't allow to modify them;
// - KSP can not examine expression-level information of source code.

/**
 * This is the central entity for KSP:
 * it handles processing of original sources and notifies about its completion (whenever it's successful or not).
 *
 * @param codeGenerator Self-descriptive: takes care about generation of new sources as the processing result.
 * @param logger        Logs anything required to be reported inside of a build console.
 * */
@KspExperimental
class ViewBindingInflatingProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    // Utility.

    /**
     * The entry point and factory providing all necessary arguments for [ViewBindingInflatingProcessor].
     * */
    class Provider : SymbolProcessorProvider { // this entry point must be declared inside
                                               // resources/META-INF/services/com.google.devtools.ksp.processing.SymbolProcessorProvider

        /**
         * Creates an instance of the target [SymbolProcessor] to be provided -
         * [ViewBindingInflatingProcessor] in this case.
         *
         * @param environment Provides all external handlers and configs,
         *                    see the docs of the [SymbolProcessorEnvironment] for details.
         * */
        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
            ViewBindingInflatingProcessor(environment.codeGenerator, environment.logger)

    }





    // Overrides.

    /**
     * The entry point for processing of original sources.
     *
     * @param resolver Queries target symbols (classes, methods, etc.) to be processed.
     *
     * @return A list of deferred symbols that the processor can't process.
     * */
    override fun process(resolver: Resolver): List<KSAnnotated> {

        // Querying all symbols of interest.
        val targetAnnotationName = WithViewBinding::class.qualifiedName
        val targetSymbols = if (targetAnnotationName != null) {
            resolver.getSymbolsWithAnnotation(targetAnnotationName)
        } else {
            sequenceOf()
        }

        // Preparing the list of deferred symbols that the processor can't process,
        // see the internal docs for details.
        val nonResolvableSymbols = targetSymbols.filter { !it.validate() }.toList()

        // Visiting all resolvable class symbols (as our annotation targets classes)
        // to generate required code for each of them.
        targetSymbols
            .filter  { it.validate() && it is KSClassDeclaration }
            .forEach { it.accept(ViewBindingInflatingVisitor(codeGenerator, logger), Unit) }

        // Fulfilling the protocol's requirement, see the docs for this method.
        return nonResolvableSymbols

    }

    /**
     * This method is called on errors during the work of [ViewBindingInflatingProcessor].
     * */
    override fun onError() =
        logger.error("The ${ViewBindingInflatingProcessor::class.simpleName} has stumbled onto some error")

    /**
     * This callback notifies about the completion of KSP.
     * */
    override fun finish() = logger.info("The ${ViewBindingInflatingProcessor::class.simpleName} has finished its work")

}
