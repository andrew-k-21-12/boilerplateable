plugins {
    kotlin("jvm")
}

dependencies {

    // Includes KSP classes to be extended for creation of custom KSP processors.
    implementation("com.google.devtools.ksp:symbol-processing-api:${Configs.kspVersion}")

}
