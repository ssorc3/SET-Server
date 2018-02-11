# SET-Server

Create User: <br>
`POST /create`<br>
Sample Request:<br>
```
{
  "username": "Test",
  "password": "test"
}
```
returns token<br>
  
Get User Token: <br>
`POST /token`<br>
Sample Request:<br>
```
{
  "username": "Test",
  "password": "test"
}
```
returns token for existing user<br>

Register a new device: <br>
`POST /device/(deviceID)`<br>
Sample Request:<br>
```
{
  "deviceName": "Living room alarm"
}
```
Requires User Token<br>

Delete device: <br>
`DELETE /device/(deviceID)`<br>
Requires User Token<br>

Get user devices: <br>
`GET /device`<br>
Requires User Token<br>

Send Temperature Data: <br>
`POST /sensors/0/(deviceID)`<br>
Sample Request:<br>
```
{
  "value": 20.0
}
```
<br>

Send Humidity Data: <br>
`POST /sensors/1/(deviceID)`<br>
Sample Request:<br>
```
{
  "value": 60.0
}
```
<br>

Send Light Data: <br>
`POST /sensors/2/(deviceID)`<br>
Sample Request:<br>
``` 
{
  "value": 14.0
}
```
<br>
