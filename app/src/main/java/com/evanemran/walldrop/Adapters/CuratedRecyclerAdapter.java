package com.evanemran.walldrop.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.evanemran.walldrop.Listeners.CuratedClickListener;
import com.evanemran.walldrop.Models.Photo;
import com.evanemran.walldrop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CuratedRecyclerAdapter extends RecyclerView.Adapter<CuratedViewHolder> {

    Context context;
    List<Photo> list;
    CuratedClickListener listener;

    public CuratedRecyclerAdapter(Context context, List<Photo> list, CuratedClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CuratedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CuratedViewHolder(LayoutInflater.from(context).inflate(R.layout.curated_list, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull CuratedViewHolder holder, int position) {
        holder.curated_container.setBackgroundColor(Color.parseColor(list.get(position).avg_color));
        Picasso.get().load(list.get(position).getSrc().getMedium())/*.placeholder(R.drawable.placeholder)*/.into(holder.imageView_curated);
        holder.textView_curated_name.setText(list.get(position).getPhotographer());
        holder.textView_curated_name.setSelected(true);

        holder.curated_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class CuratedViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView_curated;
    TextView textView_curated_name;
    CardView curated_container;
    public CuratedViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView_curated = itemView.findViewById(R.id.imageView_curated);
        textView_curated_name = itemView.findViewById(R.id.textView_curated_name);
        curated_container = itemView.findViewById(R.id.curated_container);
    }
}
