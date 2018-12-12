package my.own.FakeTinderApp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class StartSubject extends AppCompatActivity {

    private EditText subNum;
    private Button btnSubmitMale, btnSubmitFemale, createFile, clearBtn;
    private TextView prevSubject;
    private static ArrayList<String> subjects = new ArrayList<>();
    private static SharedPreferences prefs;
    private String m_Text;
    private int prevSub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_subject);

        prefs = getSharedPreferences("myprefs", 0);
        if (subjects.isEmpty()) {
            subjects.addAll(Objects.requireNonNull(prefs.getStringSet("subjects", new HashSet<String>())));
        }

        String prevMessage;
        if (subjects.isEmpty()){
            prevSub = 0;
        } else {
            prevSub = prefs.getInt("prevSubject", 0);
        }





        subNum = findViewById(R.id.subNum);
        btnSubmitMale = findViewById(R.id.btnSubmitM);
        btnSubmitFemale = findViewById(R.id.btnSubmitF);
        createFile = findViewById(R.id.btnFile);
        clearBtn = findViewById(R.id.clearBtn);
        prevSubject = findViewById(R.id.prevSubject);

        String newSubject = getIntent().getStringExtra("history");
        int newPrevId = getIntent().getIntExtra("prevSubject", 0);
        if (newSubject != null){
            subjects.add(newSubject);
            prevSub = newPrevId;
            savePreferences();
        }

        if (prevSub == 0) {
            prevMessage = getString(R.string.first_subject);
        } else{
            prevMessage = "You previously completed Subject: " + Integer.toString(prevSub);
        }

        prevSubject.setText(prevMessage);




        btnSubmitMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick("male");
            }

        });

        btnSubmitFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick("female");
            }
        });



        createFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjects.isEmpty()){
                    Toast.makeText(StartSubject.this, "There are no subjects", Toast.LENGTH_SHORT).show();
                }else{
                    if (isStoragePermissionGranted()) {
                        createFileAction();
                    }
                }
            }

        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjects.isEmpty())
                    Toast.makeText(StartSubject.this, "There are already no subjects", Toast.LENGTH_SHORT).show();
                else{
                    new AlertDialog.Builder(StartSubject.this)
                            .setTitle("Clear Confirmation")
                            .setMessage("Do you really want to clear history?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    subjects.clear();
                                    prevSub = 0;
                                    prevSubject.setText(getString(R.string.first_subject));
                                    savePreferences();
                                    Toast.makeText(StartSubject.this, "History cleared", Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }
    private void createFileAction(){
        AlertDialog.Builder builder = new AlertDialog.Builder(StartSubject.this);
        builder.setTitle("Enter a new file name");

        final EditText input = new EditText(StartSubject.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Should end in .csv or .txt");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().isEmpty()) {
                    Toast.makeText(StartSubject.this, "Please enter a file name", Toast.LENGTH_SHORT).show();
                } else {
                    m_Text = input.getText().toString();
                    writeToFile(subjects, m_Text);
                }
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

    @Override
    protected void onStop(){
        savePreferences();
        super.onStop();

    }

    private void onSubmitClick(String gender){
        if (subNum.getText().toString().isEmpty()){
            Toast.makeText(StartSubject.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        }
        else if (subNum.getText().toString() == "0") {
            Toast.makeText(StartSubject.this, "There can be no subject 0", Toast.LENGTH_SHORT).show();
        }
        else {
            int subjectNum = Integer.parseInt(subNum.getText().toString());
            System.out.println(subjectNum);

            Intent intent = new Intent(StartSubject.this,
                    MainActivity.class);

            intent.putExtra("subjectNum",subjectNum);
            intent.putExtra("gender",gender);
            prevSub = subjectNum;
            startActivity(intent);
        }
    }

    private void savePreferences(){
        Set<String> set = new HashSet<>(subjects);
        prefs = getSharedPreferences("myprefs",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putStringSet("subjects", set);
        editor.putInt("prevSubject", prevSub);
        editor.apply();
    }

    private void writeToFile(ArrayList<String> data,String filename ) {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!filename.contains(".csv") && !filename.contains(".txt")) {
            filename = filename + ".csv";
        }
        File file = new File(dir, filename);

        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.append(getHeader());
            for (String data_l : data) {
                fileWriter.append(data_l + System.getProperty("line.separator"));
            }
            fileWriter.flush();
            MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);
            Toast.makeText(StartSubject.this, "Data written", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            //Handle exception
        }

    }

    private String getHeader(){
        String swipeNums;
        String lengthNums;
        StringBuilder swipeNumsBuilder = new StringBuilder();
        for (int i = 1; i<41; i++){
            swipeNumsBuilder.append(i).append("_swipe, ");
        }
        swipeNums = swipeNumsBuilder.toString();
        StringBuilder lengthNumsBuilder = new StringBuilder();
        for (int i = 1; i<40; i++){
            lengthNumsBuilder.append(i).append("_length, ");
        }
        lengthNums = lengthNumsBuilder.toString();
        lengthNums = lengthNums+"40_length"+System.getProperty("line.separator");
        return "Participant, Total_left_swipes, Total_right_swipes, "+swipeNums+lengthNums;


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createFileAction();
        } else {
            Toast.makeText(StartSubject.this,"Cannot write unless permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
