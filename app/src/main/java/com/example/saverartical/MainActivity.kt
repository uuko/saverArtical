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

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri

import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.net.URL

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    private var hasBind = false
    lateinit var lofterParser:LofterParser
    lateinit var czBookParser: CzBookParser
    lateinit var novel101Parser: Novel101Parser
    lateinit var stoBookParser: StoCxParser
    lateinit var insPhotoParse: InsPhotoParse
    var cookie=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                  cookie=CookieManager.getInstance().getCookie(url)
                Log.d("aa",cookie)
            }
        }
        webview.loadUrl("https://www.sto.cx/pcindex.aspx");



            submit.setOnClickListener {
            val url=text.text.toString()
            if (url.length>0){
                if (url.split("https://")[1].contains("czbooks.net")){

                    parseCzBooksAndSave(url)
                }
                else if (url.split("https://")[1].contains("www.sto.cx")){
                    var mUrl=url.replace("mbook","book")
                    parseStoAndSave(url)
                }
                else{
                    parseLofterAndSave(url)
                }
            }

        }
        submitAll.setOnClickListener {
            val url=text.text.toString()
            if (url.length>0){

                if (url.split("https://")[1].contains("czbooks.net")){
                    parseNovelAllBooksAndSave(url)
                }
                else if (url.split("https://")[1].contains("www.sto.cx")){
                    var mUrl=url.replace("mbook","book")
                    parseStoAllBooksAndSave(mUrl)
                }else{
                    Toast.makeText(this,"目前還不支持整本功能",Toast.LENGTH_LONG)
                }
            }
        }

    }

    private fun setLoadText(s:String,isVisible: Boolean) {
        if (isVisible){
            loadText.visibility=View.VISIBLE
            loadText.text=s
        }
        else{
            loadText.visibility=View.GONE
            loadText.text=s
        }

    }
    var nowInt=0
    private fun  parseNovelOneBookAndSave(wh: List<String>){
        var isEnd=false
        czBookParser.getCzBookAllBookParseData(wh,nowInt,isEnd)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Artical> {
                override fun onSuccess(ar:Artical) {
                    Log.d("onSuccess","onSuccess"+wh.toString())
                    if (nowInt<wh.size-2){
                        nowInt++
                        setLoadText("下載第"+nowInt+"章中",true)
                        isEnd=false
                        parseNovelOneBookAndSave(wh)
                    }
                    else if (nowInt==wh.size-2){
                        nowInt++
                        setLoadText("下載第"+nowInt+"章中",true)
                        isEnd=true
                        parseNovelOneBookAndSave(wh)
                    }
                    else{
                        nowInt=0
                        saveToFile(ar)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe (object:CompletableObserver{
                                override fun onSubscribe(d: Disposable) {
                                    Log.d("onSubscribe","onSubscribe  saveToFile")
                                    setLoadText("儲存中",true)
                                }

                                override fun onError(e: Throwable) {
                                    Log.d("onError","onError  saveToFile"+e)
                                    setLoadText("儲存失敗",true)
                                    dismissProgressBar()
                                }

                                override fun onComplete() {
                                    Log.d("onComplete","onComplete  saveToFile")
                                    setLoadText("儲存中",false)
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
                    setLoadText("下載失敗",true)
                    nowInt=0
                    dismissProgressBar()
                }


            })
    }
    private fun parseNovelAllBooksAndSave(url: String) {

        czBookParser= CzBookParser()

        czBookParser.getCzBookAllBookData(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe(object : SingleObserver<List<String>> {
                override fun onSuccess(wh: List<String>) {
                    Log.d("onSuccess","onSuccess"+wh.toString())
                    text.text.clear()
                    setLoadText("總共有"+wh.size+"章，載入中",true)
                    parseNovelOneBookAndSave(wh)

                }

                override fun onSubscribe(d: Disposable) {
                    Log.d("onSubscribe","onSubscribe")
                    setLoadText("載入中",true)
                    showProgressBar()
                }

                override fun onError(e: Throwable) {
                    Log.d("onError","onError"+e)
                    text.text.clear()
                    setLoadText("載入失敗",true)
                    dismissProgressBar()
                    setLoadText("載入失敗",false)
                }


            })
    }

    var stoNowInt=0
    private fun  parseStoOneBookAndSave(wh: List<String>){
        var isEnd=false
        stoBookParser.getStBookAllBookParseData(wh,nowInt,cookie =cookie )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Artical> {
                override fun onSuccess(ar:Artical) {
                    Log.d("onSuccess","onSuccess"+wh.toString())
                    if (nowInt<wh.size-2){
                        nowInt++
                        setLoadText("下載第"+nowInt+"章中",true)
                        isEnd=false
                        parseStoOneBookAndSave(wh)
                    }
                    else if (nowInt==wh.size-2){
                        nowInt++
                        setLoadText("下載第"+nowInt+"章中",true)
                        isEnd=true
                        parseStoOneBookAndSave(wh)
                    }
                    else{
                        nowInt=0
                        saveToFile(ar)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe (object:CompletableObserver{
                                override fun onSubscribe(d: Disposable) {
                                    Log.d("onSubscribe","onSubscribe  saveToFile")
                                    setLoadText("儲存中",true)
                                }

                                override fun onError(e: Throwable) {
                                    Log.d("onError","onError  saveToFile"+e)
                                    setLoadText("儲存失敗",true)
                                    dismissProgressBar()
                                }

                                override fun onComplete() {
                                    Log.d("onComplete","onComplete  saveToFile")
                                    setLoadText("儲存中",false)
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
                    setLoadText("下載失敗",true)
                    nowInt=0
                    dismissProgressBar()
                }


            })
    }
    private fun parseStoAllBooksAndSave(url: String) {

        stoBookParser= StoCxParser()

        stoBookParser.getStoBookAllBookData(url,cookie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe(object : SingleObserver<List<String>> {
                override fun onSuccess(wh: List<String>) {
                    Log.d("onSuccess","onSuccess"+wh.toString())
                    text.text.clear()
                    setLoadText("總共有"+wh.size+"章，載入中",true)
                    parseStoOneBookAndSave(wh)

                }

                override fun onSubscribe(d: Disposable) {
                    Log.d("onSubscribe","onSubscribe")
                    setLoadText("載入中",true)
                    showProgressBar()
                }

                override fun onError(e: Throwable) {
                    Log.d("onError","onError"+e)
                    text.text.clear()
                    setLoadText("載入失敗",true)
                    dismissProgressBar()
                    setLoadText("載入失敗",false)
                }


            })
    }
    private fun parseStoAndSave(url: String) {
        stoBookParser= StoCxParser()

        stoBookParser.getStoCxParserData(url,cookie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe(object : SingleObserver<Artical> {
                override fun onSuccess(t: Artical) {
                    Log.d("onSuccess","onSuccess"+t.toString())
                    text.text.clear()



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
                    setLoadText("下載成功",true)
                    text.text.clear()
                        saveToFile(t)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe (object:CompletableObserver{
                                override fun onSubscribe(d: Disposable) {
                                    Log.d("onSubscribe","onSubscribe  saveToFile")
                                    setLoadText("儲存中",true)
                                }

                                override fun onError(e: Throwable) {
                                    Log.d("onError","onError  saveToFile"+e)
                                    setLoadText("儲存失敗",true)
                                    dismissProgressBar()
                                }

                                override fun onComplete() {
                                    Log.d("onComplete","onComplete  saveToFile")
                                    setLoadText("儲存中",false)
                                    dismissProgressBar()
                                }

                            })


                }

                override fun onSubscribe(d: Disposable) {
                    Log.d("onSubscribe","onSubscribe")
                    setLoadText("下載中",true)
                    showProgressBar()
                }

                override fun onError(e: Throwable) {
                    Log.d("onError","onError"+e)
                    setLoadText("下載失敗",true)
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
                    setLoadText("下載成功",true)
                    text.text.clear()
                    if (t.imgUrl.size>0){
                        saveToWord(t)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe (object:CompletableObserver{
                                override fun onSubscribe(d: Disposable) {
                                    Log.d("onSubscribe","onSubscribe  saveToFile")
                                    setLoadText("儲存中",true)
                                }

                                override fun onError(e: Throwable) {
                                    Log.d("onError","onError  saveToFile"+e)
                                    setLoadText("儲存失敗",true)
                                    dismissProgressBar()
                                }

                                override fun onComplete() {
                                    Log.d("onComplete","onComplete  saveToFile")
                                    setLoadText("儲存中",false)
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
                                    setLoadText("儲存中",true)
                                }

                                override fun onError(e: Throwable) {
                                    Log.d("onError","onError  saveToFile"+e)
                                    setLoadText("儲存失敗",true)
                                    dismissProgressBar()

                                }

                                override fun onComplete() {
                                    Log.d("onComplete","onComplete  saveToFile")
                                    setLoadText("儲存中",false)
                                    dismissProgressBar()
                                }

                            })
                    }

                }

                override fun onSubscribe(d: Disposable) {
                    Log.d("onSubscribe","onSubscribe")
                    setLoadText("下載中",true)
                    showProgressBar()
                }

                override fun onError(e: Throwable) {
                    Log.d("onError","onError"+e)
                    setLoadText("下載失敗",true)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
                } else {

//                    Handler().postDelayed({
//                        val intent = Intent(this, FloatingButtonService::class.java)
//                        intent.putExtra("rangeTime", rangeTime)
//                        hasBind =
//                            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
//                        moveTaskToBack(true)
//                    }, 1000)

                }
            }
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


