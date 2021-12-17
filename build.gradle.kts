plugins {
    kotlin("jvm")                 version Configs.kotlinVersion                   apply false
    kotlin("android")             version Configs.kotlinVersion                   apply false
    id("com.android.application") version Configs.androidApplicationPluginVersion apply false

    // KSP plugin to leverage the corresponding features.
    id("com.google.devtools.ksp") version Configs.kspVersion                      apply false

}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
