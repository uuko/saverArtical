package com.example.saverartical

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import org.jsoup.Jsoup
import java.lang.Exception

class CzBookParser {
    private  var artical: MutableLiveData<Artical> = MutableLiveData()

    fun getCzBooksParserData(url :String) : Single<Artical> {
        return Single.create<Artical> {

            try {
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