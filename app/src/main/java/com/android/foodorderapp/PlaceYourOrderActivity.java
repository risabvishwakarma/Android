package com.android.foodorderapp;

import static com.android.foodorderapp.R.id.inputCardNumber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.foodorderapp.adapters.PlaceYourOrderAdapter;
import com.android.foodorderapp.model.Menu;
import com.android.foodorderapp.model.RestaurantModel;
import com.android.foodorderapp.model.Userinfo;

import java.util.regex.Pattern;

public class PlaceYourOrderActivity extends AppCompatActivity {

    private EditText inputName, inputAddress, inputCity, inputState, inputZip,inputCardNumber, inputCardExpiry,inputCardExpiry2, inputCardPin,inputPhone ;
    private RecyclerView cartItemsRecyclerView;
    private TextView tvSubtotalAmount, tvDeliveryChargeAmount, tvDeliveryCharge, tvTotalAmount, buttonPlaceYourOrder;
    private SwitchCompat switchDelivery;
    private boolean isDeliveryOn;
    private PlaceYourOrderAdapter placeYourOrderAdapter;
    Userinfo userinfo;
    ActionBar actionBar;
    String uid ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_your_order);

        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        uid=getIntent().getStringExtra("uid");
        actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(true);

        inputName = findViewById(R.id.inputName);
        inputPhone=findViewById(R.id.inputPhone);
        inputAddress = findViewById(R.id.inputAddress);
        inputCity = findViewById(R.id.inputCity);
        inputState = findViewById(R.id.inputState);
        inputZip = findViewById(R.id.inputZip);
        inputCardNumber= findViewById(R.id.inputCardNumber);
        inputCardExpiry = findViewById(R.id.inputCardExpiry);
        inputCardExpiry2=findViewById(R.id.inputCardExpiry2);
        inputCardPin = findViewById(R.id.inputCardPin);
        tvSubtotalAmount = findViewById(R.id.tvSubtotalAmount);
        tvDeliveryChargeAmount = findViewById(R.id.tvDeliveryChargeAmount);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        buttonPlaceYourOrder = findViewById(R.id.buttonPlaceYourOrder);
        switchDelivery = findViewById(R.id.switchDelivery);
        inputZip.setAutofillHints(View.AUTOFILL_HINT_POSTAL_CODE);
        inputAddress.setAutofillHints(View.AUTOFILL_HINT_POSTAL_ADDRESS);
        inputCardNumber.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_NUMBER);
        inputCardExpiry.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH);
        inputCardExpiry2.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR);

        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView);

        buttonPlaceYourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaceOrderButtonClick(restaurantModel);
            }
        });

        switchDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    inputAddress.setVisibility(View.VISIBLE);
                    inputCity.setVisibility(View.VISIBLE);
                    inputState.setVisibility(View.VISIBLE);
                    inputZip.setVisibility(View.VISIBLE);
                    tvDeliveryChargeAmount.setVisibility(View.VISIBLE);
                    tvDeliveryCharge.setVisibility(View.VISIBLE);
                    isDeliveryOn = true;
                    actionBar.hide();
                } else {
                    inputAddress.setVisibility(View.GONE);
                    inputCity.setVisibility(View.GONE);
                    inputState.setVisibility(View.GONE);
                    inputZip.setVisibility(View.GONE);
                    tvDeliveryChargeAmount.setVisibility(View.GONE);
                    tvDeliveryCharge.setVisibility(View.GONE);
                    isDeliveryOn = false;
                    actionBar.show();
                }
                calculateTotalAmount(restaurantModel);
            }
        });
        initRecyclerView(restaurantModel);
        calculateTotalAmount(restaurantModel);
    }

    private void calculateTotalAmount(RestaurantModel restaurantModel) {
        float subTotalAmount = 0f;

        for(Menu m : restaurantModel.getMenus()) {
            subTotalAmount += m.getPrice() * m.getTotalInCart();
        }

        tvSubtotalAmount.setText("₹"+String.format("%.2f", subTotalAmount));
        if(isDeliveryOn) {
            tvDeliveryChargeAmount.setText("₹"+String.format("%.2f", restaurantModel.getDelivery_charge()));
            subTotalAmount += restaurantModel.getDelivery_charge();
        }
        tvTotalAmount.setText("₹"+String.format("%.2f", subTotalAmount));
    }

    private void onPlaceOrderButtonClick(RestaurantModel restaurantModel) {
        if(TextUtils.isEmpty(inputName.getText().toString().trim())) {
            inputName.setError("Please enter name ");
            return;
        }
        else if(inputPhone.getText().toString().length()<10) {
            inputPhone.setError("Please enter Phone no. ");
            return;
        }
        else if(isDeliveryOn && TextUtils.isEmpty(inputAddress.getText().toString().trim())) {
            inputAddress.setError("Please enter valid address ");
            return;
        }else if(isDeliveryOn && TextUtils.isEmpty(inputCity.getText().toString().trim())) {
            inputCity.setError("Please enter city ");
            return;
        }else if(isDeliveryOn && inputState.getText().toString().length()<6) {
            inputState.setError("Please enter Pin ");
            return;
        }else if( inputCardNumber.getText().toString().length()<16) {
            inputCardNumber.setError("Please enter card number ");
            return;
        }else if( inputCardExpiry.getText().toString().equals("0")||Integer.parseInt(inputCardExpiry.getText().toString())>12 ) {
            inputCardExpiry.setError("MM");
            return;}
        else if( !Pattern.matches("20[2-5][1-9]",inputCardExpiry2.getText().toString())) {
                inputCardExpiry2.setError("YYYY");
                return;}
        else if( inputCardPin.getText().toString().length()<3) {
            inputCardPin.setError("Please enter card pin/cvv ");
            return;
        }


         userinfo=new Userinfo(inputName.getText().toString().trim(),inputPhone.getText().toString(),inputCardNumber.getText().toString(),inputCardExpiry.getText().toString(),inputCardPin.getText().toString(), inputAddress.getText().toString(),inputZip.getText().toString(),inputCity.getText().toString(),inputState.getText().toString());
      //  Toast.makeText(this, inputCardNumber.getText().toString(), Toast.LENGTH_SHORT).show();
        //start success activity..


        Intent i = new Intent(PlaceYourOrderActivity.this, OrderSucceessActivity.class);
  //      Log.d("MAIN",userinfo.Address);
    //    i.putExtra("Name",inputName.getText().toString());
//        i.putExtra("Phone",inputPhone.getText().toString());
      //  Bundle b=new Bundle();
       // b.putParcelable("v1",restaurantModel);
       // b.putParcelable("User1",userinfo);
       // System.out.println(userinfo);
        i.putExtra("User1",userinfo);
        i.putExtra("v1", restaurantModel);
        i.putExtra("uid",uid);
       // Toast.makeText(this,"B"+String.valueOf(restaurantModel),Toast.LENGTH_LONG).show();
        startActivityForResult(i, 1000);
    }

    private void initRecyclerView(RestaurantModel restaurantModel) {
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeYourOrderAdapter = new PlaceYourOrderAdapter(restaurantModel.getMenus());
        cartItemsRecyclerView.setAdapter(placeYourOrderAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Toast.makeText(this, "Place your order", Toast.LENGTH_SHORT).show();
        if(requestCode == 1000) {
            setResult(Activity.RESULT_OK);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}