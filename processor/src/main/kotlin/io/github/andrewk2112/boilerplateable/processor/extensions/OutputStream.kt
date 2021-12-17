package io.github.andrewk2112.boilerplateable.processor.extensions

import java.io.IOException
import java.io.OutputStream

/**
 * Syntax sugar to append [String]s to the [OutputStream].
 *
 * @param value A [String] value to append.
 *
 * @throws IOException A failed writing exception.
 * */
@Throws(IOException::class)
operator fun OutputStream.plus(value: String) = write(value.toByteArray())
