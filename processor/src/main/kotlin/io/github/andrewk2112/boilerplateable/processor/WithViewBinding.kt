package io.github.andrewk2112.boilerplateable.processor

/**
 * Generates the boilerplate code to inflate and refer some view or data binding in a controller class.
 *
 * @param bindingClassName A view or data binding fully qualified class name
 *                         which is going be plugged inside of the controller's boilerplate code.
 * */
@Target(AnnotationTarget.CLASS)        // specifies that only classes can be annotated with the annotation
@Retention(AnnotationRetention.SOURCE) // excluding the annotation from the compiled class files and reflection
// @Repeatable       - allows using the same annotation on a single element multiple times, is not needed now
// @MustBeDocumented - determines that an annotation is a part of public API
//                     and therefore should be included in the generated documentation
//                     for the element to which the annotation is applied, is not needed now
annotation class WithViewBinding(val bindingClassName: String) // unfortunately we can not restrict the type of the class,
                                                               // because Android sources are not available here;
                                                               // we can not use any class marker as well,
                                                               // because it won't be available in the processor
// For annotation classes allowed parameter types are:
// - types that correspond to Java primitive types (Int, Long, etc.);
// - strings;
// - classes (Foo::class);
// - enums;
// - other annotations;
// - arrays of the types listed above.
