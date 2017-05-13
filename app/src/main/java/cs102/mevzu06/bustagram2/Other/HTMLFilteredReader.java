package cs102.mevzu06.bustagram2.Other;


/**
 * Created by Mert Acar on 5/13/2017.
 */

public class HTMLFilteredReader extends MySimpleURLReader
{
    //constructor
    public  HTMLFilteredReader( String string)
    {
        super( string);
    }



    /* This method gets the page content without html codes
     * @return filtered code
     */
    public String getPageContents()
    {
        String filtered = super.getPageContents();
        int end = filtered.lastIndexOf(">") + 1;
        return filtered.substring(end, filtered.length() - 1);
    }
}

