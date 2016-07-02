package feedreader.api.v1;

import feedreader.cron.CronFetchNews;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/status")
public class StatusAPI {

    @GET
    @Path("/fetcher")
    @Produces(MediaType.TEXT_PLAIN)
    public String fetchStatus() {
        String ret = "... fetch instance \n";
        ret += CronFetchNews.fetchInstance(false).toString();
        ret += " \n ... validation instance \n\n";
        ret += CronFetchNews.fetchInstance(true).toString();
        return ret;
    }

}
