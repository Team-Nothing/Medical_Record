import datetime
import traceback
from email.header import Header

from fastapi import APIRouter, Depends, Request
from utils.sql_connector import SQLConnector
from starlette.responses import JSONResponse

from utils.token_manager import TokenManager, Session


router = APIRouter(prefix="/bed-device")


@router.get("/get-patient-info")
def get_patient_info(request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    device_register_id = request.headers.get("Device-Register-ID", None)
    if device_register_id is None:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device Register ID is required"
        })

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "")
    auth = auth.replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id)

    if result == Session.APPROVED:
        device = sql_connector.query(
            "SELECT device_id FROM device WHERE device_id = %s AND account_uid = %s",
            (device_register_id, uid)
        )
        if device is None or len(device) == 0:
            return JSONResponse(status_code=404, content={
                "code": "DEVICE/NOT-FOUND",
                "message": "Device not found"
            })

        patient_info = sql_connector.query(
            "SELECT CONCAT(b.room_number, b.bed_position) AS bed, ar.admission_date, d.name AS doctor, n.name AS nurse, de.name AS department, l.name AS language, p.name, p.birth, g.name AS gender, bl.name AS blood, p.feature_id "
            + "FROM admission_record AS ar "
            + "LEFT JOIN patient AS p ON ar.patient_uid = p.uid "
            + "LEFT JOIN doctor AS d ON ar.doctor_id = d.doctor_id "
            + "LEFT JOIN nurse AS n ON ar.nurse_id = n.nurse_id "
            + "LEFT JOIN bed AS b ON ar.bed_id = b.bed_id "
            + "LEFT JOIN gender AS g ON p.gender_id = g.gender_id "
            + "LEFT JOIN blood as bl ON p.blood_id = bl.blood_id "
            + "LEFT JOIN language as l ON p.language_id = l.language_id "
            + "LEFT JOIN department as de ON ar.department_id = de.department_id "
            + "LEFT JOIN bed_device as bd ON ar.bed_id = bd.bed_id "
            + "WHERE bd.device_id = '6' AND ar.discharge_date IS NULL "
            + "ORDER BY ar.admission_date DESC LIMIT 1",

        )

        patient_data = {
            "has_patient": False,
            "bed": None,
            "admission_days": None,
            "doctor": None,
            "nurse": None,
            "department": None,
            "language": None,
            "name": None,
            "age": None,
            "gender": None,
            "blood": None,
            "feature_id": None
        }
        if patient_info is None or len(patient_info) == 0:
            return JSONResponse(status_code=200, content={
                "code": "OK",
                "message": "No current patient found",
                "data": patient_data
            })
        patient_data["has_patient"] = True
        patient_data["bed"] = str(patient_info[0][0])
        patient_data["admission_days"] = (datetime.datetime.now().date() - patient_info[0][1].date()).days
        patient_data["doctor"] = patient_info[0][2]
        patient_data["nurse"] = patient_info[0][3]
        patient_data["department"] = patient_info[0][4]
        patient_data["language"] = patient_info[0][5]
        patient_data["name"] = patient_info[0][6]
        age = (datetime.datetime.now().date() - patient_info[0][7]).days / 365
        patient_data["age"] = f"{int(age)}-{int((age-int(age)) * 12)}"
        patient_data["gender"] = patient_info[0][8]
        patient_data["blood"] = patient_info[0][9]
        patient_data["feature_id"] = patient_info[0][10]

        print(patient_data)

        return JSONResponse(status_code=200, content={
            "code": "OK",
            "message": "Patient found",
            "data": patient_data
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


@router.post("/link-bed")
def link_bed(request: Request, data: dict, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    if "device_id" not in data or "bed_id" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device ID and Bed ID are required"
        })

    device_id = data["device_id"]
    bed_id = data["bed_id"]
    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "")
    auth = auth.replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id)

    if result == Session.APPROVED:
        device_id = sql_connector.query(
            "SELECT device_id FROM device WHERE device_id = %s AND account_uid = %s",
            (device_id, uid)
        )

        if device_id is None or len(device_id) == 0:
            return JSONResponse(status_code=404, content={
                "code": "DEVICE/NOT-FOUND",
                "message": "Device not found"
            })

        try:
            sql_connector.execute(
                "INSERT INTO bed_device (bed_id, device_id) VALUES (%s, %s) ON CONFLICT (device_id) DO NOTHING",
                (bed_id, device_id[0][0])
            )
            return JSONResponse(status_code=200, content={
                "code": "OK",
                "message": "Device linked to bed"
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
