import datetime
import traceback
from email.header import Header

from fastapi import APIRouter, Depends, Request
from utils.sql_connector import SQLConnector
from starlette.responses import JSONResponse

from utils.token_manager import TokenManager, Session


router = APIRouter(prefix="/ward")


@router.post("/admission")
def admission(request: Request, data: dict, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    if "patient_uid" not in data or "bed_id" not in data or "doctor_id" not in data or "nurse_id" not in data:
        return JSONResponse(status_code=400, content={
            "code": "GENERIC/MISSING-FIELDS",
            "message": "Patient UID, Bed ID, Doctor ID, and Nurse ID are required"
        })

    return JSONResponse(status_code=500, content={
        "code": "INTERNAL-SERVER-ERROR",
        "message": "NOT IMPLEMENTED YET",
        "traceback": None
    })


@router.post("/discharge")
def discharge(request: Request, data: dict, sql_connector: SQLConnector = Depends(SQLConnector.get_connection)):
    return JSONResponse(status_code=500, content={
        "code": "INTERNAL-SERVER-ERROR",
        "message": "NOT IMPLEMENTED YET",
        "traceback": None
    })


def setup(app):
    app.include_router(router)