package com.example.noname.notify20;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by Noname on 16-03-13.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder>
{
    private LayoutInflater inflater;
    private Context context;
    private List<Note> data = Collections.emptyList();
    private int cat_id;

    public NoteAdapter(Context context, List<Note> data,int cat_id) {
        this.context = context;
        this.data = data;
        this.cat_id = cat_id;
       // Log.i("Nav", "Constructor");
       // Log.i("Nav","getItemCount Constructor : "+getItemCount());
        update();
    }

    @Override
    public NoteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //Log.i("Nav", "onCreateViewHolder");
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(NoteAdapter.MyViewHolder holder, int position)
    {
        Note d = data.get(position);
//        Log.i("Any","Data : "+d.nid);
        holder.ntitle.setText(d.ntitle);
        //Log.i("Nav", "Holder Data : "+holder.ntitle.getText());
        holder.ncontent.setText(d.ncontent);
        holder.ndate.setText(d.ndate);
    }

    public void update()
    {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        //Log.i("Nav","getItemCount : "+data.size());
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView ntitle;
        TextView ncontent;
        TextView ndate;

        private final Context context;
//        ListView list_notes;

        public MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            gatherControls();
            itemView.setOnClickListener(this);
        }

        public void gatherControls()
        {
            ntitle = (TextView)itemView.findViewById(R.id.title);
            ncontent = (TextView)itemView.findViewById(R.id.content);
            ndate = (TextView)itemView.findViewById(R.id.date);
        }

        @Override
        public void onClick(View v)
        {

            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(100);
//            anim.setBackgroundColor(Color.RED);
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.ABSOLUTE);
            v.startAnimation(anim);

//            Log.i("Any","Clicked on : "+data.get(getPosition()).getNid());
            Intent i = new Intent(context, NewNoteActivity.class);

            int tempNId = data.get(getPosition()).getNid();
            int tempCid = data.get(getPosition()).getCid();
            if(tempNId != 0 &&  tempCid != 0)
            {
                Log.i("Nid","Nid : "+tempNId);
                i.putExtra("Status", "edit");
                i.putExtra("nid", tempNId);
                i.putExtra("cid",tempCid);
                Log.i("CatId","In Note Adapter if : "+tempCid);
            }
            else
            {
                Log.i("CatId","In Note Adapter else : "+tempCid);
     //           i.putExtra("Status","new");
            }
//            i.putExtra("cid",cat_id);
//            Log.i("CatId","Note Adapter Cid : "+cat_id);
            context.startActivity(i);
        }
    }
}
