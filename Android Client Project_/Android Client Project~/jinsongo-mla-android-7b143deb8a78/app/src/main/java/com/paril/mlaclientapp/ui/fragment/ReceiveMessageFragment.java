package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.ContactChip;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.model.MlaChat;
import com.paril.mlaclientapp.model.MLAInstructorDetails;
import com.paril.mlaclientapp.model.MLARegisterUsers;
import com.paril.mlaclientapp.model.MLAStudentDetails;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.activity.MLAViewStudentActivity;
import com.paril.mlaclientapp.ui.adapter.ReceiveMessageAdapter;
import com.paril.mlaclientapp.ui.adapter.MLAStudentAdapter;
import com.paril.mlaclientapp.ui.adapter.OnItemClickListener;
import com.paril.mlaclientapp.ui.view.EmptyRecyclerView;
import com.paril.mlaclientapp.util.CommonUtils;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.util.VerticalSpaceItemDecoration;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static java.lang.Thread.sleep;

/**
 * Created by paril on 7/12/2017.
 */
public class ReceiveMessageFragment extends Fragment {
    EmptyRecyclerView recyclerViewUsers;
    ArrayAdapter<String> adapter;
    ReceiveMessageAdapter receiveMessageAdapter;
    View view;
    PrefsManager prefsManager;
    public String receiver;
    String userName;
    MLAAdminDetails adminUserDetails =new MLAAdminDetails();
    MLAInstructorDetails instructorDetails=new MLAInstructorDetails();
    MLAStudentDetails studentDetails=new MLAStudentDetails();
    String userType;
    String temp;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.second_xml, container, false);
        recyclerViewUsers = (EmptyRecyclerView) view.findViewById(R.id.mla_admin1_display_recyyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewUsers.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.divider_list)));

        MLAGetUserInformationAPI getFromUserDetails=new MLAGetUserInformationAPI(getActivity());
        Bundle bundle = getActivity().getIntent().getExtras();
        getFromUserDetails.execute(bundle.get("userName").toString(),bundle.get("userType").toString());
        userName=bundle.get("userName").toString();




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    class MLAGetUserInformationAPI extends AsyncTask<String,Void,Void>
    {
        Context context;
        public MLAGetUserInformationAPI(Context ctx) {
            context = ctx;
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void result) {
            if(userType != null  && userType.equals("admin") ) {

                receiver= adminUserDetails.emailId;
                userName = adminUserDetails.idAdmin;

            }else if(userType != null  && userType.equals("student") ) {

                receiver= studentDetails.emailId;
                userName = studentDetails.idStudent;
            }else if(userType != null  && userType.equals("instructor") ) {

                receiver= instructorDetails.emailId;
                userName = instructorDetails.idInstructor;
            }
            Toast.makeText(context,"1 "+receiver,Toast.LENGTH_LONG).show();
            if (CommonUtils.checkInternetConnection(getActivity())) {
                MLAGetAllChatAPI mlaGetAllChatAPI = new MLAGetAllChatAPI(context);
                mlaGetAllChatAPI.execute();
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.check_connection), view.findViewById(R.id.fragment_display_admin1_coordinatorLayout));
            }

        }

        @Override
        protected Void doInBackground(String... params) {
            userType=params[1];
            if(params[1].equals("admin")) {
                try {
                    Call<List<MLAAdminDetails>> callAdminData = Api.getClient().getAdminInfo(params[0]);
                    Response<List<MLAAdminDetails>> responseAdminData = callAdminData.execute();
                    if (responseAdminData.isSuccessful() && responseAdminData.body() != null&&responseAdminData.body() .size()>0) {
                        adminUserDetails= responseAdminData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }
            else if(params[1].equals("student"))
            {
                try {
                    Call<List<MLAStudentDetails>> callStudentData = Api.getClient().getStudentInfo(params[0]);
                    Response<List<MLAStudentDetails>> responseStudentData = callStudentData.execute();
                    if (responseStudentData.isSuccessful() && responseStudentData.body() != null&&responseStudentData.body() .size()>0) {
                        studentDetails= responseStudentData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }
            else if(params[1].equals("instructor"))
            {
                try {
                    Call<List<MLAInstructorDetails>> callInstData = Api.getClient().getInstInfo(params[0]);
                    Response<List<MLAInstructorDetails>> responseInstData = callInstData.execute();
                    if (responseInstData.isSuccessful() && responseInstData.body() != null&&responseInstData.body() .size()>0) {
                        instructorDetails= responseInstData.body().get(0);
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


    class MLAGetAllChatAPI extends AsyncTask<Void, Void, List<MlaChat>> {
        Context context;


        public MLAGetAllChatAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting Messages...");

            Toast.makeText(context,"2 "+receiver,Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onPostExecute(List<MlaChat> mlaChats) {

            ((MLAHomeActivity) getActivity()).hideProgressDialog();
            List<MlaChat> mlaChats1 = new ArrayList<MlaChat>();
            if (mlaChats != null) {
                mlaChats1 = mlaChats;
            } else {
                Toast.makeText(getActivity(),"Error while getting message",Toast.LENGTH_LONG).show();
                //((MLAHomeActivity) getActivity()).showSnackBar("Error while getting message", getView().findViewById(R.id.fragment_display_admin_coordinatorLayout));
            }

            receiveMessageAdapter= new ReceiveMessageAdapter(userName,context, mlaChats1,true, new OnItemClickListener<MlaChat>() {
                @Override
                public void onItemClick(MlaChat item, int resourceId) {
                    Log.d("OnItemClick", "resource:" + resourceId);
                    if (resourceId == R.id.receiver_imgEditUser) {
                        final Intent intent = new Intent(getActivity(), MLAChatFragment.class);
                        intent.putExtra(CommonUtils.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtils.EXTRA_EDIT_MODE, true);
                       // intent.putExtra(CommonUtils.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
//                    } else if (resourceId == R.id.receiver_imgDeleteUser) {
//                        if (CommonUtils.checkInternetConnection(getActivity())) {
//                            MLADeleteMessageAPI deleteMessageAPI = new MLADeleteMessageAPI(ReceiveMessageFragment.this.getActivity());
//                            deleteMessageAPI.execute(item.getSender());
//                        } else {
//                            ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.check_connection), view.findViewById(R.id.fragment_display_admin_coordinatorLayout));
//                        }

                    } else if (resourceId == R.id.displaymessage) {
                        final Intent intent = new Intent(getActivity(), MLAChatFragment.class);
                        intent.putExtra(CommonUtils.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtils.EXTRA_EDIT_MODE, false);
                        //intent.putExtra(CommonUtils.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    }

                }
            });
            receiveMessageAdapter.setMode(Attributes.Mode.Single);
            recyclerViewUsers.setAdapter(receiveMessageAdapter);
            recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_admin1_relEmptyView));
        }

        @Override
        protected List<MlaChat> doInBackground(Void... params) {

            try {
                //Log.d("receiver",receiver);

                Call<List<MlaChat>> callAdminUserData = Api.getClient().getMessage(receiver);
                Response<List<MlaChat>> responseAdminUser = callAdminUserData.execute();
                if (responseAdminUser.isSuccessful() && responseAdminUser.body() != null) {
                    return responseAdminUser.body();
                } else {
                    return null;
                }

            } catch (MalformedURLException e) {
                return null;

            } catch (IOException e) {
                return null;
            }
        }
    }

    class MLADeleteMessageAPI extends AsyncTask<String, Void, String> {
        Context context;

        public MLADeleteMessageAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Removing Student...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            if (statusCode != null && statusCode.equals("302")) //the tasks are deleted
            {
                ((MLAHomeActivity) getActivity()).showSnackBar("Student User has been removed.", getView().findViewById(R.id.fragment_display_admin1_coordinatorLayout));
                MLAGetAllChatAPI mlaGetAllChatAPI = new MLAGetAllChatAPI(ReceiveMessageFragment.this.getActivity());
                mlaGetAllChatAPI.execute();
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_display_admin1_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Call<String> callDelete = Api.getClient().removeAdmin(params[0]);
            try {
                Response<String> responseDelete = callDelete.execute();
                if (responseDelete != null ) {
                    return responseDelete.code() + "";

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
