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


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    private Button login;
    private EditText email;
    private EditText password;
    private TextView newaccount;


    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        login = (Button) rootView.findViewById(R.id.btn_login);
        email = (EditText) rootView.findViewById(R.id.editTextEmailLogin);
        password = (EditText) rootView.findViewById(R.id.editTextPassLogin);
        newaccount = (TextView) rootView.findViewById(R.id.createNew);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String e=email.getText().toString();
                String p = password.getText().toString();

                if(e.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                    email.setError("Enter a valid Email Address!");
                }
                else  if (p.isEmpty() || p.length() < 4 || p.length() > 10) {
                    password.setError("between 4 and 10 alphanumeric characters");
                }
                else {
                    email.setError(null);
                    password.setError(null);
                    mListener.loginUser(e, p);
                }
            }
        });

        newaccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.changeFragment(1);
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
        void loginUser(String email, String pass);
    }
}
