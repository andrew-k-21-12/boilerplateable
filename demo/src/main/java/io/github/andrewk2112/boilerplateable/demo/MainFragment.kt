package io.github.andrewk2112.boilerplateable.demo

import android.os.Bundle
import android.view.View
import io.github.andrewk2112.boilerplateable.processor.WithViewBinding
import androidx.fragment.app.Fragment

/**
 * An example of a template declaration to inflate basic binding features inside a fragment.
 *
 * This class must be abstract, because there is no way to modify the original code,
 * and new templates are generated via inheritance.
 *
 * Yes, magic strings to point a binding class are super ugly,
 * but maybe one day it will be possible to use dynamic strings with such annotations
 * (see https://youtrack.jetbrains.com/issue/KT-49303)
 * or some advanced annotation processing means (without the need in access to end binding classes)
 * will be introduced for KSP.
 * */
@WithViewBinding("io.github.andrewk2112.boilerplateable.demo.databinding.FragmentMainBinding")
abstract class MainFragmentProto : Fragment()

/**
 * Using the generated boilerplate-containing template.
 * */
class MainFragment : MainFragmentBase() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Let's make sure the binding was inflated correctly, and we have the access to its referencing variable.
        binding.sampleLabel.setText(R.string.app_name)

    }

}
