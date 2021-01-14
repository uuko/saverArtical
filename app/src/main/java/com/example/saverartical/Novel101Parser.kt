package com.example.saverartical

import android.util.Log
import io.reactivex.Single
import org.jsoup.Jsoup
import java.lang.Exception

class Novel101Parser {

    fun getNovel101DataSizes(url: String):Single<Int>{
        return Single.create<Int>{
            try {
                val doc= Jsoup.connect(url).get()
                val sizes=doc.select("div.novel-chapters")
                it.onSuccess(sizes.size)
            }
            catch (e : Exception){
                it.onError(e)
            }

        }
    }
    fun getNovel101ParserData(url :String) : Single<Artical> {
        return Single.create<Artical> {

            try {
//                novel-chapters
                val doc= Jsoup.connect(url).get()
                val titledoc=doc.select("title")
                val content_doc=doc.select("div.content")
                var title=titledoc.text()
                Log.d("content",content_doc.text())

                var  content=content_doc.text()
                content = content.replace("  ", "\n")
                    .replace(" ", "\n")
                var author_list=doc.select("div.name")
                var author=author_list.text()
                val imgUrlList= arrayListOf<String>()

//                 artical.value=Artical(title,content,author,url,imgUrlList)
                it.onSuccess(Artical(title,content,author,url,imgUrlList))
            }
            catch (e : Exception){
                it.onError(e)
            }

        }


    }
}