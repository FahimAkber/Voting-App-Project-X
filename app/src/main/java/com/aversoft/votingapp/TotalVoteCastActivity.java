package com.aversoft.votingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aversoft.votingapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TotalVoteCastActivity extends AppCompatActivity {

    ListView lvTotalCast;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    ArrayList<String> voters;
    Intent intent;
    String voteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_vote_cast);

        init();
        getVoters();
        lvTotalCast.setAdapter(adapter);

    }

    private void getVoters() {
        reference.child("CastVote").child(voteName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    String name = (String) data.getValue();
                    findName(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findName(String name) {
        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    User user = data.getValue(User.class);
                    if(user.getId().equals(name)){
                        voters.add(user.getName());
                        adapter.notifyDataSetChanged();
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
            return voters.size();
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
            TextView tvName = convertView.findViewById(android.R.id.text1);
            tvName.setText(voters.get(position));
            return convertView;
        }
    };

    private void init() {
        lvTotalCast = findViewById(R.id.lv_total_cast);
        firebaseDatabase = FirebaseDatabase.getInstance();
        voters = new ArrayList<>();
        reference = firebaseDatabase.getReference("Voting App");
        intent = getIntent();
        voteName = intent.getStringExtra("name");
    }
}