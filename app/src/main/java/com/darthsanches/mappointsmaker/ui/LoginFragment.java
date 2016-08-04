package com.darthsanches.mappointsmaker.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.darthsanches.mappointsmaker.App;
import com.darthsanches.mappointsmaker.R;
import com.darthsanches.mappointsmaker.bus.LoginEvent;
import com.darthsanches.mappointsmaker.bus.LoginFailureEvent;
import com.darthsanches.mappointsmaker.socket.SocketService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

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

    @Inject
    Bus bus;

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.component(getActivity()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.toolbar_title_login);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @OnClick(R.id.button_connect)
    public void onConnectClick() {
        if (!((MainActivity)getActivity()).isMyServiceRunning(SocketService.class)) {
            Intent intent = new Intent(getActivity(), SocketService.class);
            intent.putExtra("username", userNameField.getText().toString());
            intent.putExtra("password", passwordField.getText().toString());
            getActivity().startService(intent);
        }
        ((MainActivity) getActivity()).openMapFragment();
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        ((MainActivity) getActivity()).openMapFragment();
    }

    @Subscribe
    public void onLoginFailure(LoginFailureEvent event) {
        userNameField.setText("");
        passwordField.setText("");
        Toast toast = Toast.makeText(getActivity(),
                R.string.login_failed, Toast.LENGTH_SHORT);
        toast.show();
    }
}
