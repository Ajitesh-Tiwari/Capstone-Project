package webarch.com.hablar.ContactsFragment;

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
import webarch.com.hablar.HelperClasses.User;
import webarch.com.hablar.R;

/**
 * Created by ajitesh on 16/10/16.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    Context context;
    List<User> users;

    public ContactsAdapter(Context context,List<User> users){
        this.context=context;
        this.users=users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(users.get(position).getUsername());
        holder.email.setText(users.get(position).getEmail());
        holder.roll.setText(users.get(position).getRegisterID());
        if(users.get(position).getAvatar()!=0)
            holder.profile.setImageDrawable(getDrawable("avatar_"+users.get(position).getAvatar()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, email, roll;
        public ImageView profile;

        public ViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            roll = (TextView) itemView.findViewById(R.id.rollNo);
            profile = (ImageView) itemView.findViewById(R.id.ivProfile);
        }
    }
    public Drawable getDrawable(String name) {
        int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return ContextCompat.getDrawable(context,resourceId);
    }
}
