pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LunmedTV"

include(":app")
include(":core:core-model")
include(":core:core-common")
include(":core:core-design")
include(":core:core-ui")
include(":core:core-database")
include(":core:core-datastore")
include(":core:core-data")
include(":core:core-navigation")

include(":feature:feature-home")
include(":feature:feature-apps")

