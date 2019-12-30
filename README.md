# Automatic API Attack Tool

Imperva's customizable API attack tool takes an API specification as an input, and generates and runs attacks that are based on it as an output.

The tool is able to parse an API specification and create fuzzing attack scenarios based on what is defined in the API specification. Each endpoint is injected with cleverly generated values within the boundaries defined by the specification, and outside of it, the appropriate requests are sent and their success or failure are reported in a detailed manner. You may also extend it to run various security attack vectors, such as illegal resource access, XSS, SQLi and RFI, that are targeted at the existing endpoints, or even at non-existing ones.
**No human intervention is needed. Simply run the tool and get the results.**


The tool can be easily extended to adapt to meet the various needs, such as for a developer who wants to test their API, or an organization that wants to run regular vulnerability or positive security scans on its public API. It is built with CI/CD in mind.

## Requirements
- Java 8 or higher
- Gradle

## Running
- Check out the code from GitHub and run `./gradlew build` or `gradlew.bat build` on Windows
- You may find the executable jar under the build/libs folder
- Run 'java -jar imperva-api-attack-tool.jar' to see the help menu

## Making a Linux executable
- Copy the `runnable.sh` file from the `src/main/resources` folder, to the same directory with the jar file.
- Now run: `cat runnable.sh imperva-api-attack-tool.jar > api-attack.sh && chmod +x api-attack.sh`
- You may use the api-attack.sh file as a regular executable

## Usage

##### Required parameters:
  -f, --specFile=*specFilePath*
   >The API specification file (swagger 2.0) to run on. JSON/YAML format. For better results, make sure responses are well defined for each endpoint.

  -n, --hostName=*hostName*
   >The host name to connect to. It can also be an IP

  -s, --hostScheme=*hostScheme*
   >Connection to host will be made using this scheme; e.g: https or http

##### Optional parameters:
  -p, --hostPort=*hostPort*
   >The port the host is listening on for API calls, default is: 443
  
  -ph, --proxyHost=*proxyHost*
  >Specify the proxy host to send the requests via a proxy
  
  -pp, --proxyPort=*proxyPort*
  >The proxy port, default is: 80
  
  -rcn, --addNegativeRC=*responseCode[,responseCode...]*
  >Additional response codes to be accepted in negative attacks (e.g. bad value attacks). Multiple values are supported, separated by commas
  
  -rcp, --addPositiveRC=*responseCode[,responseCode...]*
  >Additional response codes to be accepted in positive checks (legitimate value attacks). Multiple values are supported, separated by commas

 

### Typical usage scenarios:
- You'd like to check whether your API is protected by an API Security solution.
  
  Example run: `api-attack.sh -f swaggerPetStore.json -n myapisite.com -s http -rcn=403`
  
  We've added the `403` response code as a legitimate response code for the negative checks. This is since the API Security solution blocks such requests, and returns a 403 status. The spec, on the other hand, doesn't necessarily define such a response with HTTP code of 403, for any of its endpoints. This would make such responses legitimate, in spite of them not being in the spec, and alert you when such a response is not received from a negative check. Such cases mean that you are left unprotected by your API security solution.

- You'd like to check how your proxy mitigates API attacks, but don't have an actual site behind it.

  Example run: `api-attack.sh -f swaggerPetStore.json -n myapisite.com -s http -ph 127.0.0.1 -pp=4010 -rcn=403 -rcp=404`
  
  This time we've added the `404` status code to the positive scenarios. So that when a scenario is not being blocked, we will not report a failure, but rather accept the legitimate `404` (resource not found) response.

- You'd like to check whether your API handles all inputs correctly. Furthermore, you'd like to run it on a nightly basis, or even after each time a developer pushes new code to the project. 
  
  Example run: `api-attack.sh -f myapi_swagger.yaml -n staging.myorg.com -s https`
  
  This time we're running without any exclusions. The API specification file must declare its response codes precisely. The tool will accept only them as legitimate, and will fail the checks otherwise. See more below on conditions of failing the checks.
  Run the above command in a Jenkins job (or any other CI/CD software to your liking), which will be triggered by a cron, or a repo code push activity.
  Make sure you have the `TestNG` plugin installed, which should parse the results written in `build/testng-results`, for better visibility in the CI/CD scenario.

- You'd like to check whether this API might be open to fuzzing attempts. Simply run the tool and check the reported failures.
  
  Example run: `api-attack.sh -f publiclyAvailableSwaggerOfAPI.yaml -n api.corporate.com -s https`
  
- You'd like to check whether your API is implemented correctly on the server side, or that its definition corresponds the server implementation.
    
  Example run: `api-attack.sh -f publiclyAvailableSwaggerOfAPI.yaml -n api.corporate.com -s https`

### Conditions for failing checks
- The tool verifies the generated request response code matches the declared response codes in the swagger.
Yet, 
- Positive checks: if it's a clear error (code is 5xx), we will still fail the check, even if this response code is not defined in the spec, but not if you supplied an override.
- Negative checks: if the response is not a legitimate error (1xx, 2xx, 5xx), we fail the check unless you supplied an override. If the legitimate error code is not in the spec, the check will fail as well.
- You may use the 'default' definition in the response section of the swagger, but this is not recommended. Always define your legitimate answers precisely.

### Conditions for failing checks
- The tool verifies the generated request response code matches the declared response codes in the swagger.
Yet, 
- Positive checks: if it's a clear error (code is 5xx), we will still fail the check, even if this response code is not defined in the spec, but not if you supplied an override.
- Negative checks: if the response is not a legitimate error (1xx, 2xx, 5xx), we fail the check. Unless you supplied an override. If the legitimate error code is not in the spec, the check will fail as well.
- You may use the 'default' definition in the response section of the swagger, but this is not recommended. Always define your legitimate answers precisely.

## Expected outputs:
- The tool uses the testng reporting framework, so any plugin that handles testng runs can be used here. Only note that the results are written under the build/testng-results folder. This can be changed, of course.
- The tool generates requests according to its check suites, and each request checks something specific. So each check will present all the relevant details in the command line output, together with what is being checked, what the response is, and whether or not it was as expected.
- Any bad requests will be stored in the `bad_requests` folder, so that you could analyze it later (e.g. if this is running on CI/CD server, for instance, and you don't have immediate access to the machine)
- In the end, you will be provided with a summary

###### Example of a negative check that failed:
```
***** Testing API Endpoint *****
***** Test ID: 1575128763286-74212
Testing: Bad Property: /username (STRING), value: {, URL encoded: %7B
--> Url: /user/{
--> Method: GET
--> Headers: []
----------**----------
Request was: GET /user/{ [Accept: application/json], Response status code: 200(UNEXPECTED)
Response (non parsed):
{"id":0,"username":"string","firstName":"string","lastName":"string","email":"string","password":"string","phone":"string","userStatus":0}
```
Why did the check fail? The request got 200, even though didn't contain a legal URL

###### Another example:
```
***** Testing API Endpoint *****
***** Test ID: 1575128763286-25078
Testing: Bad Property: /body/quantity (INTEGER), value: 0.4188493, URL encoded: 0.4188493
--> Url: /store/order
--> Method: POST
--> Headers: []
--> Body: {"petId":-2511515111206893939,"quantity":0.4188493,"id":698757161286106823,"shipDate":"�s","complete":"true","status":"approved"}
----------**----------
Request was: POST /store/order [Accept: application/json], Response status code: 200(UNEXPECTED)
Response (non parsed):
{"id":0,"petId":0,"quantity":0,"shipDate":"2019-11-30T15:46:03Z","status":"placed","complete":false}
```
The server expected to get an integer, but accepted a double value. This might be a good spot to try and exploit some buffer overflow in the server.

###### Example of a successful check:
```
***** Testing API Endpoint *****
***** Test ID: 1575128763137-43035
Testing: /user/{username}
--> Url: /user/%E68E97EDB4Oq-(!BbG,Y$p'A-KW%65f9FA6jt5vvDz-cW.QGsLS+AA~RIHC3wgy25lDJsGzcT.;kJ+(
--> Method: GET
--> Headers: []
----------**----------
Request was: GET /user/%E68E97EDB4Oq-(!BbG,Y$p'A-KW%65f9FA6jt5vvDz-cW.QGsLS+AA~RIHC3wgy25lDJsGzcT.;kJ+( [Accept: application/json], Response status code: 404
Response (non parsed):
{"statusCode":404,"error":"Not Found","message":"Not Found"}
```
We supplied a username that was nonexistent but legal, according to the API specification. The server knew how to handle this request and return a legal error.

## Supported Check Scenarios
We will use the term `endpoint` here, as the endpoint URL and Method tuple.

###### Positive Scenarios
- For each endpoint, creates a request with generated values for all of its parameters. These are generated randomly, but obey the rules that are defined in the API specification.
- For each endpoint, creates a request with only the required parameters, with values generated as described above.

###### Negative Scenarios
- For each endpoint, creates multiple requests, each which checks a different parameter. The tool does this by injecting a random bad input value in the checked parameter, and filling the rest with "positive" values which are generated in the same manner as described in the positive scenarios.

###### Ongoing Effort
We are working on migrating our other scenarios to the open-source tool, for the benefit of the community. Stay tuned for updates.

## Extensibility 
The tool is written in a way that makes it easy to extend its fuzzing and request generation functionality to meet your specific needs. Feel free to suggest any additions that others may benefit from by creating a pull request.

## Getting Help

If you have questions about the library, be sure to check out the source code documentation. If you still have questions, reach out to me via email at `boris.serebro(at)imperva(dot)com`.

## Reporting Bugs

Please open a Git Issue and include as much information as possible. If possible, provide a sample code that illustrates the problem you're encountering. If you're experiencing a bug on a specific repository only, provide a link to it, if possible. Do not open a Git Issue for help, only for bug reports.
