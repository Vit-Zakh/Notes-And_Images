package com.sab.notesandimages.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sab.notesandimages.R;
import com.sab.notesandimages.databinding.FragmentNoteSettingsBinding;
import com.sab.notesandimages.models.Note;

import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteSettingsFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_INTENT_CODE = 777;
    private static final String NO_NICKNAME = "NICKNAME_404";
    private static final String TAG = "NotesSettingsFragment";

    private FragmentNoteSettingsBinding settingsBinding;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore database;

    private CollectionReference collectionReference;
    private CollectionReference userCollectionReference;
    private StorageReference storageReference;
    private String nickname;
    private Uri imageUri;
    private Note note;

    public NoteSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("firebaseUser", user);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        collectionReference = database.collection("Notes");
        userCollectionReference = database.collection("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
        nickname = bundle.getString("nickname", NO_NICKNAME);
        user = mAuth.getCurrentUser();
        setHasOptionsMenu(true);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        user = getArguments().getParcelable("firebaseUser");
        Log.d(TAG, "onCreateView: " + user.getUid());
        settingsBinding = FragmentNoteSettingsBinding.inflate(inflater, container, false);
        settingsBinding.cameraImage.setOnClickListener(this::onClick);
        settingsBinding.saveNoteButton.setOnClickListener(this::onClick);
        if (nickname.equals(NO_NICKNAME) && user != null) {
            user = mAuth.getCurrentUser();
            userCollectionReference.whereEqualTo("userId", user.getUid())
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                            nickname = snapshot.getString("nickname");
                        settingsBinding.nicknameSettings.setText(nickname);
                    });
        } else {
            settingsBinding.nicknameSettings.setText(nickname);
        }
        return settingsBinding.getRoot();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_image:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_INTENT_CODE);
                break;
            case R.id.saveNoteButton:
                if (!TextUtils.isEmpty(settingsBinding.titleText.getText().toString())
                        && !TextUtils.isEmpty(settingsBinding.descriptionText.getText().toString())
                        && imageUri != null) {

                    settingsBinding.progressBarSettings.setVisibility(View.VISIBLE);

                    StorageReference imagePath = storageReference.child("notes_images")
                            .child("note_" + Timestamp.now().getSeconds());
                    imagePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imagePath.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                note = new Note();
                                note.setUserId(user.getUid());
                                note.setNickname(nickname);
                                note.setTitle(settingsBinding.titleText.getText().toString());
                                note.setDescription(settingsBinding.descriptionText.getText()
                                        .toString());
                                note.setImageUrl(uri.toString());
                                note.setTimeAdded(new Timestamp(new Date()));
                                collectionReference.add(note);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("note", note);
                                bundle.putParcelable("firebaseUser", user);
                                Navigation.findNavController(v).
                                        navigate(R.id.action_noteSettingsFragment_to_notesListFragment, bundle);

                            }));
                } else
                    Toast.makeText(getContext(), "Something's missing", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT_CODE
                && resultCode == RESULT_OK
                && data != null)
            imageUri = data.getData();
        settingsBinding.noteImage.setImageURI(imageUri);
    }
}
