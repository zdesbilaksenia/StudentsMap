package com.example.studentmap;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.studentmap.Network.Entity.User;
import com.example.studentmap.Network.RequestMakerModel;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.OkHttpClient;


public class Registration extends Fragment {

    OkHttpClient client;
    TextInputEditText newLogin;
    TextInputEditText newPassword;
    Button enterBtn;
    Button registrationBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration,
                container, false);

        newLogin = view.findViewById(R.id.new_login);
        newPassword = view.findViewById(R.id.new_password);
        enterBtn = view.findViewById(R.id.btn_to_enter);
        registrationBtn = view.findViewById(R.id.btn_registration);

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login login = new Login();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, login, "Login").commit();
            }
        });


        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setLogin(newLogin.getText().toString());
                user.setPassword(newPassword.getText().toString());
                registration(user);
            }
        });

        return view;
    }

    void registration(User user){
        RequestMakerModel model = ViewModelProviders.of(this).get(RequestMakerModel.class);
        LiveData<Integer> data = model.getDataInt();
        client = new OkHttpClient();

        model.addUser(client,user);

        data.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 1){
                    // Переходим на карту.
                }else Toast.makeText(getContext(), "Ошибка создания учетной записи!!!", Toast.LENGTH_SHORT).show();
                Log.d("Answer", Integer.toString(integer));
            }
        });

    }
}