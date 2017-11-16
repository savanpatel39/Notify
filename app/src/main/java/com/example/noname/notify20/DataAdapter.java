package com.example.noname.notify20;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

/**
 * Created by Savan on 16-03-10.
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder>
{
    private LayoutInflater inflater;
    private Context contextt;
    private List<Category> data = Collections.emptyList();
    private DBHandler db;
//    private ArrayAdapter<String> notes;

    DataAdapter(Context context, List<Category> data)
    {
        this.contextt = context;
        this.data = data;
//        this.db = new DBHandler(this,null,null,1);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    public void update(String category)
    {
        Category temp = new Category();
        temp.setCatname(category);
        data.add(data.size() - 1 ,temp);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        DataInformation current = data.get(position);
        Category d = data.get(position);
        List<Note> notes = new DBHandler(contextt,null,null,1).getNotes(data.get(position).getCid());
        holder.catname.setText(d.catname);
//        if(!(d.catname.equals("Add Category"))) {
//            holder.noteCount.setText("" + notes.size());
//        }
//        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.cell_layout, holder.notes[position][]);
       // holder.title.setText(d.title);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView catname;
//        TextView noteCount;
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
            catname = (TextView)itemView.findViewById(R.id.title);
//            noteCount = (TextView)itemView.findViewById(R.id.noteCount);
//            list_notes = (ListView)itemView.findViewById(R.id.list_notes);
        }

        @Override
        public void onClick(View v)
        {

            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(50);
            anim.setStartOffset(0);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.ABSOLUTE);
            v.startAnimation(anim);
//            Log.i("Any", "IN on click");
//            Toast.makeText(v.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
//            Log.i("Any", "Title Name : " + data.get(getPosition()).catname);
            if( data.get(getPosition()).catname.equals("+") )
            {
                showAlert();
            }
            else
            {
                    Intent i = new Intent(contextt, NoteActivity.class);
//                Intent i = new Intent(contextt, Temp.class);

                    List<Note> notes = new DBHandler(contextt,null,null,1).getNotes(data.get(getPosition()).getCid());
                    int tempCId = 0;

                    if(!notes.isEmpty())
                    {
//                        Log.i("Int","ID in Data Adapter : "+tempId);
//                        notes.
//                        tempId = data.get(getPosition()).getCid();
                        Toast.makeText(v.getContext(), "" + notes.size() + " notes found!!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //tempId = new DBHandler(contextt,null,null,1).getCategoryCount() + 1;
                        Toast.makeText(v.getContext(), "No notes found!!", Toast.LENGTH_SHORT).show();
                    }

                    //tempCId = data.get(getPosition()).getCid();
                    tempCId = new DBHandler(contextt,null,null,1).findCategoryIdByCatName(data.get(getPosition()).getCatname());
                    Log.i("CatId", "Data Adapter ID in Data Adapter : " + tempCId);
                    i.putExtra("notescount", notes.size());
                    i.putExtra("id",tempCId);
                    i.putExtra("checking", "checking");
                    contextt.startActivity(i);
            }
//            Toast.makeText(v.getContext(), "Clicked Position = " + data.get(getPosition()).catname, Toast.LENGTH_SHORT).show();
        }


        public void showAlert()
        {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.create_catrgory_layout, null);

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.catName);
            final TextView msg = (TextView) promptsView.findViewById(R.id.msg);


            // set dialog message
            alertDialogBuilder.setCancelable(true).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String category = userInput.getText().toString().trim();
                            if (!category.isEmpty()) {
                                //  check = 1;
                                //Log.i("Any","Some Text");
                                msg.setVisibility(View.INVISIBLE);
                            } else if (category.isEmpty()) {
                                //check = 0;
                                //Log.i("Any","Empty Text");
                                msg.setVisibility(View.VISIBLE);

                            }
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    String category = userInput.getText().toString().trim();
                    if (!category.isEmpty()) {
                        //  check = 1;
//                        Log.i("Any", "Some Text");
                        msg.setVisibility(View.INVISIBLE);
                        DBHandler db = new DBHandler(context, null, null, 1);
                        db.insertCategory(category);
                        wantToCloseDialog = true;
                        update(category);
                        //   notifyDataSetChanged();
                    } else if (category.isEmpty()) {
                        //check = 0;
//                        Log.i("Any", "Empty Text");
                        msg.setVisibility(View.VISIBLE);
                    }
                    //Do stuff, possibly set wantToCloseDialog to true then...
                    if (wantToCloseDialog)
                        alertDialog.dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }
}