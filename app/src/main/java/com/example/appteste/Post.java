package com.example.appteste;

public class Post {
    private String key; // Chave da ocorrência do post
    private String image;  // ID da imagem de recurso (0 se não houver imagem)
    private String userName; // Nome do usuário
    private String text;     // Texto da postagem

    public Post(String text, String userName, String image) {
        this.text = text;
        this.userName = userName;
        this.image = image;
    }

    public Post(){

    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public String getText() {
        return text;
    }
}
