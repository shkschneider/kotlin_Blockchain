package tasks

import org.gradle.api.Task

fun Task.exec(commandLine: String) {
    doLast {
        project.exec {
            this.commandLine = commandLine.split(" ")
        }
    }
}
