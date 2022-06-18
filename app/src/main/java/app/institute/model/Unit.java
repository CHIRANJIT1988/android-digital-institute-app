package app.institute.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHIRANJIT on 8/5/2016.
 */
public class Unit implements Serializable
{

    public static List<Unit> unitList = new ArrayList<>();


    public Branch branch = new Branch();

    public String unit_id, unit_name;

    public Unit()
    {

    }

    public Unit(String unit_id)
    {
        this.unit_id = unit_id;
    }


    public Unit(Branch branch, String unit_id)
    {

        this.branch = branch;
        this.unit_id = unit_id;
    }

    public Unit(Branch branch, String unit_id, String unit_name)
    {

        this.branch = branch;
        this.unit_id = unit_id;
        this.unit_name = unit_name;
    }
}
