package com.sab.notesandimages.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sab.notesandimages.R;
import com.sab.notesandimages.databinding.FragmentAuthorizationBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthorizationFragment extends Fragment {
    FragmentAuthorizationBinding authorizationBinding;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public AuthorizationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("firebaseUser", mAuth.getCurrentUser());
            Navigation.findNavController(getView()).navigate(R.id.action_authorizationFragment_to_notesListFragment, bundle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        authorizationBinding = FragmentAuthorizationBinding.inflate(inflater, container,
                false);

        authorizationBinding.registrationBtn.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_authorizationFragment_to_registrationFragment));

        authorizationBinding.loginBtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(authorizationBinding.emailText.getText().toString())
                    && !TextUtils.isEmpty(authorizationBinding.passwordText.getText().toString())) {
                authorizationBinding.progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(authorizationBinding.emailText.getText().toString(),
                        authorizationBinding.passwordText.getText().toString()).addOnSuccessListener(authResult -> {
//                    Toast.makeText(getContext(), authResult.getUser().getUid(), Toast.LENGTH_SHORT).show();
                    user = mAuth.getCurrentUser();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("firebaseUser", user);
                    authorizationBinding.progressBar.setVisibility(View.GONE);
                    Navigation.findNavController(v).navigate(R.id.action_authorizationFragment_to_noteSettingsFragment, bundle);
                })
                        .addOnFailureListener(e -> {
                            authorizationBinding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Something went wrong. Cannot login.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                authorizationBinding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        if (user != null)
            Toast.makeText(getContext(), user.getUid(), Toast.LENGTH_SHORT).show();
        return authorizationBinding.getRoot();
    }
}
