import datetime
import traceback
import json
from email.header import Header
from typing import List, Optional

from fastapi import APIRouter, Depends, Request, UploadFile, File, Form, BackgroundTasks

from utils.feature_embed import SpeakerEmbedding
from utils.sql_connector import SQLConnector
from starlette.responses import JSONResponse

from utils.token_manager import TokenManager, Session


router = APIRouter(prefix="/transcript")


@router.post("/bed-audio-upload")
async def bed_audio_upload(
    request: Request,
    file: UploadFile = File(...),
    data: str = Form(...),
    # nearby_bluetooth_mac: Optional[List[str]] = Form(None),
    # file_md5: str = Form(...),
    # start_at: str = Form(...),
    # previous_audio_uid: Optional[str] = Form(None),
    sql_connector: SQLConnector = Depends(SQLConnector.get_connection)
):
    device_register_id = request.headers.get("Device-Register-ID", None)
    if device_register_id is None:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Device Register ID is required"
        })

    data = json.loads(data)

    if "file_md5" not in data or "start_at" not in data or "previous_audio_uid" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "File MD5, Start at, and Previous audio ID are required"
        })

    nearby_bluetooth_mac = data["nearby_bluetooth_mac"]
    file_md5 = data["file_md5"]
    start_at = data["start_at"]
    previous_audio_uid = data["previous_audio_uid"]

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

        admission = sql_connector.query(
            "SELECT ar.serial_id, ar.patient_uid "
            + "FROM admission_record AS ar "
            + "INNER JOIN bed_device as bd ON ar.bed_id = bd.bed_id "
            + "WHERE bd.device_id = %s AND ar.discharge_date IS NULL",
            (device_register_id,)
        )
        if admission is None or len(admission) == 0:
            return JSONResponse(status_code=404, content={
                "code": "PATIENT/NOT-FOUND",
                "message": "Patient not found"
            })

        file_md5_upload = TokenManager.calculate_md5(file.file)
        if file_md5_upload != file_md5:
            return JSONResponse(status_code=400, content={
                "code": "FILE/MD5-MISMATCH",
                "message": "Check file md5 failed"
            })

        file_name_ext = file.filename.split(".")
        object_uid = sql_connector.query(
            "INSERT INTO object (account_uid, object_id, visibility, extension) VALUES (%s, %s, %s, %s) RETURNING uid",
            (uid, file_name_ext[0], False, file.filename.split(".")[-1]),
            execute=True
        )[0][0]

        file.file.seek(0)
        with open(f"data/objects/{object_uid}", "wb") as f:
            f.write(file.file.read())

        if nearby_bluetooth_mac is None:
            nearby_bluetooth_mac = []

        nearby_features = sql_connector.query(
            "SELECT f.feature_id FROM feature AS f "
            + "INNER JOIN account AS a ON f.account_uid = a.uid "
            + "INNER JOIN device AS d ON a.uid = d.account_uid "
            + f"WHERE bluetooth_mac IN {tuple(nearby_bluetooth_mac)} "
            + "GROUP BY f.feature_id",
            (nearby_bluetooth_mac,)
        )

        if nearby_features is None:
            features = []
        else:
            features = [feature[0] for feature in nearby_features]

        features.append(sql_connector.query(
            "SELECT feature_id FROM patient WHERE uid = %s",
            (admission[0][1],)
        )[0][0])

        for feature in features:
            sql_connector.query(
                "INSERT INTO nearby_feature (audio_uid, feature_id) VALUES (%s, %s)",
                (object_uid, feature),
                execute=True
            )

        sql_connector.execute(
            "INSERT INTO transcript_audio (audio_uid, admission_id, start_at, previous_audio_uid) VALUES (%s, %s, %s, %s)",
            (object_uid, admission[0][0], start_at, previous_audio_uid)
        )

        return JSONResponse(status_code=200, content={
            "code": "OK",
            "message": "Bed audio uploaded successfully",
            "data": {
                "audio_uid": object_uid
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


@router.post("/upload-feature")
async def upload_feature(
    request: Request,
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...),
    file_md5: str = Form(...),
    feature_name: str = Form(...),
    sql_connector: SQLConnector = Depends(SQLConnector.get_connection)
):
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

        # file_md5_upload = TokenManager.calculate_md5(file.file)
        # if file_md5_upload != file_md5:
        #     return JSONResponse(status_code=400, content={
        #         "code": "FILE/MD5-MISMATCH",
        #         "message": "Check file md5 failed"
        #     })

        file_name_ext = file.filename.split(".")

        object_uid = sql_connector.query(
            "INSERT INTO object (account_uid, object_id, visibility, extension) VALUES (%s, %s, %s, %s) RETURNING uid",
            (uid, file_name_ext[0], False, file_name_ext[-1]),
            execute=True
        )[0][0]

        with open(f"data/objects/{object_uid}", "wb") as f:
            f.write(file.file.read())

        background_tasks.add_task(
            SpeakerEmbedding().generate_embedding_from_file,
            sql_connector,
            name=feature_name,
            account_uid=uid,
            audio_uid=object_uid
        )

        return JSONResponse(status_code=200, content={
            "code": "OK",
            "message": "Speaking feature uploaded successfully"
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
