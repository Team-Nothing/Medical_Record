import importlib
import os

import torch

from fastapi import FastAPI

description = """
Medical_Record API helps you do awesome stuff. 🚀

### Authentication

You will be able to:

* **Create/Login Accounts**.
"""

app = FastAPI(
    title="Medical Record API",
    description=description,
    summary="XDD",
    version="0.0.1",
    contact={
        "name": "Nothing Chang",
        "url": "https://github.com/I-am-nothing",
        "email": "jdps99119@gmail.com",
    },
    license_info={
        "name": "Apache 2.0",
        "url": "https://www.apache.org/licenses/LICENSE-2.0.html",
    },
)


@app.get("/")
def read_root():
    return {
        "status": "OK",
        "message": "Medical Record API is running!"
    }


@app.get("/cuda-available")
def check_cuda():
    cuda_available = torch.cuda.is_available()
    return {
        "status": "OK" if cuda_available else "CUDA/NOT-AVAILABLE",
        "message": "CUDA is available" if cuda_available else "CUDA is not available"
    }


for file_name in os.listdir("routers"):
    if file_name.endswith(".py") and file_name != "__init__.py":
        module = importlib.import_module(f"routers.{file_name[:-3]}")
        if hasattr(module, "setup"):
            getattr(module, "setup")(app)
