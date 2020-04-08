package com.fyp.kyd;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    public static boolean bind = false;
    private List<MyListData> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre;
        public ImageView year;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (ImageView) view.findViewById(R.id.year);
        }
    }

    public static void setBind(boolean bind) {
        MyRecyclerViewAdapter.bind = bind;
    }

    public static boolean isBind() {
        return bind;
    }

    public MyRecyclerViewAdapter(List<MyListData> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyListData movie = moviesList.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());

        if(bind) {
            holder.year.setBackgroundResource(R.drawable.sw);
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
