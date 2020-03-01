package tasks

import org.gradle.api.Task

fun Task.gitDescribe() {
    group = "Versioning"
    description = "Git describe (tags)"
    exec("git describe --tags --always --first-parent")
}
