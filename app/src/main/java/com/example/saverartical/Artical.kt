package com.example.saverartical

data class Artical(var title: String,
                   var content: String,
                   var author: String,
                   var url:String,
                   var imgUrl:List<String>) {

    override fun toString(): String {
        var imgUrlString="";
        for (i in 0 until imgUrl.size){
            imgUrlString=imgUrlString+"   ,"+imgUrl[i]
        }
        return "Article{" +
                "title='" + title + '\''.toString() +
                ", content='" + content + '\''.toString() +
                ", author='" + author + '\''.toString() +
                ", url='" + url + '\''.toString() +
                ", imgUrl='" + imgUrlString + '\''.toString() +
                '}'.toString();
    }
}