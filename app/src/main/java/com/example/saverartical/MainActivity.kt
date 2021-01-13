package com.example.saverartical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.InputStream
import java.net.HttpURLConnection

import com.itextpdf.text.DocumentException
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService







class MainActivity : AppCompatActivity() {

    lateinit var lofterParser:LofterParser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        submit.setOnClickListener {
            lofterParser= LofterParser()
            val url=text.text.toString()
            lofterParser.getLofterParserData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(object : SingleObserver<Artical> {
                    override fun onSuccess(t: Artical) {
                      Log.d("onSuccess","onSuccess"+t.toString())
                        text.text.clear()
                        saveToWord(t)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe (object:CompletableObserver{
                            override fun onSubscribe(d: Disposable) {
                                Log.d("onSubscribe","onSubscribe  saveToFile")

                            }

                            override fun onError(e: Throwable) {
                                Log.d("onError","onError  saveToFile"+e)
                                dismissProgressBar()
                            }

                            override fun onComplete() {
                                Log.d("onComplete","onComplete  saveToFile")
                                dismissProgressBar()
                            }

                        })
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d("onSubscribe","onSubscribe")

                        showProgressBar()
                    }

                    override fun onError(e: Throwable) {
                        Log.d("onError","onError"+e)
                        text.text.clear()
                        dismissProgressBar()
                    }


                })


        }

    }

    private fun saveToFile(t: Artical) :Completable {
        var name=t.title+".txt"
        var filePath="/storage/emulated/0/DCIM"
       return Completable.create {
           run {

               if (!name.isEmpty()) {
                   try {
                       val outFile = File(filePath, name)
                       val outputStream = FileOutputStream(outFile)
                       outputStream.write(t.content.toByteArray())
                       it.onComplete()
                   } catch (e: IOException) {
                       e.printStackTrace()
                       it.onError(e)
                   }

               }
           }
        }

    }
    private fun saveToWord(t: Artical) :Completable {
        return Completable.create {
            run {

                var pdfItextUtil: PdfItextUtil? = null
                try {


                    pdfItextUtil = PdfItextUtil(this,"/storage/emulated/0/DCIM/"+t.title+".pdf")
                        .addTitleToPdf(t.title)
                        .addTextToPdf(t.author)
                        .addTextToPdf(t.content)
//                        .addImageToPdfCenterH(t.imgUrl[0], 160f, 160f)
                    for (i in 0 until  t.imgUrl.size) {

                        pdfItextUtil.addImageToPdfCenterH(t.imgUrl[i]);
                    }
//                        .addImageToPdfCenterH(getImageFilePath(), 160f, 160f)
//                        .addTextToPdf(getTvString(tv_content))
                } catch (e: IOException) {
                    e.printStackTrace()
                    it.onError(e)
                }  finally {
                    pdfItextUtil?.close()
                    it.onComplete()
                }

            }
        }

    }


    private fun dismissProgressBar() {
        progress_circular.visibility= View.GONE
    }

    private fun showProgressBar() {
        progress_circular.visibility= View.VISIBLE
    }

}


