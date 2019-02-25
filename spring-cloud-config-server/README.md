# spring-cloud-config-example
Simple spring cloud config with diferent configurations (localfile, git hub)

## Getting Started

This is a spring Cloud config example proyect.


### Prerequisites

Java and Maven

### Installing

Add the dependecies 
```
    <!--Internal management features to look for files and serve it, transitive spring-boot-started-web will be imported-->
    <dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-config-server</artifactId>
	</dependency>
	<!-- We want to securize the service, almost a basic auth -->
	<dependency>
	    <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-starter-security</artifactId>
	</dependency>
```

The Security configuration is based on defautls so only have to add this properties to application.properties
.you will notice that running logs will not show "defaultPassword message". This will be your default Basic credentials
```
spring.security.user.name=root
spring.security.user.password=12345
```

To enable The ConfigServer features, only have to add the annotation @EnableConfigServer
```
@SpringBootApplication
@EnableConfigServer
public class CentralizedConfigServerApplication {
    .......
```

### Getting Properties from diferent sources.
Diferent profiles was added to this proyect to test it on a easy way:

#### FromLocalFolder, uncomment on application.properties 
Fist create simple properties/yaml file and put in some directory. 
One for each app and Environment, for example :
**myAppA-dev.properties**, 
```
grettingMessage=Value for MyAppA on Dev Environment!!
```
**myAppA-prod.properties** 
```
grettingMessage=Value for MyAppA on Production Environment!!
```
**myAppB-dev.properties**
```
grettingMessage=Value for MyAppB on Develop Environment!!
```
**myAppB-int.properties**
```
grettingMessage=Value for MyAppB on Integration Environment!!
```


Now go to application.properties to edit how to search for this file

#Is a must use spring.profiles.active=native if you want to load from filesystem
spring.profiles.active=native
#Or your custom path directory
spring.cloud.config.server.native.searchLocations=/home/dpena/development/workspaces/daniel/spring-cloud-config-example/configurationsFolder
spring.security.user.name=root
spring.security.user.password=123456
```


Now Run in normal way : mvn spring-boot:run 

Request usign this "URL Format"  http://{basiccredentials}@localhost:8888/{serviceName}/{profileName}
```
GET http://root:123456@localhost:8888/myAppA/dev 
-----------------------------------------------
Response:
{
  "name": "myAppA",
  "profiles": ["dev"],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [{
    "name": "file:/home/dpena/development/workspaces/daniel/spring-cloud-config-example/localFolder/myAppA-dev.properties",
    "source": {
      "prop1": "Value for MyAppA on Dev Environment!!"
    }
  }]
}

GET http://root:123456@localhost:8888/myAppB/int
-----------------------------------------------
{
  "name": "myAppB",
  "profiles": ["int"],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [{
    "name": "file:/home/dpena/development/workspaces/daniel/spring-cloud-config-example/localFolder/myAppB-int.properties",
    "source": {
      "grettingMessage": "Value for MyAppB on Integration Environment!!"
    }
  }]
}
try yourself with the 2 remaining urls

```
As you can see, we can use 1 centralized server to manage/serve diferent configurations for diferent clients/applicaitons and profiles for each one.

### The client
Lets to create a simple SpringBoot Project that use this centralized configuration to run.
Create a Maven Project and add this dependencies:

```
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-config</artifactId>
	</dependency>

```
Edit the main class to configure as RestController and add a property

@SpringBootApplication
@RestController
public class SpringCloudConfigClientApplication {

	@Value(value = "${grettingMessage}")
	private String property;
	
	@RequestMapping("/")
    public String home() {
        return "On this environmet the value is "+property;
    }
	
	
	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigClientApplication.class, args);
	}

}

Edit application.properties to set the 
spring.cloud.config.uri= http://root:123456@localhost:8888






#### From GIT remote Repository
It is usefull to store all your properties in a available and centralized server with versioning features:

Create a repository on GITHUB (I will use current project, you can fork it to test with your own credentials)

Change current application.properties configuration in this way:









## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc

