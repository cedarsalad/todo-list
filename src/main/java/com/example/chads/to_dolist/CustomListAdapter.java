package com.example.chads.to_dolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chads on 2018-03-01.
 */

public class CustomListAdapter extends ArrayAdapter<Task> {

    public CustomListAdapter(MainActivity context, ArrayList<Task> tasks){
        super(context,0,tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_view, parent, false);
        }
        // Lookup view for data population
        ImageView imgPriority = (ImageView) convertView.findViewById(R.id.imageView);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
        TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
        CheckBox checkBox = (CheckBox ) convertView.findViewById(R.id.checkBox);

        // Populate the data into the template view using the data object
        switch (task.getPriority()){
            case 1:
                imgPriority.setImageResource(R.drawable.square_red);
                break;
            case 2:
                imgPriority.setImageResource(R.drawable.square_yellow);
                break;
            case 3:
                imgPriority.setImageResource(R.drawable.square_green);
                break;
        }
        txtTitle.setText(task.getTitle());
        txtDescription.setText(task.getDescription());
        txtDate.setText(task.getDate());

        if (task.isCompleted())
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);

        // Return the completed view to render on screen
        return convertView;
    }


}
