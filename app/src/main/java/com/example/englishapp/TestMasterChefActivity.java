package com.example.englishapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.shop.englishapp.R;

/**
 * Minimal test activity to verify navigation works
 */
public class TestMasterChefActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        android.util.Log.d("TestMasterChef", "TestMasterChefActivity onCreate");
        
        // Create simple layout programmatically
        TextView textView = new TextView(this);
        textView.setText("✅ TEST MASTER CHEF\n\nNếu bạn thấy màn hình này, navigation HOẠT ĐỘNG!\n\n🎉🎉🎉");
        textView.setTextSize(24);
        textView.setPadding(50, 200, 50, 50);
        textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        
        setContentView(textView);
        
        Toast.makeText(this, "✅ Navigation works! This is TestMasterChefActivity", Toast.LENGTH_LONG).show();
        
        android.util.Log.d("TestMasterChef", "TestMasterChefActivity created successfully");
    }
}
