package app.institute.helper;

/**
 * Created by CHIRANJIT on 7/23/2016.
 */
public class Helper
{

    public static String toCamelCase(String inputString) {

        String result = "";

        if (inputString.length() == 0) {
            return result;
        }

        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);

        result = result + firstCharToUpperCase;

        for (int i = 1; i < inputString.length(); i++) {

            char currentChar = inputString.charAt(i);
            char previousChar = inputString.charAt(i - 1);

            if (previousChar == ' ') {

                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }

        return result;
    }


    public static String format_date(int value)
    {

        if(value < 10)
        {
            return "0" + value;
        }

        return String.valueOf(value);
    }


    public static String dateTimeFormat(String str)
    {

        if(str.isEmpty())
        {
            return "0000-00-00";
        }

        String datetime = "";
        String day;

        String[] parts = str.split(" ")[0].split("-");

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int date = Integer.parseInt(parts[2]);

        if(date == 1 || date == 21 || date == 31)
        {
            day = "st";
        }

        else if(date == 2 || date == 22)
        {
            day = "nd";
        }

        else if(date == 3 || date == 23)
        {
            day = "rd";
        }

        else
        {
            day = "th";
        }

        switch (month)
        {

            case 1:

                datetime = date + day + " Jan " + year;
                break;

            case 2:

                datetime = date + day + " Feb " + year;
                break;

            case 3:

                datetime = date + day + " Mar " + year;
                break;

            case 4:

                datetime = date + day + " Apr " + year;
                break;

            case 5:

                datetime = date + day + " May " + year;
                break;

            case 6:

                datetime = date + day + " Jun " + year;
                break;

            case 7:

                datetime = date + day + " Jul " + year;
                break;

            case 8:

                datetime = date + day + " Aug " + year;
                break;

            case 9:

                datetime = date + day + " Sep " + year;
                break;

            case 10:

                datetime = date + day + " Oct " + year;
                break;

            case 11:

                datetime = date + day + " Nov " + year;
                break;

            case 12:

                datetime = date + day + " Dec " + year;
                break;
        }

        return datetime;
    }
}
