package com.tt.plugin

/**
 * @author lanxiaobin
 * @date 2022/7/11
 */
open class DependenceChecker(){
    var variant: String = "";

    var abortBuild: Boolean = true

    var whiteList: List<String> = emptyList()
    override fun toString(): String {
        return "DependenceChecker(variant='$variant', abortBuild=$abortBuild, whiteList=$whiteList)"
    }


}