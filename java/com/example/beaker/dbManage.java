package com.example.beaker;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class dbManage {
    String Reg="People";
    String wall="Wallet";
    Map<String,Object> user_state=new HashMap<>();
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();
    private DatabaseReference mDatabase;
    private String tempString;






    public dbManage() {
//        db.setFirestoreSettings(settings);
    }

    Map<String,Object> temp=new HashMap<>();


    public void setDetils(String first_name1,String last_name1,String mob1,String email1,String pass1,String dob1,String gender1){

        //db = FirebaseFirestore.getInstance();
        user_state.put("First Name",first_name1);
        user_state.put("Last Name",last_name1);
        user_state.put("Mobile Number",mob1);
        user_state.put("Email-ID",email1);
        user_state.put("Password",pass1);
        user_state.put("DOB",dob1);
        user_state.put("Gender",gender1);
        db.collection("People").document(email1).set(user_state);
    }
 /*   public void verify_User(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                null);        // OnVerificationStateChangedCallbacks
    }*/




    public boolean checkMobile(final String mobile_number){
        final boolean[] i = {false};
        db.collection(Reg)
                .document(mobile_number)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot DocSnap=task.getResult();
                            if(DocSnap.exists()){
                                i[0]=true;
                            }
                            else{
                                i[0]=false;
                            }
                        }
                        else {
                            Log.d(TAG, "onComplete: Task Failed!!");
                        }
                    }
                });
        return i[0];
    }

    public boolean checkEmail(final String email_v){
        final boolean[] i = {false};
        db.collection(Reg)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot DocSnap : task.getResult()) {
                                temp=DocSnap.getData();
                                tempString=(String)temp.get("Email-ID");
                                if(tempString==null){
                                    i[0]=true;
                                    break;
                                }
                                else continue;
                            }
                        }
                        else {
                            //i[0]=false;
                            //Toast.makeText(this, "Connect to Internet!!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: Task Failed!!");
                        }
                    }
                });
        return i[0];
    }

    public boolean checkWalletAccount(final String wallet_id){
        final boolean[] l = new boolean[1];
//        final boolean[] i={false};
        return l[0];
    }

    public String getId(){
        final String[] s = new String[1];
        db.collection(wall)
                .document(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snapshot=task.getResult();
                        try{
                            temp=snapshot.getData();
                            s[0]= String.valueOf(temp.get("id"));
                        }
                        catch (NullPointerException e){
                            s[0]=null;
                        }
                    }
                });
        return s[0];
    }

    public void CreateWalletProfile(String id,String pin) throws Exception{
        String emailID=user.getEmail();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        temp.put("id",id);
        temp.put("Cash","0");
        temp.put("PIN",pin);
        db.collection(wall)
                .document(emailID)
                .set(temp);
        mDatabase.child(wall).child(id).setValue(temp);
    }





}
