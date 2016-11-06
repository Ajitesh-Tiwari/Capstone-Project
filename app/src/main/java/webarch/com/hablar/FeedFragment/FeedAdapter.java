package webarch.com.hablar.FeedFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import webarch.com.hablar.HelperClasses.Feed;
import webarch.com.hablar.R;

/**
 * Created by ajitesh on 27/9/16.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    Context context;
    List<Feed> feedList;

    OnFeedItemSelected onFeedItemSelected;

    public interface OnFeedItemSelected{
        public void feedSelected(View view,int position);
    }

    public FeedAdapter(Context context, List<Feed> feeds){
        this.context=context;
        this.feedList=feeds;
    }

    public void SetOnFeedListener(OnFeedItemSelected onFeedItemSelected){
        this.onFeedItemSelected=onFeedItemSelected;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(feedList.get(position).getUser().getUsername());
        holder.feed.setText(feedList.get(position).getComment());
        if(feedList.get(position).getUser().getAvatar()!=0)
            holder.profile.setImageDrawable(getDrawable("avatar_"+feedList.get(position).getUser().getAvatar()));
        else
            holder.profile.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.hablar_logo));
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name, feed;
        public ImageView profile;

        public ViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            feed = (TextView) itemView.findViewById(R.id.feed);
            profile = (ImageView) itemView.findViewById(R.id.ivProfile);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(onFeedItemSelected!=null)
                onFeedItemSelected.feedSelected(view,getAdapterPosition());
        }
    }
    public Drawable getDrawable(String name) {
        int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return ContextCompat.getDrawable(context,resourceId);
    }
}
