package com.example.saverartical

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import org.jsoup.Jsoup
import java.lang.Exception

class InsPhotoParse{
    private  var artical: MutableLiveData<Artical> = MutableLiveData()

    fun getInsPhotoParserData(url :String) : Single<Artical> {
        return Single.create<Artical> {

            try {
                val doc= Jsoup.connect(url).get()
                val titledoc=doc.select("title")
                val img=doc.select("img")
                var title=titledoc.text()
                val arthor=doc.select("a.sqdOP yWX7d     _8A5w5   ZIAjV ")
                val imgUrlList= mutableListOf<String>()
                imgUrlList.add(img.attr("src"))
                it.onSuccess(Artical(title,"",arthor.text(),url,imgUrlList))
            }
            catch (e : Exception){
                it.onError(e)
            }

        }


    }
}