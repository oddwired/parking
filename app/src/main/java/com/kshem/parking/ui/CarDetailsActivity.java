package com.kshem.parking.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kshem.parking.R;

import static com.kshem.parking.Constants.FAILED_RESULT_CODE;
import static com.kshem.parking.Constants.SUCCESS_RESULT_CODE;

public class CarDetailsActivity extends AppCompatActivity {

    private EditText car_number_plate;
    private Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        car_number_plate = (EditText) findViewById(R.id.car_plate);
        add = (Button) findViewById(R.id.add_vehicle);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(car_number_plate.getText())){
                    car_number_plate.setError("required");
                }else{
                    String number_plate = car_number_plate.getText().toString();

                    setResult(SUCCESS_RESULT_CODE, getIntent().putExtra("car_plate", number_plate));
                    finish();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(FAILED_RESULT_CODE);

        super.onBackPressed();
    }
}
