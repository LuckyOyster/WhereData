package com.angela.wheredata.Others;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angela.wheredata.R;

import java.util.ArrayList;

public class AppNameListAdapter extends RecyclerView.Adapter<AppNameListAdapter.ListViewHolder> {

    private ArrayList<AppInfo> appList;

    public AppNameListAdapter(ArrayList<AppInfo> appList) {
        this.appList = appList;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_list,parent,false);

        ListViewHolder listViewHolder=new ListViewHolder(view);

        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppNameListAdapter.ListViewHolder holder, int position) {
        holder.appIconImage.setBackground(appList.get(position).getAppIcon());
        holder.appNameText.setText(appList.get(position).appName);
        holder.appPackageText.setText(appList.get(position).appPackageName);

    }

    @Override
    public int getItemCount() {
        return appList==null?0:appList.size();
    }

    class ListViewHolder extends RecyclerView.ViewHolder{

        ImageView appIconImage;
        TextView appNameText;
        TextView appPackageText;
        LinearLayout itemLayout;
        public ListViewHolder(final View itemView) {
            super(itemView);

            appIconImage=(ImageView)itemView.findViewById(R.id.app_icon);
            appNameText=(TextView)itemView.findViewById(R.id.app_name);
            appPackageText=(TextView) itemView.findViewById(R.id.app_package);
            itemLayout=(LinearLayout)itemView.findViewById(R.id.app_list_item);

             //TODO 为itemLayout添加点击事件

        }
    }
}
