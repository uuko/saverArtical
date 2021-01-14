package com.example.saverartical

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
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
import android.net.Uri
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import java.io.InputStream
import java.net.HttpURLConnection

import com.itextpdf.text.DocumentException
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private var hasBind = false
    lateinit var lofterParser:LofterParser
    lateinit var czBookParser: CzBookParser
    lateinit var novel101Parser: Novel101Parser
    lateinit var insPhotoParse: InsPhotoParse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        submit.setOnClickListener {
            val url=text.text.toString()
            if (url.length>0){
                if (url.split("https://")[1].contains("czbooks.net")){
                    parseCzBooksAndSave(url)
                }

                else{
                    parseLofterAndSave(url)
                }
            }

        }
        submitAll.setOnClickListener {
            val url=text.text.toString()
            if (url.length>0){
                Toast.makeText(this,"目前還不支持整本功能",Toast.LENGTH_LONG)
//                if (url.split("https://")[1].contains("novel101.com")){
//                    parseNovelAllBooksAndSave(url)
//                } else{
//                    Toast.makeText(this,"目前還不支持整本功能",Toast.LENGTH_LONG)
//                }
            }
        }

    }
    private fun parseNovelAllBooksAndSave(url: String) {

        novel101Parser= Novel101Parser()

        novel101Parser.getNovel101DataSizes(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe(object : SingleObserver<Int> {
                override fun onSuccess(wh: Int) {
                    Log.d("onSuccess","onSuccess"+wh.toString())
                    text.text.clear()
                    var nowUrl="https://novel101.com/novels/813a951c-ee31-4930-9212-8b9d5a87dc95/chapters/og8"





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

    private fun parseInsPhotoAndSave(url: String) {
        insPhotoParse= InsPhotoParse()

        insPhotoParse.getInsPhotoParserData(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe(object : SingleObserver<Artical> {
                override fun onSuccess(t: Artical) {
                    Log.d("onSuccess","onSuccess"+t.toString())
                    text.text.clear()
                    saveToImg(t)
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
    private fun parseCzBooksAndSave(url: String) {
        czBookParser= CzBookParser()

        czBookParser.getCzBooksParserData(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe(object : SingleObserver<Artical> {
                override fun onSuccess(t: Artical) {
                    Log.d("onSuccess","onSuccess"+t.toString())
                    text.text.clear()
                        saveToFile(t)
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
    private fun parseLofterAndSave(url: String) {
        lofterParser= LofterParser()

        lofterParser.getLofterParserData(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe(object : SingleObserver<Artical> {
                override fun onSuccess(t: Artical) {
                    Log.d("onSuccess","onSuccess"+t.toString())
                    text.text.clear()
                    if (t.imgUrl.size>0){
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
                    else{
                        saveToFile(t)
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
    private fun saveToImg(t: Artical) :Completable {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)
        var name=formatedDate+".jpg"
        var filePath="/storage/emulated/0/DCIM"
        return Completable.create {
            run {

                if (!name.isEmpty()) {
                    try {
                        for (i in  t.imgUrl){
                            val url =  URL(i);
                            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                            val outFile = File(filePath, name)
                            val outputStream = FileOutputStream(outFile)
                            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // Compress Image
                            outputStream.flush();
                            outputStream.close();
                        }
                        it.onComplete()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        it.onError(e)
                    }

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



    fun startFloatingButtonService(view: View) {
        val floatingButtonService=FloatingButtonService()
        if (floatingButtonService.getStart()) {
            return
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "無權限", Toast.LENGTH_SHORT)
            startActivityForResult(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                ), 0
            )
        } else {
            //startService(Intent(this@MainActivity, FloatingButtonService::class.java))
            val intent = Intent(this, FloatingButtonService::class.java)
            hasBind = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    internal var mServiceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as FloatingButtonService.MyBinder
            binder.getServces()
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

}


