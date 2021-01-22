package com.aversoft.votingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aversoft.votingapp.Model.Vote;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class VoteActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvName, tvStart, tvEnd, tvDate;
    ListView lvVote;
    FirebaseDatabase firebase;
    DatabaseReference reference;
    LinearLayout layout;
    Calendar calendar;
    ArrayList<Vote> allVote;
    Vote todaysVote;
    SharedPreferences sp;
    String userId;
    boolean myVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        init();


        try {
            getVotes();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        layout.setOnClickListener(this);

        lvVote.setAdapter(adapter);

    }

    private void getMyVote(String name) {
        reference.child("CastVote").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    String id =  data.getValue().toString();
                    Log.e("fahim", id+"   ami");
                    if(id.equals(userId)){
                        myVote = true;
                        Log.e("fahim1", myVote+"");
                        break;
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return allVote.size();
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
            convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);
            TextView tvVoteName = convertView.findViewById(android.R.id.text1);
            TextView tvVoteDate = convertView.findViewById(android.R.id.text2);
            tvVoteName.setText(allVote.get(position).getName());
            tvVoteDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(allVote.get(position).getDate()));
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
                    if(vote.getDate() == dateLong){
                        layout.setVisibility(View.VISIBLE);
                        tvName.setText(vote.getName());
                        tvStart.setText(vote.getStartAt());
                        tvEnd.setText(vote.getEndAt());
                        tvDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(vote.getDate()));
                        todaysVote = vote;
                        getMyVote(todaysVote.getName());
                    }
                    if ( vote.getDate() > dateLong){
                        allVote.add(vote);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void init() {
        allVote = new ArrayList<>();
        calendar = Calendar.getInstance();
        tvName = findViewById(R.id.tv_vote_name);
        tvStart = findViewById(R.id.tv_vote_start);
        tvEnd = findViewById(R.id.tv_vote_end);
        tvDate = findViewById(R.id.tv_vote_occur);
        layout = findViewById(R.id.layout_vote);
        lvVote = findViewById(R.id.lv_vote);
        firebase = FirebaseDatabase.getInstance();
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");
        reference = firebase.getReference("Voting App");
    }

    @Override
    public void onClick(View v) {
        if(v == layout){
            Log.e("fahim2", myVote+"");
            startActivity(new Intent(getApplicationContext(), CastVoteActivity.class).putExtra("myVote", myVote).putExtra("todaysVote", todaysVote));
        }
    }
}