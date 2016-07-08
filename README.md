# feedrdr.co

Sources for [feedrdr.co](http://feedrdr.co)

## Setting up Development Environment.

### Recommended layout

  * DEVELEPMENT_DIR\workspace-feedrdr
    * feedrdr - _git clone https://github.com/sliechti/feedrdr_
    * tomcat8 - [Tomcat 8](https://tomcat.apache.org/download-80.cgi), extracted .zip from link below
    * postgresql9 - [PostgreSQL 9.x](https://www.postgresql.org/download/)  installation

### Configuring Tomcat/PostgreSQL

  * If you plan to run tomcat from Eclipse, there is no need to configure it, Eclipse only needs the location of the extracted files. i.e. dev_directory/tomcat8
  * [Configuring PostgreSQL/Tomcat8 on CentOS](https://docs.google.com/document/d/1t6bs5ScYc0_eTcr8a6GIq6dT5Rn2rrhSSf-ZSGgGnGo)
    * A series of notes on doing both - not a very clear guide
  * The create DB schema and tables is under _etc/db/schema.prod.sql_

### Setting up Eclipse project

  * 3rd party dependencies should be picked up by ivy, see ivy.xml
    * If not, install the Ivy DE plugin from the Eclipse's marketplace
    * Go to Project Properties / Build Path / Libraries / Add
    * .. and add a new Library -> Ivy DE Managed Dependencies
  * Go to Window / Preferences / Server / Runtime Environments -> Add new Tomcat 8 runtime
    * This adds the required dependencies to Tomcat's .jars

#### Troubleshooting

 * If the build path shows missing libraries make sure Ivy DE is installed and configured as explained above.
 * The fdollowing are inside Tomcat's lib folder, should be added automatically once the Server is added and configured as Targeted Runtimes under Project / Properties
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

###Troubleshooting

   * Tomcat app not starting
     * Make sure the JRE and JDK match or
     * Make sure to not use any special JDK optimizations
   * ClassNotFound exception errors
      * Make sure the .WAR is exported with all dependecy .jars
      * In eclipse, go to project properties / deployment assembly / add referenced libraries

### Deployment

* Build .war, deploy.

### Contributing

If you wish to contribute, checkout the [/docs](https://github.com/sliechti/feedrdr/tree/master/docs) directory or
feature requests on http://rdr.uservoice.com/.

I plan to go over them one by one as I find the time to do so.

Feel free to contact me at steven -at- feedrdr.co if you need help setting up the environment or
building the sources, I'll document the steps as we go.


