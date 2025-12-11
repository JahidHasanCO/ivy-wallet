plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.backupdata"
}

dependencies {
    implementation(projects.feature.onboarding)
    implementation(projects.shared.base)
    implementation(projects.shared.data.core)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)


    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)

    implementation(libs.google.play.auth)
    implementation(libs.google.auth.oauth)
    implementation(libs.google.drive)
    implementation(libs.bundles.google.drive)
}