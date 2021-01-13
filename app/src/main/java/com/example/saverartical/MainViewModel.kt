package com.example.saverartical

import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel(){
    val artical = LofterParser().getLofterParserData("https://aer11.lofter.com/post/30e71f3e_1ca9d1cd4")

}