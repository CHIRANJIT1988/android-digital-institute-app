package app.institute.model;


public class Dashboard
{

    private String mName;
    private int mThumbnail;

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        this.mName = name;
    }


    public int getThumbnail()
    {
        return mThumbnail;
    }

    public void setThumbnail(int thumbnail)
    {
        this.mThumbnail = thumbnail;
    }
}