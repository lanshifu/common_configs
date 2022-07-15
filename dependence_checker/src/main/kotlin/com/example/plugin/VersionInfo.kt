package com.example.plugin

/**
 * @author lanxiaobin
 * @date 2022/7/12
 */
data class VersionInfo(val packageName:String,val version:String,val module:String) {
    override fun equals(other: Any?): Boolean {
        if(other is VersionInfo){
            return other.version == version
        }
        return super.equals(other)
    }

}