package app.institute.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHIRANJIT on 8/21/2016.
 */
public class DailyPracticePaper
{

    public static List<DailyPracticePaper> dppList = new ArrayList<>();

    public Unit unit = new Unit();

    public String paper_code, paper_name, paper_date, daily_practice_paper;


    public DailyPracticePaper(Unit unit, String paper_code, String paper_name, String paper_date, String daily_practice_paper)
    {

        this.unit = unit;
        this.paper_code = paper_code;
        this.paper_name = paper_name;
        this.paper_date = paper_date;
        this.daily_practice_paper = daily_practice_paper;
    }
}