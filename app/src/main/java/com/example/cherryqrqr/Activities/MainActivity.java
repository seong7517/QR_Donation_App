package com.example.cherryqrqr.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cherryqrqr.R;
import com.example.cherryqrqr.Utils.QRScanWV;
import com.example.cherryqrqr.Utils.SharedPreferenceUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private ImageView qr_scan;
    private ImageButton dona_list;
    private ImageView pref;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qr_scan = findViewById(R.id.qr_scan);
        dona_list = findViewById(R.id.dona_list);
        pref = findViewById(R.id.pref);

        qr_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan = new IntentIntegrator(MainActivity.this);
                qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        });

        pref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MySetting.class);
                startActivity(intent);
            }
        });

//        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if(task.isSuccessful()){
                            String idToken = task.getResult().getToken();
                            Log.e("TAG", "idToken : " + idToken);
                            //HTTPS를 통해 백엔드로 토큰전송
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //WebView를 사용한 처리방식
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(MainActivity.this, QRScanWV.class);
        if(result != null){
            if(result.getContents() == null){ //qr코드가 없으면
                Toast.makeText(MainActivity.this, "취소!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "스캔완료", Toast.LENGTH_SHORT).show(); //qr코드 결과가 있으면
                try {
                    JSONObject obj = new JSONObject(result.getContents()); //data를 json으로 변환
                    intent.putExtra("mrhstld", obj.getString("mrhstld"));
                    intent.putExtra("setleAmt", obj.getString("setleAmt"));

                    Log.e("MainActivity", "여기는 Main액티비티");
                    Log.e("상점명", "상점명 : " + obj.getString("mrhstld"));
                    Log.e("기부금액", "기부금액 : " + obj.getString("setleAmt"));
                    startActivity(intent);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}