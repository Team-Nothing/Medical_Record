import datetime
import json
import os
import traceback

from fastapi import APIRouter, Depends, Request
from utils.sql_connector import SQLConnector
from starlette.responses import JSONResponse

from utils.token_manager import TokenManager, Session

api_docs = {}
router = APIRouter(prefix="/auth")
api_funcs = ["auth_register", "auth_login", "auth_check_session", "auth_renew_session",
             "auth_reset_password", "auth_re_authenticate"]

for func in api_funcs:
    if os.path.exists(f"api-docs/{func}.json"):
        with open(f"api-docs/{func}.json", "r") as f:
            api_docs[func] = json.load(f)


@router.post("/register", openapi_extra=api_docs["auth_register"])
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


@router.post("/login", openapi_extra=api_docs["auth_login"])
def login(data: dict, request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    exp_type = {
        "TEMPORARY": 30,
        "DEVICE": 60 * 24 * 14
    }

    if "username" not in data or "password" not in data or "session" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Username, password and session are required"
        })

    if data["session"] not in exp_type:
        return JSONResponse(status_code=400, content={
            "code": "AUTH/INVALID-SESSION",
            "message": "SESSION type must be either TEMPORARY or DEVICE"
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
        account = sql_connector.query(
            "SELECT a.password, bd.bed_id, doc.doctor_id, n.nurse_id, d.device_id "
            + "FROM account AS a "
            + "INNER JOIN device AS d ON a.uid = d.account_uid "
            + "INNER JOIN bed_device AS bd ON d.device_id = bd.device_id "
            + "FULL OUTER JOIN doctor AS doc ON a.uid = doc.account_uid "
            + "FULL OUTER JOIN nurse AS n ON a.uid = n.account_uid "
            + "WHERE username = %s",
            (username,)
        )
        if not account:
            return JSONResponse(status_code=401, content={
                "code": "AUTH/USER-NOT-FOUND",
                "message": "Username does not exist"
            })

        stored_password_hash = account[0][0]
        entered_password_hash = sql_connector.query("SELECT crypt(%s, %s)", (password, stored_password_hash))[0][0]

        if entered_password_hash == stored_password_hash:
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

            token = TokenManager.create_token(session_info)
            sql_connector.execute("INSERT INTO session_token (token, account_uid) VALUES (%s, %s);", (token, uid))

            account_type = "NONE"
            if account[0][1] is not None:
                account_type = "BED-DEVICE"
            elif account[0][2] is not None:
                account_type = "DOCTOR"
            elif account[0][3] is not None:
                account_type = "NURSE"

            return JSONResponse(content={
                "code": "OK",
                "message": "Login successful",
                "data": {
                    "uid": uid,
                    "type": account_type,
                    "token": token,
                    "device_register_id": account[0][4]
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


@router.post("/check-session", openapi_extra=api_docs["auth_check_session"])
def check_session(request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

    if result == Session.APPROVED:
        try:
            sql_connector.execute("UPDATE account SET last_active = NOW() WHERE uid = %s;", (uid,))

            account = sql_connector.query(
                "SELECT bd.bed_id, doc.doctor_id, n.nurse_id "
                + "FROM account AS a "
                + "INNER JOIN device AS d ON a.uid = d.account_uid "
                + "INNER JOIN bed_device AS bd ON d.device_id = bd.device_id "
                + "FULL OUTER JOIN doctor AS doc ON a.uid = doc.account_uid "
                + "FULL OUTER JOIN nurse AS n ON a.uid = n.account_uid "
                + "WHERE a.uid = %s",
                (uid,)
            )

            account_type = "NONE"
            if account[0][0] is not None:
                account_type = "BED-DEVICE"
            elif account[0][1] is not None:
                account_type = "DOCTOR"
            elif account[0][2] is not None:
                account_type = "NURSE"

            return JSONResponse(content={
                "code": "OK",
                "message": "Session approved",
                "data": {
                    "type": account_type,
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


@router.post("/renew-session", openapi_extra=api_docs["auth_renew_session"])
def renew_session(request: Request,data: dict, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    exp_type = {
        "TEMPORARY": 30,
        "DEVICE": 60 * 24 * 14
    }

    if "session" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "session is required"
        })

    if data["session"] not in exp_type:
        return JSONResponse(status_code=400, content={
            "code": "AUTH/INVALID-SESSION",
            "message": "Invalid session type"
        })

    session = data["session"]
    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

    if result == Session.APPROVED:
        try:
            session_info = {
                "uid": uid,
                "exp": datetime.datetime.utcnow() + datetime.timedelta(minutes=exp_type.get(session, 0)),
                "device_id": x_device_id
            }

            token = TokenManager.create_token(session_info)
            sql_connector.execute("UPDATE account SET last_active = NOW() WHERE uid = %s;", (uid,))
            sql_connector.execute("UPDATE session_token SET disabled = TRUE WHERE token = %s;", (auth,))
            sql_connector.execute("INSERT INTO session_token (token, account_uid) VALUES (%s, %s);", (token, uid))

            return JSONResponse(content={
                "code": "OK",
                "message": "Session renewed",
                "data": {
                    "uid": uid,
                    "token": token
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


@router.post('/reset-password', openapi_extra=api_docs["auth_reset_password"])
def reset_password(data: dict, request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    if "password" not in data or "new_password" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Password and new password are required"
        })

    password = data["password"]
    new_password = data["new_password"]

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

    if result != Session.APPROVED:
        return JSONResponse(status_code=401, content={
            "code": "AUTH/INVALID-SESSION" if result == Session.INVALID else "AUTH/SESSION-EXPIRED",
            "message": "Invalid session" if result == Session.INVALID else "Session expired"
        })

    try:
        user = sql_connector.query("SELECT uid FROM account WHERE uid = %s", (uid,))

        if not user:
            return JSONResponse(status_code=401, content={
                "code": "AUTH/USER-NOT-FOUND",
                "message": "User does not exist"
            })

        stored_hashed_password = sql_connector.query("SELECT (password) FROM account WHERE uid = %s", (uid,))
        if not stored_hashed_password:
            return JSONResponse(status_code=401, content={
                "code": "AUTH/USER-NOT-FOUND",
                "message": "User does not exist"
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


@router.post('/re-authenticate', openapi_extra=api_docs['auth_re_authenticate'])
def re_authenticate(data: dict, request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    if "password" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Password is required"
        })

    password = data["password"]

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

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


@router.post('/logout')
def logout(request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

    if result != Session.APPROVED:
        return JSONResponse(status_code=401, content={
            "code": "AUTH/INVALID-SESSION" if result == Session.INVALID else "AUTH/SESSION-EXPIRED",
            "message": "Invalid session" if result == Session.INVALID else "Session expired"
        })

    try:
        sql_connector.execute("UPDATE account SET last_active = NOW() WHERE uid = %s;", (uid,))
        sql_connector.execute("UPDATE session_token SET disabled = TRUE WHERE token = %s;", (auth,))

        return JSONResponse(content={
            "code": "OK",
            "message": "Logged out successfully"
        })
    except Exception:
        return JSONResponse(status_code=500, content={
            "code": "INTERNAL-SERVER-ERROR",
            "message": "Something went wrong, please try again later",
            "traceback": f"{traceback.format_exc()}"
        })


def setup(app):
    app.include_router(router)
