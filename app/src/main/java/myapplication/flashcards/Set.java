package myapplication.flashcards;

import android.support.design.internal.ParcelableSparseArray;

import java.util.ArrayList;

/**
 * Created by Victor on 6/28/2016.
 */
public class Set extends ParcelableSparseArray{
    private static ArrayList<String> questions;
    private static ArrayList<String> answers;
    private static String name;

    public void Set(){
        questions = new ArrayList<String>();
        answers = new ArrayList<String>();

    }

    public void setName(String str){
        name = str;
    }

    public ArrayList<String> getQuestions(){
        return questions;
    }

    public ArrayList<String> getAnswers(){
        return answers;
    }

    public void setQuestions(ArrayList<String> q){
        questions = q;
    }

    public void setAnswers(ArrayList<String> a){
        answers = a;
    }
}
