package app.institute.model;

/**
 * Created by CHIRANJIT on 8/10/2016.
 */
public class Option
{

    public int option_id, is_correct;
    public String option;

    public Option(int option_id, String option, int is_correct)
    {

        this.option_id = option_id;
        this.option = option;
        this.is_correct = is_correct;
    }
}
