package com.bsyun.xuanxueapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PanAdapter extends RecyclerView.Adapter<PanAdapter.ViewHolder> {

    private List<PanEntity> mData = new ArrayList<>();

    public void addData(List<PanEntity> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PanEntity entity = mData.get(position);
        holder.tvDipangan.setText(entity.getDipangan());
        holder.tvTianpangan.setText(entity.getTianpangan());

        // 九星显示
        String jiuxing = entity.getJiuxing2();
        if (entity.getJiuxing1() != null) {
            holder.tvJiuxing1.setVisibility(View.VISIBLE);
            holder.tvJiuxing1.setText(entity.getJiuxing1());
        } else {
            holder.tvJiuxing1.setVisibility(View.GONE);
        }
        holder.tvJiuxing2.setText(jiuxing);

        // 特殊天干
        if (entity.getTiangan1() != null && !entity.getTiangan1().isEmpty()) {
            holder.tvTiangan1.setVisibility(View.VISIBLE);
            holder.tvTiangan1.setText(entity.getTiangan1());
        } else {
            holder.tvTiangan1.setVisibility(View.GONE);
        }

        holder.tvBamen.setText(entity.getBamen());
        holder.tvBashen.setText(entity.getBashen());

        // 生克显示
        String menke = entity.getMenke();
        String xingke = entity.getXingke();
        if (menke != null && !menke.isEmpty()) {
            holder.tvMenke.setVisibility(View.VISIBLE);
            holder.tvMenke.setText("门:" + menke);
        } else {
            holder.tvMenke.setVisibility(View.GONE);
        }
        if (xingke != null && !xingke.isEmpty()) {
            holder.tvXingke.setVisibility(View.VISIBLE);
            holder.tvXingke.setText("星:" + xingke);
        } else {
            holder.tvXingke.setVisibility(View.GONE);
        }

        // 长生十二宫
        String changsheng = entity.getChangsheng();
        if (changsheng != null && !changsheng.isEmpty()) {
            holder.tvChangsheng.setVisibility(View.VISIBLE);
            holder.tvChangsheng.setText(changsheng);
        } else {
            holder.tvChangsheng.setVisibility(View.GONE);
        }

        // 马星空亡图标（用文字替代，因为没有实际图片资源）
        if (entity.getMakong() != 0) {
            holder.tvMakong.setVisibility(View.VISIBLE);
            holder.tvMakong.setText("马");
            holder.tvMakong.setTextColor(Color.RED);
        } else {
            holder.tvMakong.setVisibility(View.GONE);
        }

        // 宫位背景色
        int[] bgColors = {
                0x33FF5722, // 巽4
                0x33FF0000, // 离9
                0x33FF9800, // 坤2
                0x334CAF50, // 震3
                0x33F44336, // 中5
                0x33FFFFFF, // 兑7
                0x33FF9800, // 艮8
                0x330000FF, // 坎1
                0x33FFFFFF  // 乾6
        };
        if (position < bgColors.length) {
            holder.itemView.setBackgroundColor(bgColors[position]);
        }

        // 中宫特殊处理
        if (position == 4) {
            holder.itemView.setBackgroundColor(0x33FFEB3B);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDipangan, tvTianpangan;
        TextView tvJiuxing1, tvJiuxing2;
        TextView tvTiangan1;
        TextView tvBamen, tvBashen;
        TextView tvMenke, tvXingke;
        TextView tvChangsheng, tvMakong;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDipangan = itemView.findViewById(R.id.tv_dipangan);
            tvTianpangan = itemView.findViewById(R.id.tv_tianpangan);
            tvJiuxing1 = itemView.findViewById(R.id.tv_jiuxing1);
            tvJiuxing2 = itemView.findViewById(R.id.tv_jiuxing2);
            tvTiangan1 = itemView.findViewById(R.id.tv_tiangan1);
            tvBamen = itemView.findViewById(R.id.tv_bamen);
            tvBashen = itemView.findViewById(R.id.tv_bashen);
            tvMenke = itemView.findViewById(R.id.tv_menke);
            tvXingke = itemView.findViewById(R.id.tv_xingke);
            tvChangsheng = itemView.findViewById(R.id.tv_changsheng);
            tvMakong = itemView.findViewById(R.id.tv_makong);
        }
    }
}
