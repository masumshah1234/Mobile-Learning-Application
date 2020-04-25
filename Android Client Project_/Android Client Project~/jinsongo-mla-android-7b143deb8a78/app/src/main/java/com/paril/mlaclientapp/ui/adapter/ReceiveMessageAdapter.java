package com.paril.mlaclientapp.ui.adapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.model.MlaChat;
import com.paril.mlaclientapp.ui.view.CustomSwipeLayout;
import java.security.KeyStoreException;
import  java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.UnrecoverableEntryException;
import javax.crypto.IllegalBlockSizeException;

import java.security.KeyStore;
import java.util.List;
import javax.crypto.NoSuchPaddingException;
import java.security.PublicKey;
import java.security.PrivateKey;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;

import javax.crypto.Cipher;


public class ReceiveMessageAdapter extends RecyclerSwipeAdapter<ReceiveMessageAdapter.SimpleViewHolder> {

    public Context mContext;
    public List<MlaChat> mDataset;

    public CustomSwipeLayout swipeLayout;
    boolean isSwipable=false;
    TextView txtName, txtDate, txtMessage;
    int i=0;
    String username;
    String message;
    PrivateKey privateKey;
    PublicKey publicKey;



    OnItemClickListener<MlaChat> listener;

    public ReceiveMessageAdapter(String username,Context context, List<MlaChat> objects,boolean isSwipable, OnItemClickListener<MlaChat> onItemClickListenerener) {
        this.username=username;
        this.mContext = context;
        this.mDataset = objects;
        this.listener = onItemClickListenerener;
        this.isSwipable=isSwipable;

    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String RSADecrypt( String message,PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String encryptedMessage = message;
        byte[] encryptedBytes=Base64.decode(encryptedMessage, Base64.DEFAULT);

        Log.d("privateKey",privateKey+"");
        Cipher cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher1.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedBytes = cipher1.doFinal(encryptedBytes);
        String decrypted = new String(decryptedBytes);
        Log.d("decrypted String",decrypted);
        return decrypted;
    }



    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (CustomSwipeLayout) itemView.findViewById(R.id.displaymessage);
            txtName = (TextView) itemView.findViewById(R.id.receive_name);
            txtDate = (TextView) itemView.findViewById(R.id.receive_date);
            txtMessage = (TextView) itemView.findViewById(R.id.receive_message);
        }
            public void bind(final MlaChat item, final OnItemClickListener listener) {
                swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
                swipeLayout.setRightSwipeEnabled(isSwipable);
                swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                    @Override
                    public void onOpen(SwipeLayout layout) {
                    }
                });
                swipeLayout.setOnClickItemListener(new CustomSwipeLayout.OnClickItemListener() {
                    @Override
                    public void onClick(View view) {

                        listener.onItemClick(item,R.id.displaymessage);
                    }
                });

                txtName.setText(item.getSender());
                //Log.d("sender",mDataset.get(i).getSender());
                txtDate.setText(item.getMessagedate());
                //Log.d("date",mDataset.get(i).getMessagedate());
               // txtMessage.setText(item.getText());
                //Log.d("msg",mDataset.get(i).getText());
                try {
                    KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                    keyStore.load(null);
                    if (keyStore.containsAlias(username)) {
                        KeyStore.Entry entry = keyStore.getEntry(username, null);
                        privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
                        //publicKey = keyStore.getCertificate(username).getPublicKey();

                    }
//                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//                    cipher.init(Cipher.DECRYPT_MODE,privateKey);
//                    //final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
//                    byte [] decodedbytes = cipher.doFinal(Base64.decode(item.getText(), Base64.DEFAULT));
//                    message=new String(decodedbytes);
                    message = RSADecrypt(item.getText(),privateKey);

                    txtMessage.setText(message);


                }catch (IOException | KeyStoreException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
                    e.printStackTrace();
                }
                catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }
                catch (CertificateException e){
                    e.printStackTrace();
                }
                catch (UnrecoverableEntryException e){
                    e.printStackTrace();
                }

            }

    }


    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mla_receive, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        MlaChat item = mDataset.get(position);
        viewHolder.bind(item, listener);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.displaymessage;
    }
}



