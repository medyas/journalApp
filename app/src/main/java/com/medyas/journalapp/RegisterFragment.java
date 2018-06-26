/*
 * Copyright 2018 Medyas Journal App
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.medyas.journalapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends Fragment {

    private TextView login;
    private Button signup;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText passConf;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        login = (TextView) rootView.findViewById(R.id.login);
        signup = (Button) rootView.findViewById(R.id.btn_create_account);
        username = (EditText) rootView.findViewById(R.id.editTextUsername);
        email = (EditText) rootView.findViewById(R.id.editTextEmailSignup);
        password = (EditText) rootView.findViewById(R.id.editTextPassSignup);
        passConf = (EditText) rootView.findViewById(R.id.editTextPassSignupConf);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = email.getText().toString(), p = password.getText().toString(),
                        u = username.getText().toString(), c = passConf.getText().toString();

                if(u.isEmpty() || u.length() < 5) {
                    username.setError("5 Characters minimum");
                } else if(e.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                    email.setError("Provide a valid Email Address!");
                } else if(p.isEmpty() || c.isEmpty() || !c.equals(p) || p.length() < 5) {
                    password.setError("Provide a valid password");
                } else {
                    mListener.createAccount(u, e, p);
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeFragment(0);
            }
        });

        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void changeFragment(int c);
        void createAccount(String username, String email, String password);
    }
}
