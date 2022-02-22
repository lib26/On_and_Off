package org.techtown.sns_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.View;

import org.techtown.sns_project.Normal.NormalMainActivity;

import java.util.Map;

public class SettingsFragmentForEnterprise extends PreferenceFragmentCompat {

    public SettingsFragmentForEnterprise() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditTextPreference memberInfo = (EditTextPreference)findPreference("signature");
        SetMemberInfo(memberInfo, firebaseUser, db);
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://github.com/Owen-Choi/ToyProject_SNS"));
        Preference mypref = (Preference)findPreference("contact_preference");
        mypref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(i);
                return true;
            }
        });
    }

    private void SetMemberInfo(EditTextPreference memberInfo,
                               FirebaseUser firebaseUser, FirebaseFirestore db) {
        DocumentReference docrf = db.collection("enterprise").document(firebaseUser.getUid());
        docrf.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        MemberInfoClass memberInfoClass = document.toObject(MemberInfoClass.class);
                        //memberInfo.setText(memberInfoClass.getName());
                        memberInfo.setText("hi there");
                    }
                }
            }
        });
    }

}