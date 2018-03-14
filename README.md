# SET-Server

## Admin
### Get All Users:
`GET /admin/user`<br>
Sample Response: <br>
```
[
{
  "userID": "00000000-0000-0000-0000-0000000000000",
  "username": "test",
  "hash": "0uathu049ighn3thpukrkucg039yhintuich.09ig0.8"
},
...
]
```
requires Admin token<br>

### Delete User:
`DELETE /admin/user`<br>
Sample Request:<br>
```
{
  "userID": "1234"
}
```
requires admin token<br>

### Set an admin script:
`POST /admin/script`<br>
Sample Request: <br>
```
{
  "scriptName": "default",
  "script": "if(light > 2)then end"
}
```
scriptName is optional<br>
requires admin token<br>

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

### Rename a device
`PUT /device/(deviceID)`<br>
Sample Request: <br>
```
{
  "deviceName": "This is actually not the living room"
}
```
requires token<br>

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
0 - temperature<br>
1 - humidity<br>
2 - light<br>
3 - noise<br>

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

### Motion Detected:
`POST /sensor/motion/:deviceID`<br>


## Scripts
### Set a user's script:
`POST /script` <br>
Sample Request:<br>
```
{
  "scriptName": "default",
  "script": "if(temperature < 20 & noise > 400) then kettle; end"
}
```
scriptName is optional
requires user token<br>

### Get a user's script:
`GET /script` <br>
Sample Response:<br>
```
{
  "script": "if(temperature < 20 & noise > 400) then kettle; end"
}
```

### Remove a user's script: 
`DELETE /script`<br>
returns OK<br>
requires user token<br>

## Actuators
### Kettle
`POST /actuator/kettle`<br>
Sample request:
```
{
  "on": true
}
```
returns OK

### Lights
`POST /actuator/lights` <br>
Sample request:
```
{
  "isWhite": false,
  "hue": 255,
  "brightness": 255
}
```
returns OK<br>
requires Token

### Plug
`POST /actuator/plug` <br>
Sample request:
```
{
  "on": true
}
```
returns OK<br>
requires token

### Alarm
`POST /actuator/alarm`<br>
Sample Request:
```
{
  "on": true
}
```

## Signaling a photon
`GET /flashPhoton/:deviceID`<br>
returns 200

## Ideal Temp
### Set Ideal temp
`POST /idealTemp`<br>
Sample Request:
```
{
  "temp": 20
}
```
<br>

### Get Ideal temp
`GET /idealTemp`
Sample Response:
```
{
  "temp": 20
}
```

## Zones
### Create zone
`POST /zone`<br>
Sample Request:
```
{
  "zoneName": "Zone 1"
}
```

### Delete zone
`DELETE /zone`<br>
Sample Request:
```
{
  "zoneName": "Zone 1"
}
```

### Get zones
`GET /zone` <br>
Sample response:
```
[
{
  "id": "0",
  "userID": "00000000-0000-0000-0000-0000000000000",
  "name": "Zone 1"
},
...
]
```

## Bridges
### WebSocket
Bridge WebSocket `ws://<ip>/bridgeWS`

# SETLang
## Example
```
if(temperature > 10 & humidity > 10 & light > 10 & noise > 10) then
  email;
  text;
  notification;
  kettle;
  lights on;
  lightSetting true, 200, 20;
end
```

<br>

lights command has parameters:
<ul>
  <li>on</li>
  <li>off</li>
</ul>

lightSetting command has parameters:
<ul>
  <li>isWhite: true,  false</li>
  <li>hue: Color as a hue value between 0 and 255</li>
  <li>brightness: brightness as a value between 0 and 20</li>
