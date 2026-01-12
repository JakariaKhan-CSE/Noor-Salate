package com.noor.prayer.ui.duas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.noor.prayer.R;
import com.noor.prayer.model.Dua;
import java.util.List;

public class DuasAdapter extends RecyclerView.Adapter<DuasAdapter.DuaViewHolder> {

    private List<Dua> duas;

    public DuasAdapter(List<Dua> duas) {
        this.duas = duas;
    }

    @NonNull
    @Override
    public DuaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dua, parent, false);
        return new DuaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DuaViewHolder holder, int position) {
        Dua dua = duas.get(position);
        holder.title.setText(dua.title);
        holder.arabic.setText(dua.arabic);
        holder.translation.setText(dua.translation);
        holder.reference.setText(dua.reference);

        boolean isExpanded = dua.expanded;
        holder.content.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            dua.expanded = !dua.expanded;
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return duas.size();
    }

    static class DuaViewHolder extends RecyclerView.ViewHolder {
        TextView title, arabic, translation, reference;
        LinearLayout content;

        public DuaViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.dua_title);
            arabic = itemView.findViewById(R.id.dua_arabic);
            translation = itemView.findViewById(R.id.dua_translation);
            reference = itemView.findViewById(R.id.dua_reference);
            content = itemView.findViewById(R.id.dua_content);
        }
    }
}
