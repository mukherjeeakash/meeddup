package com.mukherjeeakash.meeddup;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukherjeeakash.meeddup.API_KEY.GoogleMaps;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {
    private static Toast toast;
    private CreateGroupActivity currentActivity;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        final TextView groupName = (TextView) findViewById(R.id.groupName);
        final TextView userName = (TextView) findViewById(R.id.name);
        final Switch currLocSwitch = (Switch) findViewById(R.id.currLocSwitch);
        final TextView groupCode = (TextView) findViewById(R.id.groupCode);
        Button mbutton = (Button) findViewById(R.id.button);
        final DatabaseReference allGroupCodes = firebaseDatabase.getReference("groupCodes");
        final TextView address = (TextView) findViewById(R.id.address);
        currentActivity = this;

        currLocSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    address.setTag(address.getKeyListener());
                    address.setKeyListener(null);
                    address.setText("");
                } else {
                    address.setKeyListener((KeyListener) address.getTag());
                }
            }
        });

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((groupName.getText().length() > 0) && (userName.getText().length() > 0) &&
                        (currLocSwitch.isChecked() || (address.getText().length() > 0))) {
                    final List<Double> coordinates = getCoordinates(currLocSwitch.isChecked(),
                            address.getText().toString());

                    if (coordinates == null) {
                        createToast("Please enter valid address");
                        return;
                    }

                    if (groupCode.getText().length() == 0) {
                        groupCode.setText(allGroupCodes.push().getKey());
                    }

                    allGroupCodes.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(groupCode.getText().toString())) {
                                createToast("Group Code already exists");
                                return;
                            } else {
                                updateDatabase(groupName.getText().toString(),
                                        userName.getText().toString(), coordinates,
                                        groupCode.getText().toString());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(databaseError.getDetails(), databaseError.getMessage());
                        }
                    });
                } else {
                    createToast("Please fill out all non-optional fields");
                }
            }
        });
    }

    private void createToast(String text) {
        // https://stackoverflow.com/questions/6925156/how-to-avoid-a-toast-if-theres-one-toast
        // -already-being-shown
        try {
            toast.getView().isShown();     // true if visible
            toast.setText(text);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    private List<Double> getCoordinates(boolean isChecked, String address) {
        try {
            if (isChecked) {
                LocationTracker locationTracker = new LocationTracker(this.getApplicationContext());
                Location location = locationTracker.getLocation();
                if (location != null) {
                    ArrayList<Double> coordinates = new ArrayList<Double>();
                    coordinates.add(location.getLatitude());
                    coordinates.add(location.getLongitude());
                    return coordinates;
                } else {
                    createToast("Null Location");
                    return null;
                }
            } else {
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?" +
                        "address=%s&key=%s", address, GoogleMaps.API_KEY);
                AddressAsyncTask addressCoordinates = new AddressAsyncTask();
                addressCoordinates.execute(url);
                return addressCoordinates.get();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void updateDatabase(String groupName, String userName, List<Double> coordinates, String
            groupCode) {
        DatabaseReference newGroupRef = firebaseDatabase.getReference("groupCodes/" + groupCode);
        newGroupRef.child("group name").setValue(groupName);
        newGroupRef.child("creator name").setValue(userName);
        newGroupRef.child("users").child(userName).setValue(coordinates);
        newGroupRef.child("members").setValue("1");
        newGroupRef.child("hasPressedCalculate").setValue(Boolean.FALSE);
        Intent intent = new Intent(this, GroupViewActivity.class);
        intent.putExtra("group code", groupCode);
        intent.putExtra("user name", userName);
        intent.putExtra("user type", "create");
        startActivity(intent);
    }
}
