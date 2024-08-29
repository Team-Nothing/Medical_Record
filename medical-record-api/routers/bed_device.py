import datetime
import traceback
from email.header import Header

from fastapi import APIRouter, Depends, Request
from utils.sql_connector import SQLConnector
from starlette.responses import JSONResponse

from utils.token_manager import TokenManager, Session


router = APIRouter(prefix="/bed-device")


@router.post("/link")
def link(request: Request, data: dict, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    if "bed_id" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Bed ID is required"
        })

    device_register_id = request.headers.get("Device-Register-ID", None)
    if device_register_id is None:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device Register ID is required"
        })

    bed_id = data["bed_id"]
    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

    if result == Session.APPROVED:
        device_register_id = sql_connector.query(
            "SELECT device_id FROM device WHERE device_id = %s AND account_uid = %s",
            (device_register_id, uid)
        )

        if device_register_id is None or len(device_register_id) == 0:
            return JSONResponse(status_code=404, content={
                "code": "DEVICE/NOT-FOUND",
                "message": "Device not found"
            })

        try:
            sql_connector.execute(
                "INSERT INTO bed_device (bed_id, device_id) VALUES (%s, %s) ON CONFLICT (device_id) DO NOTHING",
                (bed_id, device_register_id[0][0])
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


@router.get("/patient-info")
def patient_info(request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    device_register_id = request.headers.get("Device-Register-ID", None)
    if device_register_id is None:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device Register ID is required"
        })

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

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

        patient = sql_connector.query(
            "SELECT ar.serial_id, CONCAT(b.room_number, b.bed_position), ar.admission_date, de.name AS department, d.name, d.image_uid, r.name, r.image_uid, n.name, n.image_uid, p.name, p.image_uid, l.name, p.birth, g.name, bl.name, p.feature_id "
            + "FROM admission_record AS ar "
            + "INNER JOIN patient AS p ON ar.patient_uid = p.uid "
            + "INNER JOIN doctor AS d ON ar.doctor_id = d.doctor_id "
            + "INNER JOIN doctor AS r ON ar.resident_id = r.doctor_id "
            + "INNER JOIN nurse AS n ON ar.nurse_id = n.nurse_id "
            + "INNER JOIN bed AS b ON ar.bed_id = b.bed_id "
            + "INNER JOIN gender AS g ON p.gender_id = g.gender_id "
            + "INNER JOIN blood as bl ON p.blood_id = bl.blood_id "
            + "INNER JOIN language as l ON p.language_id = l.language_id "
            + "INNER JOIN department as de ON ar.department_id = de.department_id "
            + "INNER JOIN bed_device as bd ON ar.bed_id = bd.bed_id "
            + "WHERE bd.device_id = %s AND ar.discharge_date IS NULL "
            + "ORDER BY ar.admission_date DESC LIMIT 1",
            (device_register_id,)
        )

        patient_data = {
            "has_patient": False,
            "bed": None,
            "admission_days": None,
            "department": None,
            "doctor": None,
            "resident": None,
            "nurse": None,
            "patient": None,
            "tags": []
        }
        if patient is None or len(patient) == 0:
            return JSONResponse(status_code=200, content={
                "code": "OK",
                "message": "No current patient found",
                "data": patient_data
            })

        patient_data["has_patient"] = True
        patient_data["bed"] = patient[0][1]
        patient_data["admission_days"] = (datetime.datetime.now().date() - patient[0][2].date()).days
        patient_data["department"] = patient[0][3]
        patient_data["doctor"] = {
            "name": patient[0][4],
            "image_uid": patient[0][5]
        }
        patient_data["resident"] = {
            "name": patient[0][6],
            "image_uid": patient[0][7]
        }
        patient_data["nurse"] = {
            "name": patient[0][8],
            "image_uid": patient[0][9]
        }
        age = (datetime.datetime.now().date() - patient[0][13]).days / 365
        patient_data["patient"] = {
            "name": patient[0][10],
            "image_uid": patient[0][11],
            "language": patient[0][12],
            "age": f"{int(age)}-{int((age - int(age)) * 12)}",
            "gender": patient[0][14],
            "blood": patient[0][15],
            "feature_id": patient[0][16],
        }

        patient_tags = sql_connector.query(
            "SELECT t.title, t.icon, t.description "
            + "FROM admission_tag AS at "
            + "LEFT JOIN tag_type AS t ON at.tag_type_id = t.tag_type_id "
            + "WHERE at.admission_id = %s",
            (patient[0][0],)
        )

        for tag in patient_tags:
            patient_data["tags"].append({
                "title": tag[0],
                "icon": tag[1],
                "description": tag[2]
            })

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


@router.get("/patient-reminders")
def patient_reminders(request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    device_register_id = request.headers.get("Device-Register-ID", None)
    if device_register_id is None:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device Register ID is required"
        })

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

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

        reminder_data = []

        reminders = sql_connector.query(
            "SELECT ar.title, ar.finished_at "
            + "FROM admission_reminder AS ar "
            + "INNER JOIN admission_record AS ard ON ar.admission_id = ard.serial_id "
            + "INNER JOIN bed_device AS bd ON bd.bed_id = ard.bed_id "
            + "WHERE bd.device_id = %s AND (ar.finished_at IS NULL OR ar.finished_at > (NOW() - INTERVAL '1 HOUR')) "
            + "ORDER BY ar.order",
            (device_register_id,)
        )
        if reminders is not None:
            for reminder in reminders:
                reminder_data.append({
                    "title": reminder[0],
                    "finished": False if reminder[1] is None else True
                })

        return JSONResponse(status_code=200, content={
            "code": "OK",
            "message": "Patient reminders found",
            "data": reminder_data
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


@router.get("/patient-routine/{filter_type}")
def patient_routine(filter_type: str, request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    device_register_id = request.headers.get("Device-Register-ID", None)
    if device_register_id is None:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device Register ID is required"
        })

    if filter_type not in ["CURRENT", "ALL"]:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/INVALID-FILTER",
            "message": "Filter type must be 'current' or 'all'"
        })

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

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

        reminder_data = []

        query_string = "SELECT ar.title, ar.description, ar.time, ar.finished_at "\
            + "FROM admission_routine AS ar "\
            + "INNER JOIN admission_record AS ard ON ar.admission_id = ard.serial_id "\
            + "INNER JOIN bed_device AS bd ON bd.bed_id = ard.bed_id "\
            + "WHERE bd.device_id = %s AND (ar.finished_at IS NULL OR ar.finished_at > (NOW() - INTERVAL '1 HOUR')) "
        if filter_type == "CURRENT":
            query_string += "AND (ar.time < (NOW() + INTERVAL '1 DAY')) "
        query_string += "ORDER BY ar.time"

        routines = sql_connector.query(query_string, (device_register_id,))
        if routines is not None:
            for routine in routines:
                reminder_data.append({
                    "title": routine[0],
                    "description": routine[1],
                    "time": routine[2].strftime("%m-%dT%H:%M"),
                    "finished": False if routine[3] is None else True
                })

        return JSONResponse(status_code=200, content={
            "code": "OK",
            "message": "Patient routine found",
            "data": reminder_data
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


@router.get("/medical-transcript")
def medical_transcript(data: dict, request: Request, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    device_register_id = request.headers.get("Device-Register-ID", None)
    if device_register_id is None:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device Register ID is required"
        })

    if "start_date" not in data or "end_date" not in data or "page" not in data or "order_by" not in data or "item_per_page" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Start date, end date, page, order_by, and item_per_page are required"
        })

    start_date = data["start_date"] if data["start_date"] is not None else "1970-01-01"
    end_date = data["end_date"] if data["end_date"] is not None else datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    page = data["page"] if data["page"] is not None else 1
    order_by = data["order_by"]
    item_per_page = int(data["item_per_page"]) if data["item_per_page"] is not None else 10

    if order_by not in ["ASC", "DESC"]:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/INVALID-ORDER",
            "message": "Order by must be 'ASC' or 'DESC'"
        })

    x_device_id = request.headers.get("X-Device-ID", None)
    auth = request.headers.get("Authorization", "").replace("Bearer ", "")
    result, uid = TokenManager.check_session(auth, x_device_id, sql_connector)

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

        query_string = " "\
            + "FROM transcript_record AS tr "\
            + "INNER JOIN admission_record AS ar ON tr.admission_id = ar.serial_id "\
            + "INNER JOIN bed_device AS bd ON bd.bed_id = ar.bed_id "\
            + "INNER JOIN feature AS f on tr.feature_id = f.feature_id "\
            + "WHERE bd.device_id = %s "\

        filter_string = "AND tr.datetime BETWEEN %s AND %s "

        length = sql_connector.query(
            "SELECT COUNT(tr.serial_id)" + query_string + filter_string,
            (device_register_id, start_date, end_date)
        )[0][0]

        pages = 0 if length == 0 else length // item_per_page + 1

        limit_start = (page - 1) * item_per_page
        limit_end = item_per_page

        order_by_string = f"ORDER BY tr.datetime {order_by} "
        limit_string = "LIMIT %s OFFSET %s"

        result = []
        transcripts = sql_connector.query(
            "SELECT tr.datetime, f.name, tr.content" + query_string + filter_string + order_by_string + limit_string,
            (device_register_id, start_date, end_date, limit_end, limit_start)
        )
        for transcript in transcripts:
            result.append({
                "datetime": transcript[0].strftime("%m-%dT%H:%M"),
                "name": transcript[1],
                "content": transcript[2]
            })

        return JSONResponse(status_code=200, content={
            "code": "OK",
            "message": "Query medical transcript successful",
            "data": {
                "total_pages": pages,
                "items": result
            }
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
