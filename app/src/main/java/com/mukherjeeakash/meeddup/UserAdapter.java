package com.mukherjeeakash.meeddup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by akash on 11/23/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private static final String TAG = UserAdapter.class.getSimpleName();
    private int mNumberItems;
    private String[] users;

    public UserAdapter(String[] userNames) {
        mNumberItems = userNames.length;
        users = userNames;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.user_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        UserViewHolder viewHolder = new UserViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(users[position]);

        int INDEX = position;
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        View itemView;

        /**
         * Constructor for the ViewHolder that gets the view in which to display User data
         *
         * @param itemView The View that was inflated previously
         */
        public UserViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            userNameTextView = (TextView) itemView.findViewById(R.id.userName);
        }

        /**
         * Takes a username to bind to xml object
         */
        void bind(String userName) {
            userNameTextView.setText(userName);
        }
    }
}
