package com.darthsanches.mappointsmaker.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.darthsanches.mappointsmaker.App;
import com.darthsanches.mappointsmaker.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by alexandroid on 02.08.2016.
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.user_name_field)
    EditText userNameField;
    @BindView(R.id.password_field)
    EditText passwordField;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login,container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this,view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.button_connect)
    public void onConnectClick(){
        ((App) getActivity().getApplicationContext()).bindService();
    }
}
