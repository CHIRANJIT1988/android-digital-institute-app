package app.institute.model;

import java.util.List;

/**
 * Created by CHIRANJIT on 6/30/2016.
 */
public class Question
{

    public List<Option> optionList;
    public int question_id, positive_marks, negative_marks, is_correct_answer;
    public String question, diagram;


    public Question(int question_id, String question, String diagram, int positive_marks, int negative_marks, List<Option> optionList)
    {

        this.optionList = optionList;

        this.question_id = question_id;
        this.question = question;
        this.diagram = diagram;
        this.positive_marks = positive_marks;
        this.negative_marks = negative_marks;
    }


    public Question(int positive_marks, int negative_marks)
    {
        this.positive_marks = positive_marks;
        this.negative_marks = negative_marks;
    }
}
