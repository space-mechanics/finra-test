In order to start the application run the command as follows:

mvn clean install; java -jar target/testapp-0.0.1-SNAPSHOT.jar

This will start the application server on port 8080.

RESTful services path and description:

1)
Http Method: POST
Path: http://localhost:8080/
Params:
    "file" - "multipart/form-data", file to upload
    "description" - string (optional)
    "type" - string (optional)
Description:
    Enabled the application to upload files and save files' metadata into H2 in memory data base.

2)
Http Method: GET
Path: http://localhost:8080/findAll
Params:
    empty
Description:
    Returns all previously saved records in JSON format

3)
Http Method: GET
Path: http://localhost:8080/fileMetadata/{id}
Params:
    {id} path variable which represents a request_id PK of the request record in the data base
Description:
    Returns file's metadata in JSON format

4)
Http Method: GET
Path: http://localhost:8080/file/{id}
Params:
    {id} path variable which represents a request_id PK of the request record in the data base
Description:
    Returns file's body, i.e. use this method to download the file which is stored in the data base.




