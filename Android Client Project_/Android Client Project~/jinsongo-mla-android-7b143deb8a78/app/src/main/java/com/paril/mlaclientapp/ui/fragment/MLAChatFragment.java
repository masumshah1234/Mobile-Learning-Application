package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.security.KeyStore;
import java.security.KeyStoreException;
//import java.security.PrivateKey;
//import java.security.PublicKey;

import java.security.PrivateKey;
import javax.crypto.BadPaddingException;


import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.ContactChip;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.model.MLAInstructorDetails;
import com.paril.mlaclientapp.model.MLAStudentDetails;

import com.paril.mlaclientapp.model.MLAUserWithCheckbox;
import com.paril.mlaclientapp.model.MlaChat;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.webservice.Api;
import com.pchmn.materialchips.ChipsInput;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.io.IOException;
import java.security.cert.CertificateException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.security.UnrecoverableEntryException;




import retrofit2.Call;
import retrofit2.Response;

import static java.lang.Thread.sleep;


/**
 * Created by paril on 7/16/2017.
 */
public class MLAChatFragment extends Fragment {

    View view;
    ChipsInput chipsInput;
    Button btnsend;
    String message;
    EditText message_view;
    TextView receiver_id;
    TextView sender_id;
    Calendar calendar;
    SimpleDateFormat md;
    String receiver;
    String sender;
    PublicKey publickey;
    String userName;
    PrivateKey privateKey;
    String temp;
    String message_id;
    MLAAdminDetails adminUserDetails = new MLAAdminDetails();
    MLAInstructorDetails instructorDetails = new MLAInstructorDetails();
    MLAStudentDetails studentDetails = new MLAStudentDetails();
    String userType;
    String SenderUserName;
    String receiverUN;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_messanger, container, false);
        setHasOptionsMenu(true);
        chipsInput = (ChipsInput) view.findViewById(R.id.chips_input);
        MLAGetAllUsersDetailsAPI getUserDetails = new MLAGetAllUsersDetailsAPI(getActivity());
        getUserDetails.execute();
        MLAGetUserInformationAPI mlaGetUserInformationAPI = new MLAGetUserInformationAPI();
        Bundle bundle = getActivity().getIntent().getExtras();
        mlaGetUserInformationAPI.execute(bundle.get("userName").toString(),bundle.get("userType").toString());
       // userName=bundle.get("userName").toString();
        btnsend = (Button) view.findViewById(R.id.mla_chat_send);
        message_view = (EditText) view.findViewById(R.id.mla_message_body);
        MLAGetAllUsersDetailsAPI mlaGetAllUsersDetailsAPI = new MLAGetAllUsersDetailsAPI(getActivity());
        mlaGetAllUsersDetailsAPI.execute();


        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = message_view.getText().toString();
                List<ContactChip> contactselected = (List<ContactChip>) chipsInput.getSelectedChipList();
                receiver = contactselected.get(0).getEmail();
                userName=contactselected.get(0).getLabel();
                String[] str = receiver.split(" ");
                receiverUN= str[0];
                Log.d("receiverUN",receiver);
               // userName=contactselected.get(0).getLabel();
                //sender = contactselected.get(0).getEmail();

                try{
                    KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                    keyStore.load(null);
                    if (keyStore.containsAlias(userName)) {
                        KeyStore.Entry entry = keyStore.getEntry(userName, null);
                        privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
                        publickey = keyStore.getCertificate(userName).getPublicKey();
                        Log.d("public key",publickey+"");
                    }
                }
                catch (UnrecoverableEntryException e){
                    e.printStackTrace();
                }
                catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
               /* catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }*/
                catch (CertificateException e){
                    e.printStackTrace();
                }
                catch (KeyStoreException e){
                    e.printStackTrace();
                }

                try {


                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(Cipher.ENCRYPT_MODE,publickey);
                    byte [] encodedbytes = cipher.doFinal(message.getBytes());
                    message = Base64.encodeToString(encodedbytes,Base64.DEFAULT);
                    Log.d("encoded message",message);


                }
                catch (IllegalBlockSizeException e){
                    e.printStackTrace();
                }
                catch (BadPaddingException e ){
                    e.printStackTrace();
                }
                catch (NoSuchPaddingException e){
                    e.printStackTrace();
                }catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }catch (InvalidKeyException e){
                    e.printStackTrace();
                }


                calendar = Calendar.getInstance();
                md = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

                MLAChatFragmentAPI mlaChatFragmentAPI = new MLAChatFragmentAPI (MLAChatFragment.this.getActivity());
                mlaChatFragmentAPI.execute();

            }
        });
        return view;
    }


    class MLAGetUserInformationAPI extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void result) {
            if (userType != null && userType.equals("admin")) {

                //txtUserId.setText(adminUserDetails.userId+"");
                /*txtUserName.setText(adminUserDetails.getIdAdmin());
                txtFirstName.setText(adminUserDetails.firstName);
                txtLastName.setText(adminUserDetails.lastName);
                txtEmailId.setText(adminUserDetails.emailId);
                txtTelephone.setText(adminUserDetails.telephone);
                txtAliasMailId.setText(adminUserDetails.aliasMailId);
                txtAddress.setText(adminUserDetails.address);*/
                SenderUserName = adminUserDetails.userName;
                sender = adminUserDetails.emailId;

                //txtHangoutId.setText(adminUserDetails.skypeId);
            } else if (userType != null && userType.equals("student")) {

                //txtUserId.setText(studentDetails.userId+"");
               /* txtUserName.setText(studentDetails.getIdStudent());
                txtFirstName.setText(studentDetails.firstName);
                txtLastName.setText(studentDetails.lastName);
                txtEmailId.setText(studentDetails.emailId);
                txtTelephone.setText(studentDetails.telephone);
                txtAliasMailId.setText(studentDetails.aliasMailId);
                txtAddress.setText(studentDetails.address);*/
               SenderUserName = studentDetails.userName;
                sender = studentDetails.emailId;
                //txtHangoutId.setText(studentDetails.skypeId);
            } else if (userType != null && userType.equals("instructor")) {

                //txtUserId.setText(instructorDetails.userId+"");
                /*txtUserName.setText(instructorDetails.getIdInstructor());
                txtFirstName.setText(instructorDetails.firstName);
                txtLastName.setText(instructorDetails.lastName);
                txtEmailId.setText(instructorDetails.emailId);
                txtTelephone.setText(instructorDetails.telephone);
                txtAliasMailId.setText(instructorDetails.aliasMailId);
                txtAddress.setText(instructorDetails.address);*/
               // SenderUserName = instructorDetails.userName;
                sender = instructorDetails.emailId;
                //txtHangoutId.setText(instructorDetails.skypeId);

            }
           // Toast.makeText(getActivity(),"info recieved" + SenderUserName,Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(String... params) {
            userType = params[1];
            if (params[1].equals("admin")) {
                try {
                    Call<List<MLAAdminDetails>> callAdminData = Api.getClient().getAdminInfo(params[0]);
                    Response<List<MLAAdminDetails>> responseAdminData = callAdminData.execute();
                    if (responseAdminData.isSuccessful() && responseAdminData.body() != null && responseAdminData.body().size() > 0) {
                        adminUserDetails = responseAdminData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            } else if (params[1].equals("student")) {
                try {
                    Call<List<MLAStudentDetails>> callStudentData = Api.getClient().getStudentInfo(params[0]);
                    Response<List<MLAStudentDetails>> responseStudentData = callStudentData.execute();
                    if (responseStudentData.isSuccessful() && responseStudentData.body() != null && responseStudentData.body().size() > 0) {
                        studentDetails = responseStudentData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            } else if (params[1].equals("instructor")) {
                try {
                    Call<List<MLAInstructorDetails>> callInstData = Api.getClient().getInstInfo(params[0]);
                    Response<List<MLAInstructorDetails>> responseInstData = callInstData.execute();
                    if (responseInstData.isSuccessful() && responseInstData.body() != null && responseInstData.body().size() > 0) {
                        instructorDetails = responseInstData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }

            return null;
        }

    }


    class MLAGetAllUsersDetailsAPI extends AsyncTask<Void, Void, List<ContactChip>> {
        Context context;

        public MLAGetAllUsersDetailsAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting User Details...");
        }

        @Override
        protected void onPostExecute(List<ContactChip> userDetails) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();
            //check if the call to api passed
            if (userDetails != null) {
                chipsInput.setFilterableList(userDetails);
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_new_message_coordinatorLayout));
            }
        }

        @Override
        protected List<ContactChip> doInBackground(Void... params) {
            List<ContactChip> listContactChip = new ArrayList<ContactChip>();

            try {
                Call<List<MLAAdminDetails>> callAdminUserData = Api.getClient().getAdminUsers();
                Response<List<MLAAdminDetails>> responseAdminUser = callAdminUserData.execute();
                if (responseAdminUser.isSuccessful() && responseAdminUser.body() != null) {
                    for (MLAAdminDetails adminUserDetail : responseAdminUser.body()
                    ) {
                        ContactChip contactChip = new ContactChip(adminUserDetail.getIdAdmin(), adminUserDetail.getEmailId(),adminUserDetail.getIdAdmin(), "Admin");
                        listContactChip.add(contactChip);
                    }
                }


                Call<List<MLAInstructorDetails>> callInstUserData = Api.getClient().getInstructors();
                Response<List<MLAInstructorDetails>> responseInstUser = callInstUserData.execute();
                if (responseInstUser.isSuccessful() && responseInstUser.body() != null) {
                    for (MLAInstructorDetails instUserDetail : responseInstUser.body()
                    ) {
                        ContactChip contactChip = new ContactChip(instUserDetail.getIdInstructor(), instUserDetail.getEmailId(),instUserDetail.getIdInstructor(), "Instructor");
                        listContactChip.add(contactChip);
                    }
                }

                Call<List<MLAStudentDetails>> callStudentUserData = Api.getClient().getStudents();
                Response<List<MLAStudentDetails>> responseStudentUser = callStudentUserData.execute();
                if (responseStudentUser.isSuccessful() && responseStudentUser.body() != null) {
                    for (MLAStudentDetails studentUserDetail : responseStudentUser.body()
                    ) {
                        ContactChip contactChip = new ContactChip(studentUserDetail.getIdStudent(),studentUserDetail.getEmailId(),studentUserDetail.getIdStudent(), "Student");
                        listContactChip.add(contactChip);
                    }
                }
                return listContactChip;
            } catch (MalformedURLException e) {
                return null;

            } catch (IOException e) {
                return null;
            }
        }

    }

    class MLAChatFragmentAPI extends AsyncTask<Void, Void, String> {
        Context context;

        public MLAChatFragmentAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting User Details...");
        }

        @Override
        protected void onPostExecute(String status ) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();
            //check if the call to api passed
            if (status.equals("sent")) {

                ((MLAHomeActivity) getActivity()).showSnackBar("Success", view.findViewById(R.id.fragment_chat_message_coordinatorLayout));
            }  else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_chat_message_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            final MlaChat postdata = new MlaChat();
            try {
                Call<MlaChat> mlachat = Api.getClient().postmessage(md.format(calendar.getTime()),message,receiver, sender );
                Response<MlaChat> chatResponse = mlachat.execute();
            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                return null;


            }
            return "sent";

        }
    }
}
