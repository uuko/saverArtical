package com.example.saverartical

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.reactivestreams.Subscriber
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    lateinit var lofterParser:LofterParser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        submit.setOnClickListener {
            lofterParser= LofterParser()
            
            lofterParser.getLofterParserData("https://sandynoer.lofter.com/post/1d55dc3a_1caa57483")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(object : SingleObserver<Artical> {
                    override fun onSuccess(t: Artical) {
                      Log.d("onSuccess","onSuccess"+t.toString())
                        saveToFile(t).subscribe (object:CompletableObserver{
                            override fun onSubscribe(d: Disposable) {
                                Log.d("onSubscribe","onSubscribe  saveToFile")

                            }

                            override fun onError(e: Throwable) {
                                Log.d("onError","onError  saveToFile"+e)
                            }

                            override fun onComplete() {
                                Log.d("onComplete","onComplete  saveToFile")
                            }

                        })
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d("onSubscribe","onSubscribe")
                        showProgressBar()
                    }

                    override fun onError(e: Throwable) {
                        Log.d("onError","onError"+e)
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

    private fun dismissProgressBar() {
        progress_circular.visibility= View.GONE
    }

    private fun showProgressBar() {
        progress_circular.visibility= View.VISIBLE
    }

}


