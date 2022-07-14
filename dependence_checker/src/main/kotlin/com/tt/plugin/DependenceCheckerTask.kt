package com.tt.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.result.DependencyResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.HashSet


/**
 * 检测 依赖库版本
 * 如果App 依赖了 Okhttp 4.9.3 ，Library依赖了 Okhttp 4.10.0 就报错
 *
 */
open class DependenceCheckerTask : DefaultTask() {

    private val dependenceChecker = project.extensions.getByName(DependenceCheckerPlugin.EXT_NAME) as DependenceChecker

    @Internal
    var depMap =  mutableMapOf<String, MutableSet<VersionInfo>>()

    @Internal
    val whiteList = dependenceChecker.whiteList
//    val whiteList = listOf( "androidx", "org", "com.google")

    @TaskAction
    open fun check() {

        val startTime = System.currentTimeMillis()

        println(project.configurations)

        println("DependenceCheckerTask->check configurations.size=${project.configurations.size},whiteList=$whiteList")
        project.configurations.forEach() { configuration ->

            // name=releaseCompileClasspath
            println("DependenceCheckerTask->check,name=${configuration.name}")
            // 获取所有依赖信息，有些不
            /// resolutionResult 会报错
            // 'androidTestReleaseAnnotationProcessor' is not allowed as it is defined as 'canBeResolved=false'
            try {
                val resolutionResult = configuration.incoming.resolutionResult
                println("DependenceCheckerTask->root.dependencies.size =${resolutionResult.root.dependencies.size}")
                resolutionResult.root.dependencies.forEach { dr ->
                    addQueue(dr)
                }

            } catch (e: Exception) {
            }
        }

        handleQueue()
        println("-------->DependenceCheckerTask->const ${System.currentTimeMillis() - startTime} ms")
        reportDepInfo(depMap, dependenceChecker.abortBuild)
    }


    @Internal
    var queue = LinkedBlockingQueue<DependencyResult>()

    fun addQueue(dr:DependencyResult){
        println("DependenceCheckerTask addQueue, $dr ,from=${dr.from}")
        queue.offer(dr)
    }

    fun queueTask(dr: DependencyResult){
        var depName = dr.requested.displayName
        println("DependenceCheckerTask queueTask,displayName=$depName")
        // 过滤 project :javalibrary，不过滤其实也没问题
        if (!depName.contains("project")) {
            var depSplit = depName.split(":")
            if (depSplit.size > 2) {
                var packageName = depSplit[0] + depSplit[1]
                val version =  depSplit[2]
                // version={strictly 2.1.0} 这种一般就是 library依赖的库的版本比App高，
                // 就是要特殊处理一下
                val realVersion = version
                    .replace("strictly","")
                    .replace("{","")
                    .replace("}","")
                    .replace("\\s".toRegex(), "")
                println("DependenceCheckerTask queueTask,packageName=$packageName,realVersion=$realVersion,dependencies size=${dr.from.dependencies.size}")

                // 版本判断
                val versionInfo = VersionInfo(depName,realVersion,dr.from.toString())
                var list = depMap.get(packageName)
                if (list == null) {
                    list =  HashSet<VersionInfo>()
                    depMap.put(packageName, list)
                }

                if(list.size >0){
                    var sameVersion = false
                    list.forEach {
                        if(it.version == realVersion){
                            sameVersion = true
                        }
                    }
                    if (!sameVersion) {
                        if(!isInWhiteList(versionInfo.packageName)){
                            list.add(versionInfo)
                        }
                    }

                } else {
                    if(!isInWhiteList(versionInfo.packageName)){
                        list.add(versionInfo)
                    }
                }

                if (dr is ResolvedDependencyResult) {
                    dr.selected.dependencies.forEach { subDr ->
                        addQueue(subDr)
                    }
                }
            }

        }
    }

    fun isInWhiteList(packageName: String): Boolean {
        whiteList.forEach {
            if(packageName.startsWith(it)){
                return true
            }
        }
        return false

    }

    fun handleQueue(){
        println("DependenceCheckerTask handleQueue,start")
        while (queue.isNotEmpty()){
            val poll = queue.poll()
            queueTask(poll)
        }
        println("DependenceCheckerTask handleQueue,end!")
    }


    fun reportDepInfo(depMap:Map<String, Set<VersionInfo>> ,  abortBuild:Boolean) {

        val result:MutableList<Set<VersionInfo>> = mutableListOf()
        depMap.values.forEach {
            if (it.size> 1) {
                result.add(it)
            }
        }

        println("依赖库版本冲突，size=${result.size}")
        if(result.size > 1){
            result.forEach {
                println("\n依赖库版本不一致:${it}")
            }
            if(abortBuild){
                throw  RuntimeException("依赖库版本冲突，请检查！,解决方案两种：" +
                        "1.exclude" +
                        "2.强制使用某个版本 configurations.all {\n" +
                        "        resolutionStrategy.force \"com.xx:xxx:xx\"\n" +
                        "    }")
            }

        }


    }

}