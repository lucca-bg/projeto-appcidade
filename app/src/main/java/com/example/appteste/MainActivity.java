package com.example.appteste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static final String POSTS_KEY = "posts";
    public static final String USERS_KEY = "users";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference root = database.getReference();
    DatabaseReference users = root.child(USERS_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText ptUsuario = findViewById(R.id.ptUsuario);
        EditText ptSenha = findViewById(R.id.ptSenha);
        Button btLogin = findViewById(R.id.btLogin);
        Button btCreateAccount = findViewById(R.id.btCreateAccount);

        // Configura o botão de login
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ptUsuario.getText().toString().trim();
                String password = ptSenha.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Preencha usuário e senha!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verifica a existência do usuário no banco
                users.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String storedPassword = snapshot.child("password").getValue(String.class);
                            if (storedPassword != null && storedPassword.equals(password)) {
                                Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                                // Redireciona para a tela principal
                                Intent intent = new Intent(MainActivity.this, TelaPrincipal.class);
                                intent.putExtra("username", username); // Passa o nome do usuário
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Senha incorreta!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("FirebaseError", "Erro ao acessar o banco de dados: " + error.getMessage());
                        Toast.makeText(MainActivity.this, "Erro no banco de dados!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Configura o botão de criação de conta
        btCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ptUsuario.getText().toString().trim();
                String password = ptSenha.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Preencha usuário e senha!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verifica se o usuário já existe
                users.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(MainActivity.this, "Usuário já existe!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Cria um novo usuário
                            users.child(username).child("password").setValue(password);
                            Toast.makeText(MainActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("FirebaseError", "Erro ao acessar o banco de dados: " + error.getMessage());
                        Toast.makeText(MainActivity.this, "Erro no banco de dados!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
