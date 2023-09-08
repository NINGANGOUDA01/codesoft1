package com.example.todo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> taskList;
    private ArrayAdapter<String> taskAdapter;
    private EditText taskEditText;
    private ListView taskListView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = new ArrayList<>();
        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        taskEditText = findViewById(R.id.taskEditText);
        taskListView = findViewById(R.id.taskListView);
        taskListView.setAdapter(taskAdapter);


        sharedPreferences = getSharedPreferences("MyTasks", MODE_PRIVATE);
        Set<String> savedTasks = sharedPreferences.getStringSet("taskList", new HashSet<String>());
        taskList.addAll(savedTasks);
        taskAdapter.notifyDataSetChanged();

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = taskEditText.getText().toString();
                if (!task.isEmpty()) {
                    taskList.add(task);
                    taskAdapter.notifyDataSetChanged();
                    taskEditText.setText("");
                    saveTaskList();
                }
            }
        });


        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                showEditDialog(position);
            }
        });


        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                showRemoveDialog(position);
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTaskList();
    }

    private void saveTaskList() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> taskSet = new HashSet<>(taskList);
        editor.putStringSet("taskList", taskSet);
        editor.apply();
    }

    private void showEditDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        final EditText editTaskEditText = new EditText(this);
        editTaskEditText.setText(taskList.get(position));
        builder.setView(editTaskEditText);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String editedTask = editTaskEditText.getText().toString();
                taskList.set(position, editedTask);
                taskAdapter.notifyDataSetChanged();
                saveTaskList();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showRemoveDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Task");
        builder.setMessage("Do you want to remove this task?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskList.remove(position);
                taskAdapter.notifyDataSetChanged();
                saveTaskList();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}
