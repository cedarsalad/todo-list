package com.example.chads.to_dolist;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    DBManager db;
    CustomListAdapter adapter;
    ArrayList<Task> tasks;
    ArrayList<Task> sortedTasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Construct the data source
        db = new DBManager(this);
        ListView listView = (ListView) findViewById(R.id.listView);
        //populate list
        updateList();

        //create btn action listeners

        //new task popup
        Button btnNewTask = (Button) findViewById(R.id.btnNewTask);
        btnNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // Get the layout inflater
                final LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View containerView = inflater.inflate(R.layout.new_task_view, null);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                Button btnEnter = (Button) containerView.findViewById(R.id.btnAdd);
                Button btnCancel = (Button) containerView.findViewById(R.id.btnCancel);
                final EditText etxtTitle = (EditText) containerView.findViewById(R.id.etxtTitle);
                final EditText etxtDescription = (EditText) containerView.findViewById(R.id.etxtDescription);
                final RadioGroup rg = (RadioGroup) containerView.findViewById((R.id.radioGroup));

                builder.setView(containerView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                btnEnter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = ((EditText)containerView.findViewById(R.id.etxtTitle)).getText().toString();
                        String description = ((EditText)containerView.findViewById(R.id.etxtDescription)).getText().toString();
                        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                        int radioId = rg.getCheckedRadioButtonId();
                        View radioButton = rg.findViewById(radioId);
                        int radioIndex = rg.indexOfChild(radioButton);
                        int priority = radioIndex+1;
                        //go from low-med-high -> high-med-low
                        if (priority == 3)
                            priority = 1;
                        else if (priority == 1)
                            priority = 3;

                        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && rg.getCheckedRadioButtonId()!=-1){
                            Task task = new Task(title, description, date, priority);
                            db.addTask(task);
                            updateList();
                            Toast.makeText(getApplicationContext(),"Task added!", Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(),"Please fill out all fields.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });



            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task task = adapter.getItem(i);
                if (task.isCompleted())
                    db.setCompleted(task.getId(),false);
                else
                    db.setCompleted(task.getId(),true);
                updateList();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                AlertDialog.Builder deleteConfirmation = new AlertDialog.Builder(MainActivity.this);
                final Task taskToDelete = sortedTasks.get(i);
                deleteConfirmation.setTitle("Comfirm Delete");
                deleteConfirmation.setMessage("Delete task '" + taskToDelete.getTitle() + "'?");

                // Set YES button delete
                deleteConfirmation.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int x) {
                        db.deleteTask(taskToDelete);
                        adapter.remove(adapter.getItem(i));
                        adapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
                        updateList();
                        Toast.makeText(getApplicationContext(),"Task deleted!", Toast.LENGTH_LONG).show();
                    }
                });
                // Set Cancel Button
                deleteConfirmation.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                deleteConfirmation.show();

                return false;
            }
        });
    }

    public void updateList(){
        tasks = db.getAllTasks();
        //create array list helpers
        ArrayList<Task> completed = new ArrayList<Task>();
        ArrayList<Task> uncompleted = new ArrayList<Task>();
        sortedTasks = new ArrayList<Task>();

        //sort completed from uncompleted
        for (Task task : tasks) {
            if (task.isCompleted())
                completed.add(task);
            else
                uncompleted.add(task);
        }

        //sort both parts by priority
        Task[] helper = (Task[]) completed.toArray(new Task[completed.size()]);
        mergeSort(helper);
        completed = new ArrayList<>(Arrays.asList(helper));
//        Collections.reverse(completed);

        Task[] helper2 = (Task[]) uncompleted.toArray(new Task[uncompleted.size()]);
        mergeSort(helper2);
        uncompleted = new ArrayList<>(Arrays.asList(helper2));
//        Collections.reverse(completed);

        //add sorted parts to new sorted complete array
        for (Task task : completed)
            sortedTasks.add(task);
        for (Task task : uncompleted)
            sortedTasks.add(task);

        //Create Adapter to convert the array to views
        adapter = new CustomListAdapter(MainActivity.this, sortedTasks);


        //Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    public ArrayList<Task> sortTasks(ArrayList<Task> tasks){
        ArrayList<Task> sortedTasks = new ArrayList<>();
        ArrayList<Task> helper = new ArrayList<>();

        for (Task task : tasks){
            if (task.isCompleted())
                sortedTasks.add(task);
            else
                helper.add(task);
        }

        return sortedTasks;
    }

    public static void mergeSort(Task[] inputArray) {
        int size = inputArray.length;
        if (size < 2)
            return;
        int mid = size / 2;
        int leftSize = mid;
        int rightSize = size - mid;
        Task[] left = new Task[leftSize];
        Task[] right = new Task[rightSize];
        for (int i = 0; i < mid; i++) {
            left[i] = inputArray[i];

        }
        for (int i = mid; i < size; i++) {
            right[i - mid] = inputArray[i];
        }
        mergeSort(left);
        mergeSort(right);
        merge(left, right, inputArray);
    }

    public static void merge(Task[] left, Task[] right, Task[] arr) {
        int leftSize = left.length;
        int rightSize = right.length;
        int i = 0, j = 0, k = 0;
        while (i < leftSize && j < rightSize) {
            if (left[i].getPriority() <= right[j].getPriority()){
                arr[k] = left[i];
                i++;
                k++;
            } else {
                arr[k] = right[j];
                k++;
                j++;
            }
        }
        while (i < leftSize) {
            arr[k] = left[i];
            k++;
            i++;
        }
        while (j < rightSize) {
            arr[k] = right[j];
            k++;
            j++;
        }
    }

}
