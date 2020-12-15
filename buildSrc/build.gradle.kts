plugins {
	`kotlin-dsl`
}

repositories {
	mavenCentral()
	google()
	jcenter()
}

dependencies {
	/* Depend on the android gradle plugin, since we want to access it in our plugin */
	implementation("com.android.tools.build:gradle:4.1.1")

	/* Depend on the kotlin plugin, since we want to access it in our plugin */
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")

	implementation(kotlin("android-extensions"))

	/* Depend on the default Gradle APIs since we want to build a custom plugin */
	//implementation(gradleApi())
	implementation(localGroovy())
}
