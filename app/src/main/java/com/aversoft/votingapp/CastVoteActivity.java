package com.aversoft.votingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aversoft.votingapp.Model.Vote;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CastVoteActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    LinearLayout layoutCast, layoutSecond;
    ListView lvCast;
    ProgressBar pbCast;
    TextView tvVoteName, tvVoteStart, tvVoteEnd, tvVoteDate, tvVoteMessage;
    FirebaseDatabase firebase;
    DatabaseReference reference;
    Intent intent;
    Vote vote;
    ArrayList<String> candidates;
    SharedPreferences sp;
    String userId;
    boolean voted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_vote);

        init();
        layoutCast.setVisibility(View.GONE);
        pbCast.setVisibility(View.VISIBLE);
        setVoteInfo();

        lvCast.setOnItemClickListener(this);
    }



    private void getCandidates() {

       if(vote.getCandidateOne() != null){
           candidates.add(vote.getCandidateOne());
       }
       if(vote.getCandidateTwo() != null){
           candidates.add(vote.getCandidateTwo());
       }
       if(vote.getCandidateThree() != null){
           candidates.add(vote.getCandidateThree());
       }
       if(vote.getCandidateFour() != null){
           candidates.add(vote.getCandidateFour());
       }
       if(vote.getCandidateFive() != null){
           candidates.add(vote.getCandidateFive());
       }

    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return candidates.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            TextView tvCandidate = convertView.findViewById(android.R.id.text1);
            tvCandidate.setText(candidates.get(position));
            return convertView;
        }
    };

    private void setVoteInfo() {
        tvVoteName.setText(vote.getName()+"");
        tvVoteStart.setText(vote.getStartAt()+"");
        tvVoteEnd.setText(vote.getEndAt()+"");
        tvVoteDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(vote.getDate())+"");

        pbCast.setVisibility(View.GONE);
        layoutCast.setVisibility(View.VISIBLE);

        try {
            checkValidity();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void checkValidity() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
        String thisTime = sdf.format(Calendar.getInstance().getTime());
        long thisTimeInMilis = sdf.parse(thisTime).getTime();
        long startAt = sdf.parse(vote.getStartAt()).getTime();
        long endAt = sdf.parse(vote.getEndAt()).getTime();

        if(thisTimeInMilis >= startAt && thisTimeInMilis <= endAt){
            if(voted){
                layoutSecond.setVisibility(View.GONE);
                tvVoteMessage.setVisibility(View.VISIBLE);
                tvVoteMessage.setText("You have already cast your VOTE!");
            }else{
                layoutSecond.setVisibility(View.VISIBLE);
                tvVoteMessage.setVisibility(View.GONE);
                lvCast.setAdapter(adapter);
            }
        }else{
            layoutSecond.setVisibility(View.GONE);
            tvVoteMessage.setVisibility(View.VISIBLE);
        }

    }

    private void init() {
        layoutCast = findViewById(R.id.layout_cast);
        layoutSecond = findViewById(R.id.layout_cast_second);
        candidates = new ArrayList<>();
        lvCast = findViewById(R.id.lv_cast);
        pbCast = findViewById(R.id.pb_cast);
        tvVoteName = findViewById(R.id.tv_cast_name);
        tvVoteStart = findViewById(R.id.tv_cast_start);
        tvVoteEnd = findViewById(R.id.tv_cast_end);
        tvVoteDate = findViewById(R.id.tv_cast_occur);
        tvVoteMessage = findViewById(R.id.tv_cast_message);
        firebase = FirebaseDatabase.getInstance();
        reference = firebase.getReference("Voting App");
        intent = getIntent();
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");
        vote = (Vote) intent.getSerializableExtra("todaysVote");
        voted = intent.getBooleanExtra("myVote", false);
        getCandidates();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = candidates.get(position);
        new AlertDialog.Builder(CastVoteActivity.this)
                .setTitle("Cast Your VOTE to")
                .setMessage("Candidate Name: "+candidates.get(position))
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Cast Vote", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reference.child("Result").child(vote.getName()).child(name).push().setValue(Calendar.getInstance().getTimeInMillis());
                        reference.child("CastVote").child(vote.getName()).push().setValue(userId);
                        finish();
                    }
                })
                .show();
    }
}