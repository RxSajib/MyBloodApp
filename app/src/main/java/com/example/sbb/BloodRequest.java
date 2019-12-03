package com.example.sbb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class BloodRequest extends AppCompatActivity {

    private String ReciverID;
    private String SenderID;
    private DatabaseReference MuserDatabase;
    private FirebaseAuth Mauth;

    private DatabaseReference FriendsDatabase;
    private TextView phonenumber;
    private TextView blood;
    private TextView sextext;
    private TextView height;
    private TextView Age;
    private TextView friendsname;
    private TextView requestbuttontext;


    /// blood card
    private CardView requestbutton, unfriendbutton;
    /// blood card

    //////friends user details
    private String FriendsBloodGroup;
    private String FriendsUsername;
    private String FriendsGender;
    private String FriendsHeight;
    private String FriendsAge;
    private String FriendsPhoneNumber;
    private String FriendsPhotoUrl;
    //////friends user details

    private CircleImageView senderimage;
    private CircleImageView reciverimage;
    private String CURRENT_STATES;
    private DatabaseReference MFriendRef;
    private DatabaseReference AccepectFriendDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_request);


        AccepectFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friend");
        MFriendRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        requestbutton = findViewById(R.id.RequestButtonID);
        unfriendbutton = findViewById(R.id.UnFriendButtonID);

        requestbuttontext = findViewById(R.id.RequestTextID);
        CURRENT_STATES = "not_friend";
        friendsname = findViewById(R.id.FriendsNameID);
        blood = findViewById(R.id.BloodGroup);
        phonenumber = findViewById(R.id.PhoenNumberID);
        sextext = findViewById(R.id.Sex);
        height = findViewById(R.id.Height);
        Age = findViewById(R.id.AgeText);

        senderimage = findViewById(R.id.SenderImageID);
        reciverimage = findViewById(R.id.ReciverImageID);

        Mauth = FirebaseAuth.getInstance();
        SenderID = Mauth.getCurrentUser().getUid();
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);

        MuserDatabase.child(SenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profile_imageLink")) {
                        String Myimage = dataSnapshot.child("profile_imageLink").getValue().toString();
                        Picasso.with(getApplicationContext()).load(Myimage).placeholder(R.drawable.defaltimage).into(senderimage);
                        Picasso.with(getApplicationContext()).load(Myimage).networkPolicy(NetworkPolicy.OFFLINE).into(senderimage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ReciverID = getIntent().getStringExtra("Key");


        FriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ReciverID);
        FriendsDatabase.keepSynced(true);
        FriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("phone")) {
                        FriendsPhoneNumber = dataSnapshot.child("phone").getValue().toString();
                        phonenumber.setText(FriendsPhoneNumber);
                    }
                    if (dataSnapshot.hasChild("blood")) {
                        FriendsBloodGroup = dataSnapshot.child("blood").getValue().toString();
                        blood.setText(FriendsBloodGroup);
                    }
                    if (dataSnapshot.hasChild("gender")) {
                        FriendsGender = dataSnapshot.child("gender").getValue().toString();
                        sextext.setText(FriendsGender);
                    }
                    if (dataSnapshot.hasChild("hight")) {
                        FriendsHeight = dataSnapshot.child("hight").getValue().toString();
                        height.setText(FriendsHeight);
                    }
                    if (dataSnapshot.hasChild("age")) {
                        FriendsAge = dataSnapshot.child("age").getValue().toString();
                        Age.setText(FriendsAge);
                    }
                    if (dataSnapshot.hasChild("profile_imageLink")) {
                        String profile_imageLinkget = dataSnapshot.child("profile_imageLink").getValue().toString();
                        Picasso.with(getApplicationContext()).load(profile_imageLinkget).placeholder(R.drawable.defaltimage).into(reciverimage);
                        Picasso.with(getApplicationContext()).load(profile_imageLinkget).networkPolicy(NetworkPolicy.OFFLINE).into(reciverimage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }
                    if (dataSnapshot.hasChild("name")) {
                        String nameget = dataSnapshot.child("name").getValue().toString();
                        friendsname.setText(nameget);
                    }
                } else {
                    String errormessage = "somethings error";
                    Toast.makeText(getApplicationContext(), errormessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        unfriendbutton.setEnabled(false);

        if (!SenderID.equals(ReciverID)) {


            requestbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestbutton.setEnabled(false);

                    if (CURRENT_STATES.equals("not_friend")) {
                        sendfriend_request();
                    }
                    if (CURRENT_STATES.equals("request_send")) {
                        cancel_request();
                    }
                    if (CURRENT_STATES.equals("request_recived")) {
                        AccepectRequest();
                    }
                    if (CURRENT_STATES.equals("friend")) {
                        unfriendthispersion();
                    }
                }
            });

        } else {
            requestbutton.setVisibility(View.GONE);
            unfriendbutton.setVisibility(View.GONE);
        }

        maintaincesButton();

    }

    private void sendfriend_request() {
        MFriendRef.child(SenderID).child(ReciverID).child("request_type").setValue("send")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            MFriendRef.child(ReciverID).child(SenderID).child("request_type").setValue("recived")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                requestbutton.setEnabled(true);
                                                CURRENT_STATES = "request_send";
                                                requestbuttontext.setText("Cancel Request");
                                                requestbuttontext.setTextColor(Color.RED);

                                                unfriendbutton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }

                });
    }

    private void unfriendthispersion() {

        AccepectFriendDatabase.child(SenderID).child(ReciverID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AccepectFriendDatabase.child(ReciverID).child(SenderID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                requestbutton.setEnabled(true);
                                                requestbuttontext.setText("Send Request");
                                                requestbuttontext.setTextColor(Color.WHITE);
                                                CURRENT_STATES = "not_friend";

                                                unfriendbutton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void AccepectRequest() {

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String date = simpleDateFormatdate.format(calendardate.getTime());

        AccepectFriendDatabase.child(SenderID).child(ReciverID).child("date").setValue(date)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AccepectFriendDatabase.child(ReciverID).child(SenderID).child("date").setValue(date)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                MFriendRef.child(SenderID).child(ReciverID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    requestbutton.setEnabled(true);
                                                                    CURRENT_STATES = "friend";
                                                                    requestbuttontext.setTextColor(Color.RED);
                                                                    requestbuttontext.setText("Remove this pension");

                                                                    unfriendbutton.setEnabled(false);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void cancel_request() {
        MFriendRef.child(SenderID).child(ReciverID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            MFriendRef.child(ReciverID).child(SenderID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                CURRENT_STATES = "not_friend";
                                                requestbutton.setEnabled(true);
                                                requestbuttontext.setText("Request Me");
                                                requestbuttontext.setTextColor(Color.WHITE);
                                                unfriendbutton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void maintaincesButton() {

        MFriendRef.child(SenderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(SenderID)) {
                    String type = dataSnapshot.child(ReciverID).child("request_type").getValue().toString();

                    if (type.equals("send")) {
                        CURRENT_STATES = "request_send";

                        requestbuttontext.setText("Cancel Request");
                        requestbuttontext.setTextColor(Color.RED);

                        unfriendbutton.setEnabled(false);
                    } else if (type.equals("recived")) {
                        CURRENT_STATES = "request_recived";
                        requestbuttontext.setText("Accept Request");
                        requestbuttontext.setTextColor(Color.WHITE);

                        unfriendbutton.setEnabled(true);

                        unfriendbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancel_request();
                            }
                        });
                    }
                } else {
                    MFriendRef.child(SenderID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(ReciverID)) {
                                        CURRENT_STATES = "friend";
                                        requestbuttontext.setText("Remove Request");

                                        unfriendbutton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
