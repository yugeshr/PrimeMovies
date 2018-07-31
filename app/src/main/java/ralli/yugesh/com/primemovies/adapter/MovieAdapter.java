package ralli.yugesh.com.primemovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ralli.yugesh.com.primemovies.R;
import ralli.yugesh.com.primemovies.data.FavoritelistContract;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.PosterViewHolder> {

    private String[] mPosterData;
    private Cursor mCursor;
    private int flag = 0;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler{
        void onClick(String selectedMovie);
        void onClickFavorite(int id);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public  MovieAdapter(MovieAdapterOnClickHandler clickHandler, Cursor cursor) {
        mClickHandler = clickHandler;
        mCursor = cursor;
        flag = 1;
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIdForListItem,viewGroup, false);

        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        if (flag == 1) {
            if (!mCursor.moveToPosition(position))
                return;
            holder.bindFavorite();
        }
        else {
            holder.bind(position);
        }

    }

    @Override
    public int getItemCount() {
        if (flag == 1) {
            return mCursor.getCount();
        }
        else {
            if (null == mPosterData) return 0;
            return mPosterData.length;
        }
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //TextView mPosterDataTextView;
        final ImageView mImageView;

        PosterViewHolder(View itemView) {
            super(itemView);

            //mPosterDataTextView = itemView.findViewById(R.id.tv_poster_data);
            mImageView = itemView.findViewById(R.id.iv_thumbnail_data);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            String[] posterLink = mPosterData[position].split("-",2);
            String posterPath = "http://image.tmdb.org/t/p/w342/" + posterLink[0];
            Picasso.get().load(posterPath).into(mImageView);
            //mPosterDataTextView.setText(posterPath);
        }

        void bindFavorite() {
            String posterLink = mCursor.getString(mCursor
                    .getColumnIndex(FavoritelistContract.FavortitelistEntry.COLUMN_POSTER_PATH));
            String posterPath = "http://image.tmdb.org/t/p/w342/" + posterLink;
            Picasso.get().load(posterPath).into(mImageView);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            if (flag == 1) {
                mCursor.moveToPosition(adapterPosition);
                int id = mCursor.getInt(mCursor.getColumnIndex(FavoritelistContract.FavortitelistEntry.COLUMN_ID));
                mClickHandler.onClickFavorite(id);
            }
            else {
                String selectedMovie = mPosterData[adapterPosition];
                mClickHandler.onClick(selectedMovie);
            }
        }
    }

    public void setPosterPath(String[] movieData){
        mPosterData = movieData;
        notifyDataSetChanged();
    }

}
