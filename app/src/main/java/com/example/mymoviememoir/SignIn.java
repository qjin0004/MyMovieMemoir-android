package com.example.mymoviememoir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.networkconnection.HashPassword;
import com.example.mymoviememoir.networkconnection.NetworkConnection;

public class SignIn extends AppCompatActivity {

    NetworkConnection networkConnection = null;
    Person person = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        networkConnection=new NetworkConnection();
        final ImageView imageIcon = findViewById(R.id.logoSignIn);
        imageIcon.setImageResource(R.drawable.logo);

        final EditText editUserName = findViewById(R.id.etUserEmail);
        final EditText editPassword = findViewById(R.id.etUserPassword);

        Button signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                GetPasswordTask getPasswordTask = new GetPasswordTask();
                //set an image
                String userName = editUserName.getText().toString();
                HashPassword hashpassword = new HashPassword();
                String password = editPassword.getText().toString();
                password = hashpassword.getMd5(password);
                String[] para = new String[2];
                para[0] = userName;
                para[1] = password;
                if(!userName.isEmpty() && !password.isEmpty()){
                    getPasswordTask.execute(userName,password);
                }



            }
        });


        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

    }




    private class GetPasswordTask extends AsyncTask<String,Void, Person>{
        @Override
        protected Person doInBackground(String... params){
            String username = params[0];
            String password = params[1];
            Person result = networkConnection.getOneCredential(username,password);
            if(result == null){
                boolean exist = networkConnection.checkExist(username);
                if(exist == false){
                    result = new Person(0);  // does not exist
                } else {
                    result = new Person(-1);  // wrong password
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Person results){
            person = results;
            if(person.getPersonId() == 0){
                Toast.makeText(getApplicationContext(),"this person does not exist, sign up first",Toast.LENGTH_LONG).show();
            } else if(person.getPersonId() == -1){
                Toast.makeText(getApplicationContext(),"wrong password, enter again",Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(SignIn.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("person",person);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }
}
