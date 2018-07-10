package ralli.yugesh.com.primemovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.PosterViewHolder> {

    private String[] mPosterData;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler{
        void onClick(String selectedMovie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType Hjj
     * @return A new PosterViewHolder that holds the View for each list item
     */

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIdForListItem,viewGroup, false);

        return new PosterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        holder.bind(position);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our movies
     */

    @Override
    public int getItemCount() {
        if (null == mPosterData) return 0;
        return mPosterData.length;
    }

    /**
     * Cache of the children views for a list item.
     */

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

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String selectedMovie = mPosterData[adapterPosition];
            mClickHandler.onClick(selectedMovie);

        }
    }

    public void setPosterPath(String[] movieData){
        mPosterData = movieData;
        notifyDataSetChanged();
    }

}
