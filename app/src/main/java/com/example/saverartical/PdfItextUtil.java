package com.example.saverartical;


import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.PngImage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class PdfItextUtil {

    private Document document;
    private Context c;
    // savePath:保存pdf的路径
    public PdfItextUtil(Context c,String savePath) throws FileNotFoundException, DocumentException {
        //创建新的PDF文档：A4大小，左右上下边框均为0
        this.c=c;
        document = new Document(PageSize.A4,50,50,30,30);
        //获取PDF书写器
        PdfWriter.getInstance(document, new FileOutputStream(savePath));
        //打开文档
        document.open();
    }
    public float getWidth(){
        return document.getPageSize().getWidth();
    }
    public float getleftMargin(){
        return document.leftMargin();
    }
    public void close(){
        if (document.isOpen()) {
            document.close();
        }
    }

    // 添加图片到pdf中，这张图片在pdf中居中显示
    // imgPath:图片的路径，我使用的是sdcard中图片
    // imgWidth：图片在pdf中所占的宽
    // imgHeight：图片在pdf中所占的高
    public PdfItextUtil addImageToPdfCenterH(@NonNull String imgPath) throws IOException, DocumentException {
        //获取图片
        Image img = Image.getInstance(imgPath);
//        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
//                - document.rightMargin() - 0) / img.getWidth()) * 100;
        img.setAlignment(Element.ALIGN_CENTER);
        img.scaleToFit(document.getPageSize().getWidth()-document.leftMargin()-document.rightMargin()
                ,document.getPageSize().getHeight()-document.bottomMargin()-document.topMargin());
//        img.scalePercent(scaler);
        //添加到PDF文档
        document.add(img);

        return this;
    }

    public PdfItextUtil addPngToPdf(InputStream inputStream) throws DocumentException, IOException {
        Image img = PngImage.getImage(inputStream);
        img.setAlignment(Element.ALIGN_CENTER);
        //添加到PDF文档
        document.add(img);
        return this;
    }

    // 添加文本到pdf中
    public PdfItextUtil addTextToPdf(String content) throws DocumentException, UnsupportedEncodingException {

        Paragraph elements = new Paragraph(content,setChineseFont(12));

        elements.setAlignment(Element.ALIGN_BASELINE);
//        elements.setIndentationLeft(55);  //设置距离左边的距离
        document.add(elements); // result为保存的字符串
        return this;
    }

    // 给pdf添加个标题，居中黑体
    public PdfItextUtil addTitleToPdf(String title){
        try {
            Paragraph elements = new Paragraph(title);
            elements.setAlignment(Element.ALIGN_CENTER);
            document.add(elements); // result为保存的字符串
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return this;
    }

    private Font setChineseFont(int size) {

        BaseFont bf = null;
        Font fontChinese = null;
        try {
//            BaseFont chinese = BaseFont.createFont("assets/kaiu.ttf"
//                    , BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);//設置中文字型
            bf = BaseFont.createFont("assets/genryu.ttf"
                    , BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);//設置中文字型
            fontChinese = new Font(bf, 12, Font.NORMAL);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fontChinese;

    }




}




