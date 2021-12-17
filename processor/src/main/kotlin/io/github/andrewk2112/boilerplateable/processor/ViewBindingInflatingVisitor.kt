package io.github.andrewk2112.boilerplateable.processor

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import io.github.andrewk2112.boilerplateable.processor.extensions.plus
import java.io.IOException

/**
 * This visitor walks through all elements on a symbol it's applied too and performs the required code generations.
 */
@KspExperimental
class ViewBindingInflatingVisitor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : KSVisitorVoid() {

    // Overrides.

    /**
     * Self-explanatory - processes each [classDeclaration] it's applied to.
     */
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

        // Checking and preparing all prerequisites.
        val bindingClassName       = extractBindingClassName(classDeclaration) ?: return
        val originalContainingFile = classDeclaration.containingFile           ?: return
        if (!hasSupportedFragmentSuperClass(classDeclaration)) return

        // Preparing building blocks for the class going to be generated.
        val packageName        = originalContainingFile.packageName.asString()
        val originalClassName  = classDeclaration.simpleName.asString()
        val generatedClassName = prepareGeneratedClassName(originalClassName)

        try {
            generateBindingTemplateClass(
                originalContainingFile,
                packageName,
                originalClassName,
                generatedClassName,
                bindingClassName
            )
        } catch (exception: IOException) {
            logger.error(
                "Could not generate a template binding class for $originalClassName: ${exception.localizedMessage}"
            )
        }

        // It's possible to pass the processing to some other visitor's branch -
        // for example, a primary constructor can be processed as a function separately by doing the following call
        // and overriding the corresponding visit* method:
        // classDeclaration.primaryConstructor?.accept(this, data)

    }





    // Private.

    /**
     * Extracts a binding class name which should be used as the layout for the annotated controller.
     *
     * @return A binding class name or null in a case of some really rare error.
     * */
    private fun extractBindingClassName(classDeclaration: KSClassDeclaration): String? {
        for (annotation in classDeclaration.getAnnotationsByType(WithViewBinding::class)) {
            if (annotation.bindingClassName.isNotBlank()) {
                return annotation.bindingClassName
            }
        }
        return null
    }

    /**
     * Checks whether the [classDeclaration] being processed confirms to the [supportedFragmentSuperClass].
     * */
    private fun hasSupportedFragmentSuperClass(classDeclaration: KSClassDeclaration): Boolean {
        for (superType in classDeclaration.getAllSuperTypes()) {
            if (superType.declaration.qualifiedName?.asString() == supportedFragmentSuperClass) return true
        }
        return false
    }

    /**
     * Prepares a class name to be used as the generated template.
     *
     * @param originalClassName An original name of a class marked to generate the template for.
     * */
    private fun prepareGeneratedClassName(originalClassName: String): String {
        val trimmedClassName = if (originalClassName.endsWith(classNameEndingToDrop)) {
            originalClassName.substringBeforeLast(classNameEndingToDrop)
        } else {
            originalClassName
        }
        return trimmedClassName + classNameEndingToAppend
    }

    /**
     * Generates a view / data binding template controller class
     * according to the extracted and prepared prerequisites.
     *
     * @throws IOException In the case of any I/O errors.
     * */
    @Throws(IOException::class)
    private fun generateBindingTemplateClass(
        originalContainingFile: KSFile,
        packageName: String,
        originalClassName: String,
        generatedClassName: String,
        bindingClassName: String
    ) {

        // For more complicated cases the KotlinPoet library (https://square.github.io/kotlinpoet) can be used,
        // but for simple scenarios it's pretty enough to use manual ways.
        codeGenerator.createNewFile(
            Dependencies(true, originalContainingFile), // KSFiles from which this output is built,
                                                        // see the docs for this method for details
            packageName,
            generatedClassName
        ).use { out ->

            if (packageName.isNotBlank()) {
                out + "package $packageName\n\n"
            }

            out + """
                |import android.os.Bundle
                |import android.view.LayoutInflater
                |import android.view.View
                |import android.view.ViewGroup
                |
                |abstract class $generatedClassName : $originalClassName() {
                |
                |    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
                |        ${bindingClassName}.inflate(inflater, container, false).also {
                |            binding = it
                |        }.root
                |        
                |    protected lateinit var binding: $bindingClassName
                |    
                |}
            """.trimMargin()

            out.flush()

        }

    }

    /** This is the only super class supported to generate binding templates for. */
    private val supportedFragmentSuperClass = "androidx.fragment.app.Fragment"

    /** The class name ending to be dropped from original templates if met. */
    private val classNameEndingToDrop = "Proto"

    /** The suffix to be appended for generated template classes. */
    private val classNameEndingToAppend = "Base"

}
