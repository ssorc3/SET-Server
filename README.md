# SET-Server

## User
### Create User: 
`POST /create`<br>
Sample Request:<br>
```
{
  "username": "Test",
  "password": "test"
}
```
returns token<br>
  
### Get User Token:
`POST /token`<br>
Sample Request:<br>
```
{
  "username": "Test",
  "password": "test"
}
```
returns token for existing user<br>

## Devices
### Register a new device: 
`POST /device/(deviceID)`<br>
Sample Request:<br>
```
{
  "deviceName": "Living room alarm"
}
```
Requires User Token<br>

### Delete device:
`DELETE /device/(deviceID)`<br>
Requires User Token<br>

### Get user devices:
`GET /device`<br>
Requires User Token<br>

### Send Temperature Data:
`POST /sensors/0/(deviceID)`<br>
Sample Request:<br>
```
{
  "value": 20.0
}
```
<br>

### Send Humidity Data:
`POST /sensors/1/(deviceID)`<br>
Sample Request:<br>
```
{
  "value": 60.0
}
```
<br>

### Send Light Data:
`POST /sensors/2/(deviceID)`<br>
Sample Request:<br>
``` 
{
  "value": 14.0
}
```
<br>

## Scripts
### Set a user's script:
`POST /script` <br>
Sample Request:<br>
```
{
  "script": "if(temperature < 20 & noise > 400) then kettle; end"
}
```
requires user token<br>

### Remove a user's script: 
`DELETE /script`<br>
requires user token<br>

## Bridges
### WebSocket
Bridge WebSocket `ws://<ip>/bridgeWS`
