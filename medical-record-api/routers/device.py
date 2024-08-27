import datetime
import traceback
from email.header import Header

from fastapi import APIRouter, Depends, Request
from utils.sql_connector import SQLConnector
from starlette.responses import JSONResponse

from utils.token_manager import TokenManager, Session


router = APIRouter(prefix="/device")


@router.post("/add")
def add_device(request: Request, data: dict, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    if "device_type_id" not in data or "bluetooth_mac" not in data or "ipv6" not in data or "ipv4" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device type ID, Bluetooth MAC, IPv6, and IPv4 are required"
        })

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

    if result == Session.APPROVED:
        try:
            device_id = sql_connector.query(
                "INSERT INTO device (device_type_id, account_uid, bluetooth_mac, ipv6, ipv4) VALUES (%s, %s, %s, %s, %s)"
                + " ON CONFLICT (device_type_id, account_uid, bluetooth_mac, ipv6, ipv4) DO NOTHING RETURNING device_id",
                (data["device_type_id"], uid, data["bluetooth_mac"], data["ipv6"], data["ipv4"]),
                execute=True,
            )

            if device_id is None or len(device_id) == 0:
                device_id = sql_connector.query(
                    "SELECT device_id FROM device WHERE device_type_id = %s AND account_uid = %s AND bluetooth_mac = %s AND ipv6 = %s AND ipv4 = %s",
                    (data["device_type_id"], uid, data["bluetooth_mac"], data["ipv6"], data["ipv4"])
                )[0][0]
            else:
                device_id = device_id[0][0]

            return JSONResponse(status_code=201, content={
                "code": "OK",
                "message": "Device added",
                "data": {
                    "device_register_id": device_id
                }
            })
        except Exception:
            return JSONResponse(status_code=500, content={
                "code": "INTERNAL-SERVER-ERROR",
                "message": "Something went wrong, please try again later",
                "traceback": f"{traceback.format_exc()}"
            })

    elif result == Session.EXPIRED:
        return JSONResponse(status_code=401, content={
            "code": "AUTH/SESSION-EXPIRED",
            "message": "Session expired"
        })
    else:
        return JSONResponse(status_code=401, content={
            "code": "AUTH/INVALID-SESSION",
            "message": "Invalid session"
        })


@router.post("/update")
def add_device(request: Request, data: dict, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    if "device_type_id" not in data or "bluetooth_mac" not in data or "ipv6" not in data or "ipv4" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device type ID, Bluetooth MAC, IPv6, and IPv4 are required"
        })

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

    device_register_id = request.headers.get("Device-Register-ID", None)
    if device_register_id is None:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device Register ID is required"
        })

    if result == Session.APPROVED:
        try:
            sql_connector.execute(
                "UPDATE device SET device_type_id = %s, bluetooth_mac = %s, ipv6 = %s, ipv4 = %s WHERE device_id = %s",
                (data["device_type_id"], data["bluetooth_mac"], data["ipv6"], data["ipv4"], device_register_id),
            )

            return JSONResponse(status_code=201, content={
                "code": "OK",
                "message": "Device added",
                "data": {
                    "device_register_id": device_register_id
                }
            })
        except Exception:
            return JSONResponse(status_code=500, content={
                "code": "INTERNAL-SERVER-ERROR",
                "message": "Something went wrong, please try again later",
                "traceback": f"{traceback.format_exc()}"
            })

    elif result == Session.EXPIRED:
        return JSONResponse(status_code=401, content={
            "code": "AUTH/SESSION-EXPIRED",
            "message": "Session expired"
        })
    else:
        return JSONResponse(status_code=401, content={
            "code": "AUTH/INVALID-SESSION",
            "message": "Invalid session"
        })


def setup(app):
    app.include_router(router)
