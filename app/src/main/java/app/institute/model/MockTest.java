package app.institute.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHIRANJIT on 8/10/2016.
 */
public class MockTest
{

    public static List<MockTest> testPaperList = new ArrayList<>();
    public static List<MockTest> testList = new ArrayList<>();

    public Question question;
    public int test_id, total_marks, duration;
    public String test_name, attempted_on;
    public int count_correct, count_wrong, count_not_attempt;
    public int percentage;


    public MockTest(int test_id, String test_name, int total_marks, int duration, String attempted_on, int percentage)
    {

        this.test_id = test_id;
        this.test_name = test_name;
        this.total_marks = total_marks;
        this.duration = duration;
        this.attempted_on = attempted_on;
        this.percentage = percentage;
    }


    public MockTest(int test_id, int total_marks, int duration, Question question)
    {

        this.test_id = test_id;
        this.total_marks = total_marks;
        this.duration = duration;
        this.question = question;
    }


    public MockTest(int test_id, int count_correct, int count_wrong, int count_not_attempt, Question question)
    {

        this.test_id = test_id;
        this.count_correct = count_correct;
        this.count_wrong = count_wrong;
        this.count_not_attempt = count_not_attempt;
        this.question = question;
    }


    public static int calculateScore()
    {

        int positive_score = 0;
        int negative_score = 0;

        for (MockTest test: testList)
        {

            if(test.question.is_correct_answer == 1)
            {
                positive_score += test.question.positive_marks;
            }

            else if(test.question.is_correct_answer == -1)
            {
                negative_score += (test.question.negative_marks * -1);
            }
        }

        return positive_score + negative_score;
    }
}