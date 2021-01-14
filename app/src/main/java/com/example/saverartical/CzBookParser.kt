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

    fun getCzBookAllBookData(url: String) : Single<List<String>>{
        return Single.create<List<String>> {

            try {
                val doc= Jsoup.connect(url).get()
                val liUI=doc.select("ul.chapter-list").select("li")

                val imgUrlList= arrayListOf<String>()
                for (i in 1 until liUI.size-1){
                    imgUrlList.add(liUI.select("a")[i].attr("href"))
                }
//                 artical.value=Artical(title,content,author,url,imgUrlList)

                it.onSuccess(imgUrlList)
            }
            catch (e : Exception){
                it.onError(e)
            }

        }
    }

    var nowInWhere=0
    var  allContent=""
    fun getCzBookAllBookParseData(url: List<String>,nowInt: Int,end:Boolean) : Single<Artical>{
        return Single.create<Artical> {

            try {


                    val doc= Jsoup.connect("https://"+url[nowInt]).get()
                    val titledoc=doc.select("title")
                    val content_doc=doc.select("div.content")
                    var title=titledoc.text()
                    Log.d("content",content_doc.text())
                    var author=doc.select("div.position").select("a")[1].text()
                    val  content=content_doc.text()
                    allContent += "第"+nowInt.toString()+"章End..."+"\n"+content.replace("  ", "\n")
                        .replace(" ", "\n")
//                    val author_list=doc.select("div.name")
//                    var author=author_list.text()
                    val imgUrlList= arrayListOf<String>()
                it.onSuccess(Artical(author,allContent,title,url[nowInt],imgUrlList))






//                 artical.value=Artical(title,content,author,url,imgUrlList)


            }
            catch (e : Exception){
                it.onError(e)
            }

        }
    }
}