package com.sab.notesandimages.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sab.notesandimages.R;
import com.sab.notesandimages.adapters.NotesListAdapter;
import com.sab.notesandimages.databinding.FragmentNotesListBinding;
import com.sab.notesandimages.models.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesListFragment extends Fragment {

    FragmentNotesListBinding fragmentNotesListBinding;
    NotesListAdapter adapter;
    FirebaseFirestore database;
    FirebaseUser user;
    FirebaseAuth mAuth;
    CollectionReference collectionReference;
    List<Note> notesList = new ArrayList<>();
    private static final String TAG = "MomentsListFragment";


    public NotesListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        collectionReference = database.collection("Notes");
        user = getArguments().getParcelable("firebaseUser");
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mAuth.signOut();
        Navigation.findNavController(getView())
                .navigate(R.id.action_notesListFragment_to_authorizationFragment);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", user.getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Note note = snapshot.toObject(Note.class);
                            notesList.add(note);
                        }

                        Collections.sort(notesList, (o1, o2) -> o2.getTimeAdded().compareTo(o1.getTimeAdded()));

                        adapter = new NotesListAdapter();
                        adapter.setNotesList(notesList);
                        fragmentNotesListBinding.rvList.setLayoutManager(new LinearLayoutManager(getContext()));
                        fragmentNotesListBinding.rvList.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), user.getUid() + " " + notesList.size(), Toast.LENGTH_LONG).show();
                    }

                }).addOnFailureListener(e -> {

        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentNotesListBinding = FragmentNotesListBinding.inflate(inflater, container, false);
        fragmentNotesListBinding.addFloatingButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            FirebaseUser mUser = mAuth.getCurrentUser();
            bundle.putParcelable("firebaseUser", mUser);
            Log.d(TAG, "onClick: " + mUser.getUid());
            Navigation.findNavController(v).navigate(R.id.action_notesListFragment_to_noteSettingsFragment, bundle);
        });
        return fragmentNotesListBinding.getRoot();
    }
}
