package com.example.noteapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Note> notes;
    private List<Note> notes1;

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        this.notes1 = notes;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, timeCreated;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            timeCreated = itemView.findViewById(R.id.timeCreated);
        }
    }
    @Override
    public  Filter getFilter() {
    return  new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String strSearch = charSequence.toString();
            if (strSearch.isEmpty()) {
                notes = notes1;
            } else {
                List<Note> list= new ArrayList<>();
                for(Note note:notes1){
                    if(note.getTitle().toLowerCase().contains(strSearch.toLowerCase())){
                        list.add(note);
                    }
                }
                notes=  list;

            }
            FilterResults filterResults = new FilterResults();
            filterResults.values=notes;
            return  filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes=(List<Note>)  results.values;
            notifyDataSetChanged();
        }
    };
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formatedTime = sdf.format(note.getTimeCreated());
        holder.timeCreated.setText(formatedTime);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, EditNoteActivity.class);

                    //Vì khai báo intent không trong activity nên phải thêm hàng này
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    long timeCreated = note.getTimeCreated();

                    //Lưu dữ liệu vào bundle
                    Bundle myBundle = new Bundle();
                    myBundle.putLong("timeCreated", timeCreated);
                    intent.putExtra("myBundle", myBundle);

                    context.startActivity(intent);
                } catch(Exception e) {
                    Log.d("MyTag", "Lỗi khi nhấn nút ADD NEW NOTE: " + e.toString());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return notes.size();
    }
}
