plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.settings"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.data.core)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)
    implementation(projects.widget.balance)

    testImplementation(projects.shared.ui.testing)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)

    implementation(libs.google.play.auth)
    implementation(libs.google.auth.oauth)
    implementation(libs.google.drive)
    implementation(libs.bundles.google.drive)
}
