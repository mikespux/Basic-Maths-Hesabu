package com.wachi.hesabu.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.wachi.hesabu.R;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private PdfTemplate t;

    private float height, width;
    private Context c;
    private String school_name, link, waterMarkText;
    public static int page_number;

    private Bitmap bitmapLogo;

    public HeaderFooterPageEvent(Context context, float height, float width, String school_name, String link, Bitmap bitmapLogo, String waterMarkText) {
        this.c = context;
        this.height = height;
        this.width = width;
        this.link = link;
        this.bitmapLogo = bitmapLogo;
        this.school_name = school_name;
        this.waterMarkText = waterMarkText;
    }

    public void onOpenDocument(PdfWriter writer, Document document) {
        t = writer.getDirectContent().createTemplate(30, 0);


    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        page_number++;

        addHeader(writer);
        addFooter(writer, link);
        PdfContentByte canvas = writer.getDirectContent();

        int margin = 18;
        int marginTop = 80;


        Rectangle rect = new Rectangle(document.getPageSize().getRight() - margin, document.getPageSize().getTop() - margin, margin, marginTop); // you can resize rectangle
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        canvas.rectangle(rect);

//        Font f = new Font(Font.FontFamily.HELVETICA, 30);

        Font FONT = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD, new GrayColor(0.75f));
//        Font FONT = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD, new GrayColor(0.85f));

        Phrase p = new Phrase(waterMarkText, FONT);

        ColumnText.showTextAligned(writer.getDirectContentUnder(),
                Element.ALIGN_CENTER, p,
                297.5f, 421, 45);
//                297.5f, 421, writer.getPageNumber() % 2 == 1 ? 45 : -45);


    }

    private Image getIconImage(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        Image image = null;

        try {
            image = Image.getInstance(out.toByteArray());
            image.setAlignment(Image.ALIGN_BOTTOM);
            image.setScaleToFitLineWhenOverflow(true);
        } catch (IOException | BadElementException e) {
            e.printStackTrace();
        }

        return image;
    }


    private void addHeader(PdfWriter writer) {
        PdfPTable header = new PdfPTable(1);

        header.setTotalWidth(width - 50);
        header.setLockedWidth(true);
        header.getDefaultCell().setFixedHeight(80);

        header.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell text = new PdfPCell();
        text.setFixedHeight(40);
        text.setBorder(Rectangle.NO_BORDER);
        text.setBorderColor(BaseColor.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph(school_name);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        text.addElement(paragraph);
        header.addCell(text);

        Image logo = getIconImage(bitmapLogo);
        PdfPCell imagecell = new PdfPCell();
        paragraph = new Paragraph();

        imagecell.setFixedHeight(40);
        imagecell.setPaddingTop(-5);
        paragraph.add(new Chunk(logo, 0, 0));
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        imagecell.setBorder(Rectangle.NO_BORDER);
        imagecell.addElement(paragraph);


        header.addCell(imagecell);


        header.writeSelectedRows(0, -1, 20, height - 20, writer.getDirectContent());
    }


    private void addFooter(PdfWriter writer, String link) {

        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        PdfPTable footer = new PdfPTable(1);
        footer.setTotalWidth(width - 50);
        footer.setLockedWidth(true);
        footer.getDefaultCell().setFixedHeight(10);
        footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);


        PdfPCell text = new PdfPCell();
        text.setBorder(Rectangle.NO_BORDER);
        text.setBorderColor(BaseColor.LIGHT_GRAY);

        Paragraph paragraph = new Paragraph(link);
        paragraph.setAlignment(Element.ALIGN_RIGHT);

        text.addElement(paragraph);
        footer.addCell(text);


        text = new PdfPCell();
        text.setPaddingBottom(15);
        text.setBorder(Rectangle.NO_BORDER);
        text.setBorderColor(BaseColor.LIGHT_GRAY);
        text.setVerticalAlignment(Element.ALIGN_CENTER);

        paragraph = new Paragraph(c.getString(R.string.page) + c.getString(R.string.space) + page_number);
        paragraph.setFont(smallBold);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        text.addElement(paragraph);
        footer.addCell(text);


        PdfContentByte canvas = writer.getDirectContent();
        footer.writeSelectedRows(0, -1, 20, 80, canvas);
    }


    public void onCloseDocument(PdfWriter writer, Document document) {
        int totalLength = String.valueOf(writer.getPageNumber()).length();
        int totalWidth = totalLength * 5;
        ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
                new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)),
                totalWidth, 6, 0);
    }
}


