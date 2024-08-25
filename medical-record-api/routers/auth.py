import datetime
import traceback
from email.header import Header

from fastapi import APIRouter, Depends, Request
from utils.sql_connector import SQLConnector
from starlette.responses import JSONResponse

from utils.token_manager import TokenManager, Session

router = APIRouter(prefix="/auth")


@router.post("/register")
def register(data: dict, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    if "username" not in data or "password" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Username and password are required"
        })

    username = data["username"]
    password = data["password"]

    try:
        user = sql_connector.query("SELECT username FROM account WHERE username = %s", (username,))

        if user:
            return JSONResponse(status_code=400, content={
                "code": "AUTH/USER-EXISTS",
                "message": "Username already exists"
            })

        sql_connector.execute(
            "INSERT INTO account (username, password) VALUES (%s, crypt(%s, gen_salt('bf'))) RETURNING password",
            (username, password),
        )

        return JSONResponse(content={
            "code": "OK",
            "message": "Account created successfully"
        })
    except Exception:
        return JSONResponse(status_code=500, content={
            "code": "INTERNAL-SERVER-ERROR",
            "message": "Something went wrong, please try again later",
            "traceback": f"{traceback.format_exc()}"
        })


@router.post("/login")
def login(data: dict, request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    exp_type = {
        "temporary": 30,
        "device": 60 * 24 * 14
    }

    if "username" not in data or "password" not in data or "session" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Username, password and session are required"
        })

    if data["session"] not in exp_type:
        return JSONResponse(status_code=400, content={
            "code": "AUTH/INVALID-SESSION",
            "message": "Invalid session type"
        })

    username = data["username"]
    password = data["password"]
    session = data["session"]
    x_device_id = request.headers.get("X-Device-ID", None)

    if x_device_id is None and session == "device":
        return JSONResponse(status_code=400, content={
            "code": "AUTH/MISSING-DEVICE-ID",
            "message": "Device ID is required for device session"
        })

    try:
        stored_hashed_password = sql_connector.query("SELECT (password) FROM account WHERE username = %s", (username,))
        if not stored_hashed_password:
            return JSONResponse(status_code=401, content={
                "code": "AUTH/USER-NOT-FOUND",
                "message": "Username does not exist"
            })
        stored_hashed_password = stored_hashed_password[0][0]

        entered_password_hash = sql_connector.query("SELECT crypt(%s, %s)", (password, stored_hashed_password))[0][0]

        if entered_password_hash == stored_hashed_password:
            uid = sql_connector.query(
                "SELECT uid FROM account WHERE username = %s;",
                (username,)
            )[0][0]
            sql_connector.execute("UPDATE account SET last_login = NOW(), last_active = NOW() WHERE uid = %s;", (uid,))
            session_info = {
                "uid": uid,
                "exp": datetime.datetime.utcnow() + datetime.timedelta(minutes=exp_type.get(session, 0)),
                "device_id": x_device_id
            }

            return JSONResponse(content={
                "code": "OK",
                "message": "Login successful",
                "data": {
                    "uid": uid,
                    "token": TokenManager.create_token(session_info)
                }
            })
        else:
            return JSONResponse(status_code=401, content={
                "code": "AUTH/PASSWORD-MISMATCH",
                "message": "Passwords do not match"
            })

    except Exception:
        return JSONResponse(status_code=500, content={
            "code": "INTERNAL-SERVER-ERROR",
            "message": "Something went wrong, please try again later",
            "traceback": f"{traceback.format_exc()}"
        })


@router.post("/check-session")
def check_session(request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "")
    auth = auth.replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id)

    if result == Session.APPROVED:
        try:
            sql_connector.execute("UPDATE account SET last_active = NOW() WHERE uid = %s;", (uid,))

            return JSONResponse(content={
                "code": "OK",
                "message": "Session approved"
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


@router.post("/renew-session")
def renew_session(request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "")
    auth = auth.replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id)

    if result == Session.APPROVED:
        try:
            session_info = {
                "uid": uid,
                "exp": datetime.datetime.utcnow() + datetime.timedelta(minutes=30),
                "device_id": x_device_id
            }
            sql_connector.execute("UPDATE account SET last_active = NOW() WHERE uid = %s;", (uid,))

            return JSONResponse(content={
                "code": "OK",
                "message": "Session renewed",
                "data": {
                    "uid": uid,
                    "token": TokenManager.create_token(session_info)
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


@router.post('/reset-password')
def reset_password(data: dict, request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    if "password" not in data or "new_password" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Password and new password are required"
        })

    password = data["password"]
    new_password = data["new_password"]

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "")
    auth = auth.replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id)

    if result != Session.APPROVED:
        return JSONResponse(status_code=401, content={
            "code": "AUTH/INVALID-SESSION" if result == Session.INVALID else "AUTH/SESSION-EXPIRED",
            "message": "Invalid session" if result == Session.INVALID else "Session expired"
        })

    try:
        user = sql_connector.query("SELECT uid FROM account WHERE uid = %s", (uid,))

        if not user:
            return JSONResponse(status_code=400, content={
                "code": "AUTH/USER-NOT-FOUND",
                "message": "Username does not exist"
            })

        stored_hashed_password = sql_connector.query("SELECT (password) FROM account WHERE uid = %s", (uid,))
        if not stored_hashed_password:
            return JSONResponse(status_code=401, content={
                "code": "AUTH/USER-NOT-FOUND",
                "message": "Username does not exist"
            })
        stored_hashed_password = stored_hashed_password[0][0]

        entered_password_hash = sql_connector.query("SELECT crypt(%s, %s)", (password, stored_hashed_password))[0][0]

        if entered_password_hash != stored_hashed_password:
            return JSONResponse(status_code=401, content={
                "code": "AUTH/PASSWORD-MISMATCH",
                "message": "Passwords do not match"
            })

        sql_connector.execute(
            "UPDATE account SET password = crypt(%s, gen_salt('bf')) WHERE uid = %s",
            (new_password, uid)
        )
        sql_connector.execute("UPDATE account SET last_active = NOW() WHERE uid = %s;", (uid,))

        return JSONResponse(content={
            "code": "OK",
            "message": "Password updated successfully"
        })
    except Exception:
        return JSONResponse(status_code=500, content={
            "code": "INTERNAL-SERVER-ERROR",
            "message": "Something went wrong, please try again later",
            "traceback": f"{traceback.format_exc()}"
        })

@router.post('/re-authencate')
def re_authenticate(data: dict, request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    if "password" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Password is required"
        })

    password = data["password"]

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "")
    auth = auth.replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id)

    if result != Session.APPROVED:
        return JSONResponse(status_code=401, content={
            "code": "AUTH/INVALID-SESSION" if result == Session.INVALID else "AUTH/SESSION-EXPIRED",
            "message": "Invalid session" if result == Session.INVALID else "Session expired"
        })

    try:
        stored_hashed_password = sql_connector.query("SELECT (password) FROM account WHERE uid = %s", (uid,))
        if not stored_hashed_password:
            return JSONResponse(status_code=401, content={
                "code": "AUTH/USER-NOT-FOUND",
                "message": "Username does not exist"
            })
        stored_hashed_password = stored_hashed_password[0][0]

        entered_password_hash = sql_connector.query("SELECT crypt(%s, %s)", (password, stored_hashed_password))[0][0]

        if entered_password_hash == stored_hashed_password:
            sql_connector.execute("UPDATE account SET last_active = NOW() WHERE uid = %s;", (uid,))

            return JSONResponse(content={
                "code": "OK",
                "message": "Re-authentication successful",
                "data": {
                    "uid": uid,
                    "token": auth
                }
            })
        else:
            return JSONResponse(status_code=401, content={
                "code": "AUTH/PASSWORD-MISMATCH",
                "message": "Passwords do not match"
            })

    except Exception:
        return JSONResponse(status_code=500, content={
            "code": "INTERNAL-SERVER-ERROR",
            "message": "Something went wrong, please try again later",
            "traceback": f"{traceback.format_exc()}"
        })


def setup(app):
    app.include_router(router)
