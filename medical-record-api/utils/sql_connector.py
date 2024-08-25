import json
from contextlib import contextmanager
from enum import Enum

from psycopg2 import pool


class SQLConnector:
    @staticmethod
    def get_connection():
        with open("configs/db-current.json", "r") as f:
            db_cfg = json.load(f)
        return SQLConnector(db_cfg)

    def __init__(self, cfg):
        self.conn_pool = pool.ThreadedConnectionPool(
            minconn=1,
            maxconn=64,
            **cfg
        )

    @contextmanager
    def use_connection(self):
        connection = self.conn_pool.getconn()

        try:
            yield connection
        finally:
            self.conn_pool.putconn(connection)

    def query(self, query, params=None, execute=False):
        with self.use_connection() as connection:
            cursor = connection.cursor()
            result = None

            try:
                cursor.execute(query, params)
                if execute:
                    connection.commit()
                result = cursor.fetchall()
            except Exception as e:
                if execute:
                    connection.rollback()
                raise e
            finally:
                cursor.close()
                return result

    def execute(self, query, params=None):
        with self.use_connection() as connection:
            cursor = connection.cursor()

            try:
                cursor.execute(query, params)
                connection.commit()
            except Exception as e:
                connection.rollback()
                raise e
            finally:
                cursor.close()
