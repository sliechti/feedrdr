
[![Join the chat at https://gitter.im/sliechti/feedrdr](https://badges.gitter.im/sliechti/feedrdr.svg)](https://gitter.im/sliechti/feedrdr?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# https://feedrdr.co

## Setting up Development Environment.

**The required steps are**

  * Getting Eclipse to compile the project
    * Dependencies to third parties and Tomcat
  * Setting up postgreSQL
  * Running it inside Eclipse
    * Tomcat Server configured
    * Project setup as a dynamic web module

### Recommended layout

  * DEVELOPMENT_DIR\workspace-feedrdr
    * feedrdr - _git clone https://github.com/sliechti/feedrdr_
    * tomcat8 - [Tomcat 8](https://tomcat.apache.org/download-80.cgi), extracted .zip from link below
    * postgresql9 - [PostgreSQL 9.x](https://www.postgresql.org/download/)  installation

### Setting up Eclipse project

  * 3rd party dependencies should be picked up by ivy, see ivy.xml
    * If not, install the Ivy DE plug-in from the Eclipse's marketplace
    * Go to Project Properties / Build Path / Libraries / Add
    * .. and add a new Library -> Ivy DE Managed Dependencies
  * Go to Window / Preferences / Server / Runtime Environments -> Add new Tomcat 8 runtime
    * This adds the required dependencies to Tomcat's .jars
  * Go to project / properties and select the Targeted Runtime
    * You should see the new server
    * Once selected Eclipse should add the Tomcat libraries to your build path, if not
      * Go to project / properties and add a new Library, select Tomcat or add the required libraries manually

#### Troubleshooting Eclipse Build

 * If the build path shows missing libraries make sure Ivy DE is installed and configured as explained above.
 * The following are inside Tomcat's lib folder, should be added automatically once the Server is added and configured as Targeted Runtimes under Project / Properties
    * _org.apache.tomcat.util.http.fileupload.*_ is in _tomcat-coyote.jar_
    * _javax.servlet.jsp.*_ is in jsp-api.jar

### Configuring Tomcat/PostgreSQL

  * If you plan to run tomcat from Eclipse, there is no need to configure the server
    * Eclipse only needs the location of the extracted files. i.e. dev_directory/tomcat8
    * and it will set everything in its own workspace.
  * [Configuring PostgreSQL/Tomcat8 on CentOS](https://docs.google.com/document/d/1t6bs5ScYc0_eTcr8a6GIq6dT5Rn2rrhSSf-ZSGgGnGo)
    * A series of notes on doing both - not a very clear guide
  * The create DB schema and tables is under _etc/db/schema.prod.sql_

### Eclipse: Deploying the web app

  * Go to you servers view, Windows / Show View / Others / Server
    * Right click on the server -> Add and Remove ...
    * Select the project
      * If you don't see it, make sure your project is defined as a dynamic web project
      * Project properties / Project Facets / activate:
        * Dynamic Web Modules -> 3.1, Java 1.7, Javascript
    * For Devs: Double click on the server
      * Go to the modules tab, and change the path to /feedreader
      * this path needs to match WEB-INF/conf/conf.*properties -> base_url = '/feedreader'

### Running for the first time

**Before starting Tomcat check the following and make the necessary changes for your environment**

  * Go to etc/deploy/local, and review the *.properties files.
  * Check web.xml under WEB-INF/web.xml

When Tomcat starts, it calls a listener class: `feedreader.main.AppContextInit`

  * This class is responsible for starting background threads and setting up the environment.

### Troubleshooting Eclipse Deployment

   * It throws ClassNotFound exceptions
      * Go to project / properties / deployment assembly and make sure to add Ivy
   * ClassNotFound exception errors
      * Make sure the .WAR is exported with all dependency .jars
      * In eclipse, go to project properties / deployment assembly / add referenced libraries

### Standalone Deployment

   * Document: If not using Eclipse

### Troubleshooting Standalone Deployment

   * Tomcat app not starting
     * Make sure the JRE and JDK match or
     * Make sure to not use any special JDK optimizations
     * Make sure the Eclipse .war export didn't have any specific JRE optimizations

### Contributing

If you wish to contribute, checkout the [/docs](https://github.com/sliechti/feedrdr/tree/master/docs) directory or
feature requests on http://rdr.uservoice.com/.

I plan to go over them one by one as I find the time to do so.

Feel free to contact me at steven -at- feedrdr.co if you need help setting up the environment or
building the sources, I'll document the steps as we go.


