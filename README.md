This is an example of [Kotlin Symbol Processing API](https://github.com/google/ksp) usage 
to generate template classes with basic view binding features plugged into Android fragments.

**Why can it be helpful?** Because it allows avoiding lots of similar boilerplate chunks 
which introduce excessive code duplications or spoil inheritance for no adequate reason.

The main idea is to replace boilerplate for configuring view / data bindings with something like this:

```kotlin 
@WithViewBinding("io.github.andrewk2112.boilerplateable.demo.databinding.FragmentMainBinding")
abstract class MainFragmentProto : Fragment()

class MainFragment : MainFragmentBase() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sampleLabel.setText(R.string.app_name)
    }

}
```

The `MainFragment` class has got the reference to the binding inflated inside via the `binding` variable.

**How to use it?** There is no any artifact published. 
It's just a documented example to describe and explain basics of KSP's practical usage.
Start reviewing from the comments provided inside the `build.gradle` files for the `processor` and `demo` modules.
