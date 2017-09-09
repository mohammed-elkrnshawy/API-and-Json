package com.example.a3zt.taskxdev;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by 3ZT on 8/9/2017.
 */

public class BookAdapter extends ArrayAdapter {

    private List<BookDetails> movieModelList;
    private int resource;
    private LayoutInflater inflater;
    public BookAdapter(Context context, int resource, List<BookDetails> objects) {
        super(context, resource, objects);
        movieModelList = objects;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(resource, null);

            holder.ivBookIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
            holder.tvBookTitle = (TextView)convertView.findViewById(R.id.tv_Book);
            holder.tvAuthor = (TextView)convertView.findViewById(R.id.tvAuthor);
            holder.Publisher = (TextView)convertView.findViewById(R.id.tv_Publisher);
            holder.PublisherDate = (TextView)convertView.findViewById(R.id.tv_PublishDate);
            holder.Price = (TextView)convertView.findViewById(R.id.tv_Price);
            holder.tv_Description = (ReadMoreTextView) convertView.findViewById(R.id.tv_Description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);


        final ViewHolder finalHolder = holder;
        ImageLoader.getInstance().displayImage(movieModelList.get(position).Image, holder.ivBookIcon, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    finalHolder.ivBookIcon.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
                finalHolder.ivBookIcon.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
                finalHolder.ivBookIcon.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
                finalHolder.ivBookIcon.setVisibility(View.INVISIBLE);
            }
        });

        holder.tvBookTitle.setText(movieModelList.get(position).Title);
        holder.Publisher.setText(movieModelList.get(position).Publisher);
        holder.PublisherDate.setText(movieModelList.get(position).PublisherDate);
        holder.tvAuthor.setText(movieModelList.get(position).Author);
        if(movieModelList.get(position).Price.contains("NOT"))
            holder.Price.setTextColor(Color.parseColor("#de1414"));
        else
            holder.Price.setTextColor(Color.parseColor("#44e444"));
        holder.Price.setText(movieModelList.get(position).Price);
        holder.tv_Description.setText(movieModelList.get(position).Description);
        return convertView;
    }


    class ViewHolder{
        private ImageView ivBookIcon;
        private TextView tvBookTitle;
        private TextView tvAuthor;
        private TextView Publisher;
        private TextView PublisherDate;
        private TextView Price;
        private ReadMoreTextView tv_Description;
    }

}