package com.example.saverartical

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Emitter
import io.reactivex.Single
import org.jsoup.Jsoup
import java.lang.Exception


class LofterParser(){
    private  var artical: MutableLiveData<Artical> = MutableLiveData()

     fun getLofterParserData(url :String) : Single<Artical>{
         return Single.create<Artical> {

             try {
                 val doc=Jsoup.connect(url).get()
                 val titledoc=doc.select("title")
                 var imgUrl=doc.select("div.content").select("div.img").select("a").select("img[src]")
                 val content_doc=doc.select("p")
                 var title=titledoc.text()
                 Log.d("content",content_doc.text())

                 var  content=content_doc.text()
                 content = content.replace("  ", "\n")
                     .replace(" ", "\n")
                 val author_title=title.split("-")
                 var author=""
                 if (author_title.size>1){
                     author=author_title.get(author_title.size-1)
                 }
                 else{
                     author = url.replace("http://", "")
                     author = author.split(".".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                 }

                 val imgUrlList= arrayListOf<String>()
                 if (imgUrl.size>0){
                     val sizes=imgUrl.size
                     for (i in 0 until sizes){
                        Log.d("aaaa",""+imgUrl[i].select("img[src]").attr("src"))
                         imgUrlList.add(imgUrl[i].select("img[src]").attr("src"))
                     }
                 }

//                 artical.value=Artical(title,content,author,url,imgUrlList)
                 it.onSuccess(Artical(title,content,author,url,imgUrlList))
             }
             catch (e : Exception){
                 it.onError(e)
             }

         }


    }
}