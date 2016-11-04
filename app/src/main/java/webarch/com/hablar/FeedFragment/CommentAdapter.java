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

import webarch.com.hablar.HelperClasses.Comment;
import webarch.com.hablar.R;

/**
 * Created by ajitesh on 2/11/16.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    List<Comment> feedList;

    CommentAdapter(Context context, List<Comment> feeds){
        this.context=context;
        this.feedList=feeds;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(feedList.get(position).getName());
        holder.feed.setText(feedList.get(position).getComment());
        if(feedList.get(position).getAvatar()!=0)
            holder.profile.setImageDrawable(getDrawable("avatar_"+feedList.get(position).getAvatar()));
        else
            holder.profile.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.hablar_logo));
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, feed;
        public ImageView profile;

        public ViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            feed = (TextView) itemView.findViewById(R.id.feed);
            profile = (ImageView) itemView.findViewById(R.id.ivProfile);
        }
    }
    public Drawable getDrawable(String name) {
        int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return ContextCompat.getDrawable(context,resourceId);
    }
}
