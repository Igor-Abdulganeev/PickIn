import com.android.build.api.dsl.SigningConfig
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) localProperties.load(FileInputStream(localPropertiesFile))

val vCode = libs.versions.version.code.get().toInt() ?: 8
val vName = libs.versions.version.name.get() ?: "3"

fun SigningConfig.loadFromPreference(prefix: String) {
    val path = localProperties.getProperty("${prefix}_PATH")
        ?: error("Can`t find local properties file")
    storeFile = file(path)
    storePassword = localProperties.getProperty("${prefix}_PASSWORD")
        ?: error("Wrong key store password")
    keyAlias = localProperties.getProperty("${prefix}_ALIAS") ?: error("Wrong key store alias")
    keyPassword = localProperties.getProperty("${prefix}_ALIAS_PASSWORD")
        ?: error("Wrong key store alias password")
}

android {
    namespace = "ru.gorinih.pickin"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "ru.gorinih.pickin"
        minSdk = 33
        targetSdk = 37
        versionCode = vCode
        versionName = vName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("liteProd") {
            loadFromPreference("KEYSTORE_LITE")
        }
        create("proProd") {
            loadFromPreference("KEYSTORE_PRO")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            isDebuggable = true
            isMinifyEnabled = false
        }
        release {
            isDebuggable = false

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }

        flavorDimensions += listOf("env", "tier")
        productFlavors {
            create("dev") {
                dimension = "env"
                applicationIdSuffix = ".test"
                versionNameSuffix = "-test"
            }

            create("prod") {
                dimension = "env"
            }

            create("lite") {
                dimension = "tier"
                signingConfig = signingConfigs.getByName("liteProd")
            }

            create("pro") {
                dimension = "tier"
                applicationIdSuffix = ".pro"
                versionNameSuffix = "-pro"
                signingConfig = signingConfigs.getByName("proProd")
            }
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

androidComponents {
    onVariants { variant ->
        val appName = "pickin"
        val buildType = variant.buildType
        val flavor = variant.flavorName
        val fileName = buildString {
            append(appName)
            append("-")
            append(buildType)
            append("_")
            append("$vName.$vCode")

            if (!flavor.isNullOrEmpty() && flavor != "prod") {
                append("-")
                append(flavor)
            }
            append(".apk")
        }
        println("FileName === $fileName")
        val variantName =
            "${flavor?.replaceFirstChar { it.uppercaseChar() }}${buildType?.replaceFirstChar { it.uppercaseChar() }}"
        tasks.register("assemble${variantName}Apk") {
            description = ""
            dependsOn("assemble${variantName}")
            doLast {
                val apkDir =
                    layout.buildDirectory.dir("outputs/apk/${flavor}/${buildType}").get().asFile

                apkDir.listFiles()?.forEach { file ->
                    if (file.extension == "apk") {
                        val renamedFile = File(file.parent, fileName)
                        file.renameTo(renamedFile)
                    }
                }
            }
        }
    }

    beforeVariants { variantBuilder ->
        val allowedVariants = setOf(
            "devLiteDebug",
            "devProDebug",
            "prodLiteRelease",
            "prodProRelease"
        )
        if (variantBuilder.name !in allowedVariants) {
            variantBuilder.enable = false
        }
    }

}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.android)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.core)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.webkit)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}