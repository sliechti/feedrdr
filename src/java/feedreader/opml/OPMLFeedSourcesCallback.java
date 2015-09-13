package feedreader.opml;

import feedreader.entities.OPMLEntry;
import feedreader.log.Logger;
import feedreader.store.FeedSourcesTable;

public class OPMLFeedSourcesCallback implements OPMLParser.Callback 
{
    int entriedFound = 0;
    int sourceQueued = 0;
    int sourceError= 0;
    int sourceKnown = 0;
    
    @Override
    public void onEntry(OPMLEntry entry)
    {
        entriedFound++;
        FeedSourcesTable.RetCodes retCode = FeedSourcesTable.addNewSource(entry.getXmlUrl());
        switch(retCode)
        {
            case QUEUED:
                sourceQueued++;
                break;

            case ERROR:
                sourceError++;
                break;

            case IN_QUEUE:
                sourceKnown++;
                break;

            default:
                Logger.error(this.getClass()).log("Unhandled ret code: ").log(retCode.name()).end();
        }        
    }

    @Override
    public void onDirectoryStart(String name)
    {
    }

    @Override
    public void onDirectoryEnd()
    {
    }    
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("entries found ").append(entriedFound).append(" - ")
            .append("queued ").append(sourceQueued).append(" - ")
            .append("error ").append(sourceError).append(" - ")
            .append("source known ").append(sourceKnown).append(" - ");
        return sb.toString();
    }

    @Override
    public void onBodyStart()
    {
    }

    @Override
    public void onBodyEnd()
    {
    }
    
}
