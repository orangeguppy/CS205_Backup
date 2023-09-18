package com.example.cs205project;

public class HighScore {
    private int id;
    private String user_name;
    private int score;

    public HighScore(int id, String user_name, int score) {
        this.id = id;
        this.user_name = user_name;
        this.score = score;
    }

    public int getRank() {
        return id;
    }

    public String getName() {
        return user_name;
    }

    public int getScore() {
        return score;
    }

    public void setID(int id){
        this.id = id;
    }
    public String toString(){
        return "id: " + id + " name: " + user_name + " score: " + score;
    }

}

