package app.institute.model;

import java.io.Serializable;

/**
 * Created by CHIRANJIT on 7/2/2016.
 */
public class Subject implements Serializable
{

    public Branch branch;
    public String subject_code, subject_name;


    public Subject()
    {

    }


    public Subject(String subject_code)
    {
        this.subject_code = subject_code;
    }


    public Subject(String subject_code, String subject_name)
    {
        this.subject_code = subject_code;
        this.subject_name = subject_name;
    }
}