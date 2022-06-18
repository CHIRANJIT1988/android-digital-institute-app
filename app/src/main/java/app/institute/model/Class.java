package app.institute.model;

import java.io.Serializable;

/**
 * Created by CHIRANJIT on 8/4/2016.
 */
public class Class implements Serializable
{

    public String class_code, class_name;


    public Class()
    {

    }

    public Class(String class_code)
    {
        this.class_code = class_code;
    }

    public Class(String class_code, String class_name)
    {

        this.class_code = class_code;
        this.class_name = class_name;
    }
}
