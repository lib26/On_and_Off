package org.techtown.sns_project.Enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.techtown.sns_project.CommonSignInActivity;
import org.techtown.sns_project.Enterprise.QR.EnterpriseQRActivity;
import org.techtown.sns_project.Enterprise.QR.EnterpriseQRListActivity;
import org.techtown.sns_project.R;

public class EnterpriseMainActivity extends AppCompatActivity {
    private final String TAG = "MainActivityDB";
    private long backKeyPressedTime = 0;
    private Toast terminate_guide_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_main);
        ActionBar ac = getSupportActionBar();
        getSupportActionBar().setIcon(R.drawable.onandofflogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.EnterpsireQRButton).setOnClickListener(onClickListener);
        findViewById(R.id.EnterpriseQRListButton).setOnClickListener(onClickListener);
        // manifest에서 첫 화면은 MainActivity로 설정되어있는데,
        // 로그인이 되지 않은 상태면 로그인창을 띄워야 한다.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            StartActivity(CommonSignInActivity.class);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.LogoutMenu) {
            FirebaseAuth.getInstance().signOut();
            StartActivity(CommonSignInActivity.class);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void StartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.EnterpsireQRButton:
                    StartActivity(EnterpriseQRActivity.class);
                    break;
                case R.id.EnterpriseQRListButton:

                    StartActivity(EnterpriseQRListActivity.class);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 1000) {
            backKeyPressedTime = System.currentTimeMillis();
            terminate_guide_msg = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            terminate_guide_msg.show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 1000) {
            terminate_guide_msg.cancel();
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

    }
}