package com.mc2022.template;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{

    private static final String TAG = "NewsAdapter";
    private final ArrayList<News> newsList;

    public NewsAdapter(ArrayList<News> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_news, parent, false);
        NewsViewHolder holder = new NewsViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        News cur = newsList.get(position);

        holder.setContent(position, cur.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick" + position);
//                Toast.makeText(v.getContext(), "onClick" + position, Toast.LENGTH_SHORT).show();

//                Bundle bundle = new Bundle();
//                bundle.putString("title",cur.getTitle());
//                bundle.putString("body",cur.getBody());
//                bundle.putString("image",cur.getImageUrl());

                DetailsFragment detailsFragment = new DetailsFragment(cur, position); // new DetailsFragment();
//                detailsFragment.setArguments(bundle);

                FragmentManager fm = ((AppCompatActivity)v.getContext()).getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.mainFragmentContainer, detailsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void addToNewsList(News news)
    {
        newsList.add(news);
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNewsIdx;
        TextView textViewNewsTitle;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNewsIdx = itemView.findViewById(R.id.news_idx);
            textViewNewsTitle = itemView.findViewById(R.id.news_title);
        }

        public void setContent(int idx, String title)
        {
            textViewNewsIdx.setText(String.valueOf(idx));
            textViewNewsTitle.setText(title);
        }
    }
}
