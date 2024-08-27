import datetime
import hashlib
from enum import Enum

import jwt


class Session(Enum):
    APPROVED = "Session approved"
    EXPIRED = "Session expired"
    INVALID = "Invalid session"


class TokenManager:
    SECRET_KEY = "s&(E]cWPf1XX3JK$`9&@"
    ALGORITHM = "HS256"

    @staticmethod
    def create_token(data: dict):
        to_encode = data.copy()
        encoded_jwt = jwt.encode(to_encode, TokenManager.SECRET_KEY, algorithm=TokenManager.ALGORITHM)
        return encoded_jwt

    @staticmethod
    def decode_token(token: str):
        try:
            decoded_token = jwt.decode(token, TokenManager.SECRET_KEY, algorithms=[TokenManager.ALGORITHM])
            return decoded_token
        except Exception:
            return None

    @staticmethod
    def check_session(token: str, x_device_id, sql_connector):
        decoded_token = TokenManager.decode_token(token)

        try:
            if decoded_token is None:
                return Session.INVALID, None
            if decoded_token["device_id"] != x_device_id:
                return Session.INVALID, None
            if decoded_token["exp"] < datetime.datetime.utcnow().timestamp():
                return Session.EXPIRED, None

            token = sql_connector.query(
                "SELECT token FROM session_token WHERE token = %s AND disabled = FALSE",
                (token,)
            )
            if token is None or len(token) == 0:
                return Session.INVALID, None

            return Session.APPROVED, decoded_token["uid"]
        except Exception:
            return Session.INVALID, None

    @staticmethod
    def calculate_md5(file, chunk_size=4096):
        md5 = hashlib.md5()
        while chunk := file.read(chunk_size):
            md5.update(chunk)
        return md5.hexdigest()
