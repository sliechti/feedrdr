# feedrdr.co

Sources for [feedrdr.co](http://feedrdr.co)

## Setting up Development Environment.

### Pre-requisites

  * PostgreSQL 9.x
    * The create DB schema and tables is under _etc/db/schema.prod.sql_
  * Tomcat 8 installed
  * The document below is not a guide, it contains hints I created for me.
  * [configuring psql/tomcat gdrive/doc](https://docs.google.com/document/d/1t6bs5ScYc0_eTcr8a6GIq6dT5Rn2rrhSSf-ZSGgGnGo)

### Setting up Eclipse project

 * git clone https://github.com/sliechti/feedrdr.git
 * 3rd parties should be picked up by ivy
   * If not, you can grab the ivy plugin from Eclipse's marketplace
 * preferences / server / run time environment -> add new tomcat 8 runtime
 * build (build path shows missing libraries, manually download/reference them)
	 * _org.apache.tomcat.util.http.fileupload.*_ is in _tomcat-coyote.jar_
	 * _javax.servlet.jsp.*_ is in jsp-api.jar

### Running

Before starting Tomcat check the following classes/files and make the necessary changes:

  * feedreader.config.FeedAppConfig
    * FETCH_RUN_START_FETCHING
      * If true, it starts the background thread that fetches feed sources
    * FETCH_RUN_START_VALIDATION
      * If true, it starts the background thread that validates the fetched sources
  * WEB-INF/ *.properties
  * see web.xml, feedreader.main.AppContextInit
    * The class is responsible for starting background threads
    * Setting up the environment

### Deployment

* Build .war, deploy.

### Contributing

If you wish to contribute, checkout the [/docs](https://github.com/sliechti/feedrdr/tree/master/docs) directory or
feature requests on http://rdr.uservoice.com/.

I plan to go over them one by one as I find the time to do so.

Feel free to contact me at steven -at- feedrdr.co if you need help setting up the environment or
building the sources, I'll document the steps at the same time.


