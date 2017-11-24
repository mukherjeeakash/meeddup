package com.mukherjeeakash.meeddup;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupViewActivity extends AppCompatActivity {
    private DatabaseReference groupCodeRef;
    private String userName;
    private RecyclerView mUserList;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
        Bundle bundle = getIntent().getExtras();
        final String groupCode = bundle.getString("group code");
        userName = bundle.getString("user name");
        userType = bundle.getString("user type");

        mUserList = (RecyclerView) findViewById(R.id.recyclerView);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
        mUserList.setHasFixedSize(true);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference groupRef = database.getReference("groupCodes/" + groupCode);
        groupCodeRef = groupRef;

        final TextView mGroupName = (TextView) findViewById(R.id.groupName);
        final TextView mNumMember = (TextView) findViewById(R.id.numMember);
        Button calculate = (Button) findViewById(R.id.button);
        Button share = (Button) findViewById(R.id.shareButton);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCode(groupCode);
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calculate meeting point
                groupRef.child("hasPressedCalculate").setValue(Boolean.TRUE);
            }
        });

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    mGroupName.setText((String) dataSnapshot.child("group name").getValue());
                    Map<String, Object> users = (HashMap<String, Object>)
                            dataSnapshot.child("users").getValue();
                    UserAdapter userAdapter = new UserAdapter(
                            users.keySet().toArray(new String[users.keySet().size()]));
                    mUserList.setAdapter(userAdapter);
                    mNumMember.setText(String.format("Group Code: %s  ···  %s members", groupCode,
                            users.size()));

                    if ((Boolean) dataSnapshot.child("hasPressedCalculate").getValue()) {
                        String uri = "http://maps.google.com/maps?q=loc:42,-88(Meeting Place)";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivityForResult(intent, 1);
                    }
                } catch (NullPointerException e) {
                    if (userType == "create") {
                        groupRef.removeValue();
                    } else {
                        groupRef.child("users").child(userName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String uri = "http://maps.google.com/maps?q=loc:42,-88(Meeting Place)";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivityForResult(intent, 1);
            }
        });
    }

    //https://code.tutsplus.com/tutorials/android-sdk-implement-a-share-intent--mobile-8433
    public void shareCode(String groupCode) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Meeddup group code:");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Here's my Meeddup group " +
                "code:\n" + groupCode);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            groupCodeRef.removeValue();
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        if (this.isFinishing()) {
            groupCodeRef.child("users").child(userName).removeValue();
        }
        super.onStop();
    }
}
