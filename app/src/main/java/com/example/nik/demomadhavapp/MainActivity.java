package com.example.nik.demomadhavapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    String serverAuthCode;
    Session session;
    ProgressDialog progressDialog;
    Context context;
    String Sfrom, Smsg, Ssub;
    String name, email;
    String Sto = "guptanik1231@gmail.com";
    boolean flag;
    String custNamestr, custLocationstr, custNumberstr, custEmailstr, custDepartmentstr, discussionstr, actionplanstr, timelinestr, agentRemarkstr;
    EditText custName, custLocation, custNumber, custEmail, custDepartment, discussion, actionplan, timeline, agentRemark;
    TextView contactPerson;
    Button submit;
    GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 7727;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        custName = (EditText) findViewById(R.id.custName);
        custLocation = (EditText) findViewById(R.id.custLocation);
        contactPerson = (TextView) findViewById(R.id.contactPerson);
        custNumber = (EditText) findViewById(R.id.custNumber);
        custEmail = (EditText) findViewById(R.id.custEmail);
        custDepartment = (EditText) findViewById(R.id.custDepartment);
        discussion = (EditText) findViewById(R.id.discussion);
        actionplan = (EditText) findViewById(R.id.actionPlan);
        timeline = (EditText) findViewById(R.id.timeline);
        agentRemark = (EditText) findViewById(R.id.agentRemark);

        submit = (Button) findViewById(R.id.subnit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flag = false;

                custNamestr = custName.getText().toString();
                custLocationstr = custLocation.getText().toString();
                custNumberstr = custNumber.getText().toString();
                custEmailstr = custEmail.getText().toString();
                custDepartmentstr = custDepartment.getText().toString();
                discussionstr = discussion.getText().toString();
                actionplanstr = actionplan.getText().toString();
                timelinestr = timeline.getText().toString();
                agentRemarkstr = agentRemark.getText().toString();

                if (custNamestr.isEmpty()) {
                    custName.setError("Enter valid Name");
                    flag = true;
                }
                if (custLocationstr.isEmpty()) {
                    custLocation.setError("Enter valid Loaction");
                    flag = true;
                }
                if (custNumberstr.isEmpty()) {
                    custNumber.setError("Enter valid Number");
                    flag = true;
                }
                if (custEmailstr.isEmpty()) {
                    custEmail.setError("Enter valid EmailId");
                    flag = true;
                }
                if (custDepartmentstr.isEmpty()) {
                    custDepartment.setError("Enter valid Department Name");
                    flag = true;
                }
                if (discussionstr.isEmpty()) {
                    discussion.setError("Enter discussion points");
                    flag = true;
                }
                if (actionplanstr.isEmpty()) {
                    actionplan.setError("Enter future scope");
                    flag = true;
                }
                if (timelinestr.isEmpty()) {
                    timeline.setError("Enter valid date");
                    flag = true;
                }

                if (flag == false) {
                    new AlertDialog.Builder(MainActivity.this).setTitle("Info").setMessage("Are you sure to submit changes").setNegativeButton("No", null).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // mail sending
                            Sfrom = email;
                            Ssub = "Report from" + name;
                            Smsg = custNumberstr + "\n" + custLocationstr + "\n" + custNumberstr + "\n" + custEmailstr + "\n" + custDepartmentstr + "\n" + discussionstr + "\n" + actionplanstr + "\n" + timelinestr;
                            // msg = "Subject : " + Ssub + "\n" + "Message : " + Smsg;
                            //String[] to = {Sfrom, Sto};
                            //String[] cc = {Sfrom, Scc};
                            //String[] bcc = {Sfrom, Sbcc};
                            sendemail(Sto, Sfrom, Ssub, Smsg);

                        }
                    }).create().show();
                }

            }
        });


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);


    }

    private void sendemail(String to, String sfrom, String sub, String msg) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "465");
        session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return super.getPasswordAuthentication();
            }
        });
        progressDialog = ProgressDialog.show(context, "", "Sending mail....", true);
        progressDialog.setCancelable(false);
        RetriveFeedTask task = new RetriveFeedTask();
        task.execute();
    }

    class RetriveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(email));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(Sto));
                message.setSubject(Ssub);
                message.setContent(Smsg, "text/html; charset=utf-8");

                Transport.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Toast.makeText(context, "message sent", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleresult(result);
        }
    }

    private void handleresult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            name = account.getDisplayName();
            email = account.getEmail();
            serverAuthCode = account.getServerAuthCode();
            Toast.makeText(this, "Name:  " + name, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Email ID:  " + email, Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Code: " + serverAuthCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
