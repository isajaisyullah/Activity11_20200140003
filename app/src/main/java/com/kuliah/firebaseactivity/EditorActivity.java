package com.kuliah.firebaseactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class EditorActivity extends AppCompatActivity {

    // Mendefinisikan variabel yang akan dipakai

    private EditText editName, editEmail;
    private Button btnSave;

    // Inisialisasi firestone

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editName = findViewById(R.id.name);
        editEmail = findViewById(R.id.email);
        btnSave = findViewById(R.id.btn_save);

        progressDialog = new ProgressDialog(EditorActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Menyimpan...");
        btnSave.setOnClickListener(v -> {
            if (editName.getText().length()>0 && editEmail.getText().length()>0){

                // Memanggil method save data

                saveData(editName.getText().toString(), editEmail.getText().toString());
            }else {
                Toast.makeText(getApplicationContext(), "Silahkan isi semua data!", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Mendaaptkan data dari  main activity
         */

        Intent intent = getIntent();
        if (intent!= null){
            id = intent.getStringExtra("id");
            editName.setText(intent.getStringExtra("name"));
            editEmail.setText(intent.getStringExtra("email"));
        }
    }

    private void saveData(String name, String email){
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        progressDialog.show();
        /**
         * Jika id kosong maka akan edit data
         */
        if (id!= null){
            /**
             * Kode untuk edit data firestore dengan mengambil id
             */
            db.collection("users").document(id)
                    .set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Berhasil!", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(),"Gagal!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            /**
             * kode untuk menambahkan data dengan .add
             */
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Berhasil!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}