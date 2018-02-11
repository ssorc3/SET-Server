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
requires User Token<br>

### Delete device:
`DELETE /device/(deviceID)`<br>
Requires User Token<br>

### Get user devices:
`GET /device`<br>
Sample Response:<br>
```
[
  {
    "deviceID": "deviceid",
    "deviceName": "deviceName"
  }
]
```
Requires User Token<br>

## Device Types
0 - temperature
1 - humidity
2 - light
3 - noise

### Send Sensor Data:
`POST /sensors/(sensorType)/(deviceID)`<br>
Sample Request:<br>
```
{
  "value": 20.0
}
```
<br>

### Get Sensor Data:
`GET /sensors/(sensorType)/(deviceID)`<br>
Sample Response:<br>
```
[
  {
    "id": 0,
    "value": 0,
    "timestamp": 0
  }
]
```

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
