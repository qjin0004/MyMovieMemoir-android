package com.example.mymoviememoir;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mymoviememoir.database.MovieWatchlistDatabase;
import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.networkconnection.HashPassword;
import com.example.mymoviememoir.networkconnection.NetworkConnection;
import com.example.mymoviememoir.viewmodel.MovieWatchlistViewModel;
import com.example.mymoviememoir.watchlistEntity.MovieWatchlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SignUp extends AppCompatActivity {
    NetworkConnection networkConnection = null;
    TextView DOB;
    EditText editDOB;
    DatePickerDialog picker;
    RadioGroup radioGroup;
    RadioButton radioButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        networkConnection = new NetworkConnection();

        //set date time picker
        DOB  = (TextView)findViewById(R.id.DOB);
        editDOB = (EditText)findViewById(R.id.editDOB);
        editDOB.setInputType(InputType.TYPE_NULL);
        editDOB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(SignUp.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editDOB.setText( year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                },year,month,day);
                picker.show();
            }
        });

        // check radio group value
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = (RadioButton)findViewById(checkedId);
            }
        });

        final Button signUpButton = findViewById(R.id.signUpButton);
        final EditText etFirstName = findViewById(R.id.etFirstName);
        final EditText etSurname = findViewById(R.id.etSurname);
        final EditText editDOB = findViewById(R.id.editDOB);
        final EditText etAddress = findViewById(R.id.etAddress);
        final Spinner spState = findViewById(R.id.spState);
        final EditText etPostcode = findViewById(R.id.etPostcode);

        final EditText etUserEmail = findViewById(R.id.etUserEmail);
        final EditText etUserPassword = findViewById(R.id.etUserPassword);
        final EditText etUserConfirmPassword = findViewById(R.id.etUserConfirmPassword);


        //set red asterisk mark

        //firstName
        TextView firstName = findViewById(R.id.firstName);
        firstName.setText(addAsteriskMark(firstName));
        TextView surname = findViewById(R.id.surname);
        surname.setText(addAsteriskMark(surname));
        TextView gender = findViewById(R.id.gender);
        gender.setText(addAsteriskMark(gender));
        TextView address = findViewById(R.id.address);
        address.setText(addAsteriskMark(address));
        TextView state = findViewById(R.id.state);
        state.setText(addAsteriskMark(state));
        TextView postcode = findViewById(R.id.postcode);
        postcode.setText(addAsteriskMark(postcode));
        TextView email = findViewById(R.id.email);
        email.setText(addAsteriskMark(email));
        TextView Password = findViewById(R.id.Password);
        Password.setText(addAsteriskMark(Password));
        TextView ConfirmPassword = findViewById(R.id.ConfirmPassword);
        ConfirmPassword.setText(addAsteriskMark(ConfirmPassword));


        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //add Person
//                if(!etFirstName.getText().toString().isEmpty()&&!etSurname.getText().toString().isEmpty()&&
//                        radioGroup.getCheckedRadioButtonId()!=-1&&!etAddress.getText().toString().isEmpty()&&
//                        !spState.getSelectedItem().toString().isEmpty()&& !etPostcode.getText().toString().isEmpty()){
//                    String firstName = etFirstName.getText().toString();
//                    String surname = etSurname.getText().toString();
//                    String gender = radioButton.getText().toString().toLowerCase().substring(0,1);
//                    String dob = editDOB.getText().toString();
//                    String address = etAddress.getText().toString();
//                    String state = spState.getSelectedItem().toString();
//                    String postcode = etPostcode.getText().toString();
//                    AddPersonTask addPersonTask = new AddPersonTask();
//                    addPersonTask.execute(firstName,surname,gender,dob,address,state,postcode);
//                }
                //add Credentials
                 if (etUserEmail.getText().toString().isEmpty() || etUserPassword.getText().toString().isEmpty() || etUserConfirmPassword.getText().toString().isEmpty()||etPostcode.getText().toString().isEmpty() || spState.getSelectedItem().toString().isEmpty()||etAddress.getText().toString().isEmpty()||etSurname.getText().toString().isEmpty()||etFirstName.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"should not be empty",Toast.LENGTH_LONG).show();
                } else if(!etUserConfirmPassword.getText().toString().equals(etUserPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(),"passwords do not match",Toast.LENGTH_LONG).show();
                } else if(!(etUserEmail.getText().toString().isEmpty() || etUserPassword.getText().toString().isEmpty() || etUserConfirmPassword.getText().toString().isEmpty()||etPostcode.getText().toString().isEmpty() || spState.getSelectedItem().toString().isEmpty()||etAddress.getText().toString().isEmpty()||etSurname.getText().toString().isEmpty()||etFirstName.getText().toString().isEmpty())){
                    final String userEmail = etUserEmail.getText().toString();
                    HashPassword hashpassword = new HashPassword();
                    final String userPassword = hashpassword.getMd5(etUserPassword.getText().toString());

                    final String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    //test
                    final String firstName = etFirstName.getText().toString();
                    final String surname = etSurname.getText().toString();
                    final String gender = radioButton.getText().toString().toLowerCase().substring(0,1);
                    final String dob = editDOB.getText().toString();
                    final String address = etAddress.getText().toString();
                    final String state = spState.getSelectedItem().toString();
                    final String postcode = etPostcode.getText().toString();



                    new AsyncTask<String,Void, Boolean>(){
                        @Override
                        protected Boolean doInBackground(String... params){
                            boolean exist = networkConnection.checkExist(userEmail);
                            return exist;
                        }

                        @Override
                        protected void onPostExecute(Boolean results){
                            if(results == true){
                                Toast.makeText(getApplicationContext(),"this account is existed",Toast.LENGTH_LONG).show();
                            } else {
                                AddCredentialsTask addCredentialsTask = new AddCredentialsTask();
                                addCredentialsTask.execute(firstName,surname,gender,dob,address,state,postcode,userEmail,userPassword,date);
                            }
                        }
                    }.execute();




                }
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });


    }


    private class AddPersonTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params){
            return networkConnection.addPerson(params);
        }

        protected void onPostExecute(String result){
            TextView resultTextView = findViewById(R.id.test);
            resultTextView.setText(result);
        }
    }


    private class AddCredentialsTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params){
            return networkConnection.addCredential(params);
        }
        //addCredential() return personId

        protected void onPostExecute(String result){
            final EditText etFirstName = findViewById(R.id.etFirstName);
            final EditText etSurname = findViewById(R.id.etSurname);
            final EditText editDOB = findViewById(R.id.editDOB);
            final EditText etAddress = findViewById(R.id.etAddress);
            final Spinner spState = findViewById(R.id.spState);
            final EditText etPostcode = findViewById(R.id.etPostcode);

            String firstName = etFirstName.getText().toString();
            String surname = etSurname.getText().toString();
            String gender = radioButton.getText().toString().toLowerCase().substring(0,1);
            String dob = editDOB.getText().toString();
            String address = etAddress.getText().toString();
            String state = spState.getSelectedItem().toString();
            String postcode = etPostcode.getText().toString();

            Person person = new Person(Integer.parseInt(result),firstName,surname,gender,dob,address,state,Integer.parseInt(postcode));
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("person",person);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

//    private class CheckExist extends

    //set red asterisk mark
    public SpannableStringBuilder addAsteriskMark(TextView column){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(column.getText());
        int start = builder.length();
        builder.append("*");
        int end = builder.length();
        builder.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }




}
