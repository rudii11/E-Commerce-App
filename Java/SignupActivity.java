package internship.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    EditText dateOfBirth;

    Calendar calendar;

    //RadioButton male;
    RadioGroup radioGroup;
    //RadioButton male,female,transgender;

    Spinner spinner;

    String[] cityArray = {"Ahmedabad", "Gandhinagar", "Vadodara", "Rajkot", "Surat"};

    EditText name, email, contact, password, confirmPassword;
    Button signup;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    String sGender, sCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Signup");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateOfBirth = findViewById(R.id.signup_date_of_birth);

        calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateClick = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);

                SimpleDateFormat dateFormate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                dateOfBirth.setText(dateFormate.format(calendar.getTime()));
            }
        };

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(SignupActivity.this, dateClick, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });

        /*male = findViewById(R.id.signup_male);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(SignupActivity.this,male.getText().toString());
            }
        });*/

        /*male = findViewById(R.id.signup_male);
        female = findViewById(R.id.signup_female);
        transgender = findViewById(R.id.signup_transgender);*/

        radioGroup = findViewById(R.id.signup_gender);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                sGender = radioButton.getText().toString();
                new CommonMethod(SignupActivity.this, sGender);
            }
        });

        spinner = findViewById(R.id.signup_city);
        ArrayAdapter adapter = new ArrayAdapter(SignupActivity.this, android.R.layout.simple_list_item_1, cityArray);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sCity = cityArray[i];
                new CommonMethod(SignupActivity.this, sCity);
                /*if(i==0){
                    new CommonMethod(SignupActivity.this,"Male");
                    male.setChecked(true);
                }
                else if(i==1){
                    new CommonMethod(SignupActivity.this,"Female");
                    female.setChecked(true);
                }
                else if(i==2){
                    new CommonMethod(SignupActivity.this,"Female");
                    female.setChecked(true);
                }
                else if(i==3){
                    new CommonMethod(SignupActivity.this,"Transgender");
                    transgender.setChecked(true);
                }
                else{

                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        name = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        contact = findViewById(R.id.signup_contact);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirm_password);

        signup = findViewById(R.id.signup_button);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().equalsIgnoreCase("")) {
                    name.setError("Name Required");
                } else if (email.getText().toString().trim().equalsIgnoreCase("")) {
                    email.setError("Email Id Required");
                } else if (!email.getText().toString().matches(emailPattern)) {
                    email.setError("Valid Email Id Required");
                } else if (contact.getText().toString().trim().equalsIgnoreCase("")) {
                    contact.setError("Contact No. Required");
                } else if (contact.getText().toString().length() < 10) {
                    contact.setError("Valid Contact No. Required");
                } else if (password.getText().toString().trim().equalsIgnoreCase("")) {
                    password.setError("Password Required");
                } else if (password.getText().toString().length() < 6) {
                    password.setError("Min. 6 Character Required");
                } else if (confirmPassword.getText().toString().trim().equalsIgnoreCase("")) {
                    confirmPassword.setError("Confirm Password Required");
                } else if (!password.getText().toString().matches(confirmPassword.getText().toString())) {
                    confirmPassword.setError("Password Does Not Match");
                } else if (dateOfBirth.getText().toString().trim().equalsIgnoreCase("")) {
                    dateOfBirth.setError("Date Of Birth Required");
                } else if (radioGroup.getCheckedRadioButtonId() == -1) {
                    new CommonMethod(SignupActivity.this, "Please Select Gender");
                } else {
                    if (new ConnectionDetector(SignupActivity.this).isConnectingToInternet()) {
                        new doSignup().execute();
                    } else {
                        new ConnectionDetector(SignupActivity.this).connectiondetect();
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class doSignup extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SignupActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", name.getText().toString());
            hashMap.put("email", email.getText().toString());
            hashMap.put("contact", contact.getText().toString());
            hashMap.put("password", password.getText().toString());
            hashMap.put("city", sCity);
            hashMap.put("dob", dateOfBirth.getText().toString());
            hashMap.put("gender", sGender);
            return new MakeServiceCall().MakeServiceCall(ConstantUrl.SIGNUP_URL, MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("Status") == true) {
                    new CommonMethod(SignupActivity.this, object.getString("Message"));
                    onBackPressed();
                } else {
                    new CommonMethod(SignupActivity.this, object.getString("Message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                new CommonMethod(SignupActivity.this, e.getMessage());
            }

        }
    }
}