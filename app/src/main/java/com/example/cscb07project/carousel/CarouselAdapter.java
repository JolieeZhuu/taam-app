package com.example.cscb07project.carousel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;

import com.example.cscb07project.R;
import com.example.cscb07project.entities.Artifact;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
    private final List<Artifact> carouselArtifacts;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Artifact artifact);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public CarouselAdapter(List<Artifact> carouselArtifacts) {
        this.carouselArtifacts = carouselArtifacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artifact carouselArtifact = carouselArtifacts.get(position);
        holder.textView.setText(carouselArtifact.getName());

        // clear previous image first to prevent RecyclerView from briefly showing wrong picture
        holder.image.setImageBitmap(null);
        if (holder.currentBitmap != null && !holder.currentBitmap.isRecycled()) {
            holder.currentBitmap.recycle();
            holder.currentBitmap = null;
        }

        if (carouselArtifact.getImage() != null && !carouselArtifact.getImage().isEmpty()) {
            String imageUrl = carouselArtifact.getImage();

            Executors.newSingleThreadExecutor().execute(() -> {
                Bitmap bitmap = null;
                try {
                    InputStream in = new URL(imageUrl).openStream();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    if (bitmap != null) {
                        Bitmap finalBitmap = bitmap;
                        holder.itemView.post(() -> {
                            if (holder.getAdapterPosition() == position) {
                                holder.image.setImageBitmap(finalBitmap);
                                holder.currentBitmap = finalBitmap;
                            } else {
                                finalBitmap.recycle();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (bitmap != null) bitmap.recycle();
                }
            });
        }

        // set click listener on every bind so it points at the current item
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(carouselArtifact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carouselArtifacts.size();
    }

    public void clearBitmaps() {
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView textView;
        Bitmap currentBitmap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.carouselItemImage);
            textView = itemView.findViewById(R.id.carouselItemText);
        }
    }
}
