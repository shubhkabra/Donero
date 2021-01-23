package doneroapp.globulaertech.com.doneroapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import doneroapp.globulaertech.com.doneroapplication.R;

public class OffersActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar mToolbar;
    String spinner_item;
    private CardView amt,pun;
    private Spinner msSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        setUpToolbar();
        List<String> categories = new ArrayList<String>();
        categories.add("Select city names");
        categories.add("Amravati");
        categories.add("Pune");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amt = (CardView)findViewById(R.id.amravti);
        pun = (CardView)findViewById(R.id.pune);
        pun.setVisibility(View.GONE);
        amt.setVisibility(View.GONE);
        msSpinner = (Spinner) findViewById(R.id.cityname);
        // attaching data adapter to spinner
        msSpinner.setAdapter(dataAdapter);
        msSpinner.setOnItemSelectedListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OffersActivity.this, ContactUs.class));
                finish();
            }
        });
    }

    public void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        setTitle("Donero Offers");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(OffersActivity.this, MapsActivity.class));
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OffersActivity.this, MapsActivity.class));
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinner_item = parent.getItemAtPosition(position).toString();
        if (spinner_item.equals("Amravati")){
            amt.setVisibility(View.VISIBLE);
        }
        if (spinner_item.equals("Pune")){
            amt.setVisibility(View.GONE);
            pun.setVisibility(View.VISIBLE);
        }

        // Showing selected spinner item
       Toast.makeText(parent.getContext(), "Selected: " + spinner_item, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}