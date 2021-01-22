package com.aversoft.votingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aversoft.votingapp.Model.Vote;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvHomeCount, tvMsg, tvOneName, tvOneVote, tvTwoName, tvTwoVote,
             tvThreeName, tvThreeVote, tvFourName, tvFourVote, tvFiveName, tvFiveVote;
    LinearLayout layoutThree, layoutFour, layoutFive, layoutAbc;
    ProgressBar pb;
    FloatingActionButton fab;
    ListView lvHome;
    FirebaseDatabase firebase;
    DatabaseReference reference;
    Calendar calendar;
    ArrayList<Vote> votes;
    Vote todayVote;
    String name, one, two, three, four, five;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        //Initialization
        init();

        //Visibility of progressbar
        layoutAbc.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);

        try {
            //Try to get all votes from database
            getVotes();

            //After getting data visibility of views are changed
            layoutAbc.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Click event
        fab.setOnClickListener(this);
        lvHome.setAdapter(adapter);
        layoutAbc.setOnClickListener(this);
    }

    //Implementation of listview adapter to show data in list
    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return votes.size();
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

            //Inflate the android layout to show data
            convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);
            TextView tv1 = convertView.findViewById(android.R.id.text1);
            TextView tv2 = convertView.findViewById(android.R.id.text2);
            tv1.setText(votes.get(position).getName());
            tv2.setText(votes.get(position).getStartAt());
            return convertView;
        }
    };

    private void getVotes() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String date = sdf.format(Calendar.getInstance().getTime());
        long dateLong = sdf.parse(date).getTime();
        reference.child("Votes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Vote vote = data.getValue(Vote.class);

                    //Check if today's vote is available
                    if(vote.getDate() == dateLong){
                        todayVote = vote;
                        getTodayVoteInfo();
                    }

                    //Upcoming Vote checking
                    if(vote.getDate() > dateLong){
                        votes.add(vote);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getTodayVoteInfo() {

        //Get today's vote info
        if(todayVote != null) {
            name = todayVote.getName();
            one = todayVote.getCandidateOne();
            two = todayVote.getCandidateTwo();
            if(todayVote.getCandidateThree() != null){
                three = todayVote.getCandidateThree();
            }
            if(todayVote.getCandidateFour() != null){
                four = todayVote.getCandidateFour();
            }
            if(todayVote.getCandidateFive() != null){
                five = todayVote.getCandidateFive();
            }
        }
        //Show today's vote data by a method
        setTodaysVoteInfo();

        //Get today's vote result by a method
        getResult();
        getTotalCast();
    }

    private void getTotalCast() {
        reference.child("CastVote").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvHomeCount.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getResult() {
        Log.e("fahimtest", "Result/"+name+"/"+one);
        reference.child("Result/"+name+"/"+one).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvOneVote.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("Result/"+name+"/"+two).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvTwoVote.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(three != null){
            reference.child("Result/"+name+"/"+three).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tvThreeVote.setText(snapshot.getChildrenCount()+"");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if(four != null){
            reference.child("Result/"+name+"/"+four).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tvFourVote.setText(snapshot.getChildrenCount()+"");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if(five != null){
            reference.child("Result/"+name+"/"+five).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tvFiveVote.setText(snapshot.getChildrenCount()+"");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setTodaysVoteInfo() {
        layoutAbc.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        tvOneName.setText(one);
        tvTwoName.setText(two);
        if(three != null){
            layoutThree.setVisibility(View.VISIBLE);
            tvThreeName.setText(three);
        }
        if(four != null){
            layoutFour.setVisibility(View.VISIBLE);
            tvFourName.setText(four);
        }
        if(five != null){
            layoutFive.setVisibility(View.VISIBLE);
            tvFiveName.setText(five);
        }
    }


    private void init() {
        calendar = Calendar.getInstance();
        tvHomeCount = findViewById(R.id.tv_home_count);
        votes = new ArrayList<>();
        tvMsg = findViewById(R.id.tv_home_msg);
        tvOneName = findViewById(R.id.tv_one_name);
        tvOneVote = findViewById(R.id.tv_one_vote);
        tvTwoName = findViewById(R.id.tv_two_name);
        layoutAbc = findViewById(R.id.layout_abcd);
        pb = findViewById(R.id.pb_abc);
        tvTwoVote = findViewById(R.id.tv_two_vote);
        tvThreeName = findViewById(R.id.tv_three_name);
        tvThreeVote = findViewById(R.id.tv_three_vote);
        tvFourName = findViewById(R.id.tv_four_name);
        tvFourVote = findViewById(R.id.tv_four_vote);
        tvFiveName = findViewById(R.id.tv_five_name);
        tvFiveVote = findViewById(R.id.tv_five_vote);
        layoutThree = findViewById(R.id.layout_three);
        layoutFour = findViewById(R.id.layout_four);
        layoutFive = findViewById(R.id.layout_five);
        fab = findViewById(R.id.fab_home);
        lvHome = findViewById(R.id.lv_home);
        firebase = FirebaseDatabase.getInstance();
        reference = firebase.getReference("Voting App");
    }

    @Override
    public void onClick(View v) {
        if(v == fab){
            startActivity(new Intent(getApplicationContext(), AddVoteActivity.class));
            finish();
        }
        if(v == layoutAbc){
            startActivity(new Intent(getApplicationContext(), TotalVoteCastActivity.class).putExtra("name", name));

        }
    }
}