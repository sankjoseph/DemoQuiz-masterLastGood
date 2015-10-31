package com.qppacket.ekalavya4G.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import com.qppacket.ekalavya4G.R;

/**
 * Created by gorillalogic on 6/26/15.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    OnItemClickListener mItemClickListener;
    List<String> mItems;
    private List<Integer> mIcons;
    String mName, mEmail;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int type;
        TextView item;
        TextView name, email;
        ImageView pic, icon;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType == TYPE_ITEM) {
                item = (TextView) itemView.findViewById(R.id.item_text);
                icon = (ImageView) itemView.findViewById(R.id.item_icon);
                itemView.setOnClickListener(this);
            } else {
                name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                pic = (ImageView) itemView.findViewById(R.id.profile_pic);
            }
            type = viewType;
        }

        @Override
        public void onClick(View v) {
            int pos = getPosition();
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, pos);
        }
    }

    public NavigationDrawerAdapter(List<String> items
            , String name
            , String email) {
        mItems = items;
        mName = name;
        mEmail = email;
        mIcons = Arrays.asList(R.drawable.ic_menu_home, R.drawable.ic_menu_faq, R.drawable.ic_menu_price, R.drawable.ic_menu_news, R.drawable.ic_menu_contact);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType == TYPE_ITEM
                        ? R.layout.adapter_item_navigation_drawer
                        : R.layout.adapter_item_navigation_drawer_header
                        , parent
                        , false);
        ViewHolder holder = new ViewHolder(v, viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.type == TYPE_ITEM) {
            int pos = position - 1;
            holder.item.setText(mItems.get(pos));
            holder.icon.setImageResource(mIcons.get(pos));

        } else {
            // Main Header
            holder.name.setText(mName.isEmpty() ? "Username" : mName);
            holder.email.setText(mEmail.isEmpty() ? "Email" : mEmail);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }
}
