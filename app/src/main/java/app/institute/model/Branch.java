package app.institute.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHIRANJIT on 7/2/2016.
 */
public class Branch implements Serializable
{

    public Subject _subject = new Subject();
    public Class _class = new Class();
    public String branch_code, branch_name;

    public static List<Branch> list = new ArrayList<>();


    public Branch()
    {

    }


    public Branch(String branch_code, String branch_name)
    {
        this.branch_code = branch_code;
        this.branch_name = branch_name;
    }


    public Branch(Subject _subject, Class _class, String branch_code, String branch_name)
    {

        this._subject = _subject;
        this._class = _class;
        this.branch_code = branch_code;
        this.branch_name = branch_name;
    }


    public Branch(Subject _subject, Class _class, String branch_code)
    {

        this._subject = _subject;
        this._class = _class;
        this.branch_code = branch_code;
    }
}