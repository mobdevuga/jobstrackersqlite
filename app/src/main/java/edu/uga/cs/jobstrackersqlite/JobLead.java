package edu.uga.cs.jobstrackersqlite;

/**
 * This class (a POJO) represents a single job lead, including the id, company name,
 * phone number, URL, and some comments.
 * The id is -1 if the object has not been persisted in the database yet, and
 * the db table's primary key value, if it has been persisted.
 */
public class JobLead {

    private long   id;
    private String companyName;
    private String phone;
    private String url;
    private String comments;

    public JobLead()
    {
        this.id = -1;
        this.companyName = null;
        this.phone = null;
        this.url = null;
        this.comments = null;
    }

    public JobLead( String companyName, String phone, String url, String comments ) {
        this.id = -1;  // the primary key id will be set by a setter method
        this.companyName = companyName;
        this.phone = phone;
        this.url = url;
        this.comments = comments;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public String toString()
    {
        return id + ": " + companyName + " " + phone + " " + url + " " + comments;
    }
}
