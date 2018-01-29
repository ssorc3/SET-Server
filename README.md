# SET-Server

Create User: POST /create
Sample Request:
{
  "username": "Test",
  "password": "test"
}
returns token
  
Get User Token: POST /token
Sample Request:
{
  "username": "Test",
  "password": "test"
}
returns token for existing user

Register a new device: POST /registerDevice
Sample Request:
{
  "deviceID": (DEVICEID),
  "deviceName": "Living room alarm"
}
Requires User Token

Get user devices: GET /devices
Requires User Token

Send Temperature Data: POST /sensors/0/(deviceID)
Sample Request:
{
  "value": 20.0
}

Send Humidity Data: POST /sensors/1/(deviceID)
Sample Request:
{
  "value": 60.0
}

Send Light Data: POST /sensors/2/(deviceID)
Sample Request:
{
  "value": 14.0
}
