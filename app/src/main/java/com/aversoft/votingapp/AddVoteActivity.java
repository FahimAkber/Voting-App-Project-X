package com.aversoft.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aversoft.votingapp.Model.Vote;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddVoteActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvDate, tvStart, tvEnd;
    EditText etName, etOne, etTwo, etThree, etFour, etFive;
    Button btnAdd;
    FirebaseDatabase firebase;
    DatabaseReference reference;
    Calendar calendar;
    long date;
    int dateClick = 0, startClick= 0, endClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vote);

        //Initialize the Variable/Views
        init();

        //OnClick Event
        tvDate.setOnClickListener(this);
        tvStart.setOnClickListener(this);
        tvEnd.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

    }

    private void init() {
        tvDate = findViewById(R.id.tv_vote_date);
        etName = findViewById(R.id.et_vote_name);
        etOne = findViewById(R.id.et_vote1);
        etTwo = findViewById(R.id.et_vote2);
        etThree = findViewById(R.id.et_vote3);
        etFour = findViewById(R.id.et_vote4);
        etFive = findViewById(R.id.et_vote5);
        tvStart = findViewById(R.id.tv1_vote_start);
        tvEnd = findViewById(R.id.tv1_vote_end);
        calendar = Calendar.getInstance();
        btnAdd = findViewById(R.id.btn_vote_add);
        firebase = FirebaseDatabase.getInstance();
        reference = firebase.getReference("Voting App");
    }

    @Override
    public void onClick(View v) {
        if(v == tvStart){
            startClick = 1;
            TimePickerDialog.OnTimeSetListener lister = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    tvStart.setText(new SimpleDateFormat("HH.mm").format(calendar.getTime()));
                }
            };
            TimePickerDialog tpd = new TimePickerDialog(AddVoteActivity.this, lister, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
            tpd.show();
        }

        if(v == tvEnd){
            endClick = 1;
            TimePickerDialog.OnTimeSetListener lister = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    tvEnd.setText(new SimpleDateFormat("HH.mm").format(calendar.getTime()));
                }
            };
            TimePickerDialog tpd = new TimePickerDialog(AddVoteActivity.this, lister, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
            tpd.show();
        }

        if(v == tvDate){
            dateClick = 1;
            DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    //calendar access
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

                    try {
                        date = sdf.parse(sdf.format(calendar.getTime())).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    tvDate.setText(sdf.format(calendar.getTime()));
                }
            };

            //Show the calendar interface
            DatePickerDialog dpd = new DatePickerDialog(AddVoteActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        }
        if(v== btnAdd){
            String name = etName.getText().toString();
            String candidateOne = etOne.getText().toString();
            String candidateTwo = etTwo.getText().toString();
            String candidateThree = etThree.getText().toString();
            String candidateFour = etFour.getText().toString();
            String candidateFive = etFive.getText().toString();

            if(dateClick == 0){
                Toast.makeText(this, "Please select date!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(startClick == 0){
                Toast.makeText(this, "Please select start time!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(endClick == 0){
                Toast.makeText(this, "Please select end time!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(name.isEmpty() || candidateOne.isEmpty() || candidateTwo.isEmpty() ){
                Toast.makeText(this, "Fill the fields first!!", Toast.LENGTH_SHORT).show();
                return;
            }

            String start = tvStart.getText().toString();
            String end = tvEnd.getText().toString();

            Vote newVote = new Vote(name, start, end, date, candidateOne, candidateTwo);

            if(!candidateThree.isEmpty()){
                newVote.setCandidateThree(candidateThree);
            }
            if(!candidateFour.isEmpty()){
                newVote.setCandidateFour(candidateFour);
            }
            if(!candidateFive.isEmpty()){
                newVote.setCandidateFive(candidateFive);
            }

            //Set Value to database
            DatabaseReference pushref = reference.child("Votes").push();
            pushref.setValue(newVote);
            startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
            finish();

        }
    }
}