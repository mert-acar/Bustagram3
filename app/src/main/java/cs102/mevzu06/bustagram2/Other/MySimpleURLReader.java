package cs102.mevzu06.bustagram2.Other;

import cs1.*;

/**
 * Created by Mert Acar on 5/13/2017.
 */
public class MySimpleURLReader extends SimpleURLReader
{
    //properties
    private String url;

    //methods

    //constructor
    public  MySimpleURLReader( String string)
    {
        super( string);
        url = string;
    }


    /* This method gets the private string url
     * @return url
     */
    public String getURL()
    {
        return url;
    }

    /* This method gets the name of the url after the last '/' character
     * @return url's name.
     */
    public String getName()
    {
        int index = url.lastIndexOf("/");
        String name = url.substring( index + 1, url.length());
        return name;
    }

    /* This method overrides the getPageContents method in SimpleURLReader class and deletes the "null" at the beginning of the content
     * @return string page content
     */
    @Override
    public String getPageContents()
    {
        String pageContent;
        pageContent = super.getPageContents().substring( 4, super.getPageContents().length());
        return pageContent;
    }
}
