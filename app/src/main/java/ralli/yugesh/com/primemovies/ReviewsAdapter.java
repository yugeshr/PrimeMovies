package ralli.yugesh.com.primemovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReviewsAdapter extends Adapter<ReviewsAdapter.ReviewViewHolder>  {

    private String[] mAuthorData;
    private String[] mReviewData;
    private TextView authorView;
    private TextView reviewView;

    public ReviewsAdapter() {

    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutForListItem = R.layout.review_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutForListItem,viewGroup,false);

        authorView = view.findViewById(R.id.tv_author);
        reviewView = view.findViewById(R.id.tv_review);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mReviewData ==  null ? 0 : mReviewData.length;
    }



    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        ReviewViewHolder(View itemView) {
            super(itemView);
        }

        void bind(int position) {
            authorView.setText("By "+ mAuthorData[position]);
            reviewView.setText(mReviewData[position]);
        }
    }

    public void setReviewData(String[] reviewData) {
       mReviewData = reviewData;
       notifyDataSetChanged();
    }

    public void setAuthorData(String[] author) {
        mAuthorData = author;
        notifyDataSetChanged();
    }
}
