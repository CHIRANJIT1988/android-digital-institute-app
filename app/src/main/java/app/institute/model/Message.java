package app.institute.model;

/**
 * Created by dell on 25-08-2015.
 */
public class Message
{

    public String message_id, message_title, message_body, timestamp;
    public int read_status;


    public Message()
    {
        super();
    }


    public Message(String message_id, String message_title, String message_body, String timestamp, int read_status)
    {

        this.message_id = message_id;
        this.message_title = message_title;
        this.message_body = message_body;
        this.timestamp = timestamp;
        this.read_status = read_status;
    }
}