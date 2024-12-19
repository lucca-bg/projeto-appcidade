package com.example.appteste;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class TelaPrincipal extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference root = database.getReference();
    DatabaseReference dbPosts = root.child(MainActivity.POSTS_KEY);
    FirebaseListAdapter<Post> listAdapter;


    ListView listPosts;
    List<Post> posts = new ArrayList<>();
    PostAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_principal);

        listPosts = findViewById(R.id.listPost);
        adapter = new PostAdapter(posts, getBaseContext());
        listPosts.setAdapter(adapter);

        String userName;

        // Busca username setado no login
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                userName = null;

            }else{
                userName = extras.getString("username");
            }
        }else{
            userName = (String) savedInstanceState.getSerializable("username");
        }

        // Configuração da consulta para obter os posts
        Query query = FirebaseDatabase.getInstance().getReference().child("posts");

        // Configuração do FirebaseListOptions
        FirebaseListOptions<Post> options = new FirebaseListOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .setLayout(R.layout.activity_lista_principal)
                .setLifecycleOwner(this)
                .build();

        listAdapter = new FirebaseListAdapter<Post>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Post model, int position) {

                // Obtém a chave única do Firebase para o item
                String key = getRef(position).getKey();
                model.setKey(key);
                TextView txtPost = v.findViewById(R.id.txtTextoPost);
                txtPost.setText(model.getText());
                TextView txtUsername = v.findViewById(R.id.txtUserName);
                txtUsername.setText(model.getUserName());
                ImageView imgPost = v.findViewById(R.id.imgPost);
                // Trecho incluído para garantir que não carregue a imagem errada na lista
                if (model.getImage() != null && !model.getImage().isEmpty()) {
                    byte[] imageData = Base64.decode(model.getImage(), Base64.DEFAULT);
                    Bitmap img = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    imgPost.setImageBitmap(img);
                } else {
                    imgPost.setImageDrawable(null);
                }


            }
        };

        listPosts.setAdapter(listAdapter);

        listPosts.setOnItemLongClickListener((parent, view, position, id) -> {
            // Obtém o item selecionado
            Post selectedPost = (Post) listAdapter.getItem(position);
            // Busca user responsável pela postagem
            TextView username = view.findViewById(R.id.txtUserName);
            String stringUsername = username.getText().toString();
            // Verifica se user da postagem é o mesmo user logado
            assert userName != null;
            if (userName.equals(stringUsername)) {
                // Cria o pop-up de confirmação
                new AlertDialog.Builder(this)
                        .setTitle("Confirmar exclusão")
                        .setMessage("Deseja realmente deletar este post?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            // Remove o item do Firebase usando a chave única
                            DatabaseReference postRef = dbPosts.child(selectedPost.getKey());
                            postRef.removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("DeletePost", "Post removido com sucesso!");
                                } else {
                                    Log.e("DeletePost", "Erro ao remover post: ", task.getException());
                                }
                            });
                        })
                        .setNegativeButton("Não", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }

            return true;
        });



        Button btLogin = findViewById(R.id.btnPost);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia a PostActivity ao clicar no botão Login
                Intent intent = new Intent(TelaPrincipal.this, PostActivity.class);
                intent.putExtra("username", userName);
                startActivity(intent);
            }
        });

    }
}