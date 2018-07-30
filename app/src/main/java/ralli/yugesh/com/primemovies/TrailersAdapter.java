package ralli.yugesh.com.primemovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private String[] mTrailerData;
    private TextView trailerView;

    private final TrailersAdapterOnClickHandler mClickHandler;

    public interface TrailersAdapterOnClickHandler{
        void onClick(String key);
    }

    public TrailersAdapter(TrailersAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutForListItem = R.layout.trailer_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutForListItem,viewGroup,false);

        trailerView = view.findViewById(R.id.tv_trailer);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mTrailerData ==  null ? 0 : mTrailerData.length;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TrailerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            trailerView.setText("Trailer " + String.valueOf(position));
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG,"clicked");
            String key = mTrailerData[getAdapterPosition()];
            mClickHandler.onClick(key);
        }
    }

    public void setTrailerData(String[] trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }
}
