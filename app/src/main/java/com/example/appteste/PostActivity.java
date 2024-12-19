package com.example.appteste;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference root = database.getReference();
    DatabaseReference posts = root.child(MainActivity.POSTS_KEY);

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etPostText;
    private ImageView imgPostagem;
    Bitmap bmp;
    public static final int CAMERA_CALL = 1022;
    Boolean hasImage = false;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        etPostText = findViewById(R.id.etPostText);
        imgPostagem = findViewById(R.id.imgPostagem);
        Button btnAttachImage = findViewById(R.id.btnAttachImage);
        Button btnPost = findViewById(R.id.btnPost);
        Button btnFoto = findViewById(R.id.btnFoto);

        // Busca username definido no login
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                username = null;

            }else{
                username = extras.getString("username");
            }
        }else{
            username = (String) savedInstanceState.getSerializable("username");
        }

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("AppCidade");

        // Habilita o botão de voltar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Clique para selecionar uma imagem
        btnAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
        // Clique para abrir a câmera
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        // Clique para enviar a postagem
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postContent();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        // Fecha a Activity atual e volta para a anterior
        finish();
        return true;
    }

    // Abre a galeria para escolher uma imagem
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_CALL);
    }

    // Recebe o resultado da seleção de imagem
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                // Obtém o URI da imagem selecionada
                Uri imageUri = data.getData();
                try {
                    // Converte o URI em Bitmap
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    hasImage = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_CALL) {
                // Para imagens capturadas pela câmera
                bmp = (Bitmap) data.getExtras().get("data");
                hasImage = true;
            }
            imgPostagem.setImageBitmap(bmp);
        }
    }


    public String loadImage(){
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteOut);
        return Base64.encodeToString(byteOut.toByteArray(), Base64.DEFAULT);
    }

    // Envia a postagem
    private void postContent() {
        String postText = etPostText.getText().toString();
        if (postText.isEmpty()) {
            Toast.makeText(this, "Digite um texto para a postagem!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Enviar o conteúdo da postagem (imagem + texto)
        if (hasImage) {
            Toast.makeText(this, "Postagem com imagem enviada!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Postagem sem imagem enviada!", Toast.LENGTH_SHORT).show();
        }
        Post p = new Post();
        p.setText(etPostText.getText().toString());
        p.setUserName(username);
        if(hasImage){
            String bmpEncoded = loadImage();
            hasImage = false;
            p.setImage(bmpEncoded);
        }

        String key = posts.push().getKey();
        posts.child(key).setValue(p);

        finish();
    }
}
