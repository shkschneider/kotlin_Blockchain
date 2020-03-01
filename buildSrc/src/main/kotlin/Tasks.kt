import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

fun TaskContainer.add(name: String, configurationAction: Action<Task>) {
    register(name) {
        configurationAction.execute(this)
    }
}

fun TaskContainer.alias(from: String, to: String) {
    register(from) {
        dependsOn(to)
    }
}
