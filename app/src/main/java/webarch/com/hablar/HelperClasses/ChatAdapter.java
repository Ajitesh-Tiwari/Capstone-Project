package webarch.com.hablar.HelperClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import webarch.com.hablar.R;

/**
 * Created by ajitesh on 2/11/16.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    Context context;
    List<Chat> chats;
    FirebaseUser firebaseUser;

    public ChatAdapter(Context context,List<Chat> chats){
        this.context=context;
        this.chats=chats;
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.message.setText(chats.get(position).getMessage());
        setAlignment(holder,!chats.get(position).getName().equals(firebaseUser.getDisplayName()));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView message;
        LinearLayout content;
        LinearLayout contentWithBG;
        public ViewHolder(final View itemView) {
            super(itemView);
            message=(TextView)itemView.findViewById(R.id.msg);
            content=(LinearLayout)itemView.findViewById(R.id.content);
            contentWithBG=(LinearLayout)itemView.findViewById(R.id.contentWithBackground);
        }
    }
    private void setAlignment(ViewHolder holder, boolean isMe) {
        if (!isMe) {
            holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.END;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.message.getLayoutParams();
            layoutParams.gravity = Gravity.END;
            holder.message.setLayoutParams(layoutParams);

            layoutParams.gravity = Gravity.END;
        } else {
            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.START;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.message.getLayoutParams();
            layoutParams.gravity = Gravity.START;
            holder.message.setLayoutParams(layoutParams);

            layoutParams.gravity = Gravity.START;
        }
    }
}
