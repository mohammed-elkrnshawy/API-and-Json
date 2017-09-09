package com.example.a3zt.taskxdev;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 3ZT on 8/8/2017.
 */

public class BookDetails implements Serializable {
    String Title;
    String Publisher;
    String PublisherDate;
    String Image;
    String Price;
    String Description;
    String Category;
    String Author;
    String Info;

    public BookDetails(String title, String publisher, String publisherDate, String image, String price
            , String description,String author,String category,String info) {
        this.Title = title;
        this.Publisher = publisher;
        this.PublisherDate = publisherDate;
        this.Image = image;
        this.Price = price;
        this.Description = description;
        this.Author=author;
        this.Category=category;
        this.Info=info;
    }
}
