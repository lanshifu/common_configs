package com.example.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author lanxiaobin
 * @date 2022/7/11
 */
open class DependenceCheckerPlugin : Plugin<Project> {
    companion object {
        val EXT_NAME = "dependenceChecker"
        val PLUGIN_NAME = "DependenceCheckerTask"
    }

    @Override
    override fun apply(project: Project) {

        project.extensions.create(EXT_NAME, DependenceChecker::class.java)

        project.afterEvaluate {

            val dependenceCheckerTask =
                project.tasks.create(EXT_NAME, DependenceCheckerTask::class.java)

            val preBuildTask = it.tasks.findByName("preBuild")
            Log.info("findByName is $preBuildTask")
            preBuildTask?.dependsOn(dependenceCheckerTask)
        }

    }

}