package com.example.androidlibrary

import android.util.Log
import okhttp3.OkHttpClient

/**
 * @author lanxiaobin
 * @date 2022/7/11
 */
class AndroidLibraryTest {
    companion object {
        fun print(){
            Log.d("AndroidLibraryTest", "print: ")
            test()
        }

        fun test() {
            var okHttpClient = OkHttpClient()
        }
    }


}