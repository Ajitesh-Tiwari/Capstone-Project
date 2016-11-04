package webarch.com.hablar.ProfileFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import webarch.com.hablar.R;

/**
 * Created by ajitesh on 13/10/16.
 */

public class AvatarsAdapter extends RecyclerView.Adapter<AvatarsAdapter.ViewHolder> {
    Context context;
    OnAvatarSelected onAvatarSelected;

    public interface OnAvatarSelected{
        public void avatarSelected(View view,int position);
    }

    AvatarsAdapter(Context context){
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.avatar_image_item, parent, false);
        return new AvatarsAdapter.ViewHolder(view);
    }
    public void SetOnAvatarListener(OnAvatarSelected onAvatarSelected){
        this.onAvatarSelected=onAvatarSelected;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageDrawable(getDrawable("avatar_"+(position+1)));
    }
    public Drawable getDrawable(String name) {
        int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return ContextCompat.getDrawable(context,resourceId);
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivAvatar);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onAvatarSelected != null) {
                onAvatarSelected.avatarSelected(view, getAdapterPosition());
            }
        }
    }
}

