import datetime

import numpy as np
import torch
import torchaudio
import whisper

from pydub import AudioSegment

from pyannote.audio import Audio
from pyannote.core import Segment

from pyannote.audio.pipelines.speaker_verification import PretrainedSpeakerEmbedding
from sklearn.cluster import AgglomerativeClustering


NUM_SPEAKERS = 3
MODEL_SIZE = 'medium'


class SpeakerEmbedding:
    def __init__(self):
        self.embedding_model = PretrainedSpeakerEmbedding(
            "speechbrain/spkrec-ecapa-voxceleb",
            device=torch.device("cuda") if torch.cuda.is_available() else torch.device("cpu")
        )

    def average_channels(self, waveform):
        # Check if waveform has more than one channel
        if waveform.shape[0] > 1:
            waveform = waveform.mean(axis=0)
        # Add back the channel dimension (1 channel)
        waveform = waveform.unsqueeze(0)
        return waveform

    def generate_embedding_from_file(self, sql_connector, name, account_uid, audio_uid):
        waveform, sample_rate = torchaudio.load(f"data/objects/{audio_uid}")
        embedding = self.generate_embedding(waveform)

        object_uid = sql_connector.query(
            f"INSERT INTO object (account_uid, object_id, visibility, extension) VALUES (%s, %s, %s, %s) RETURNING uid",
            (account_uid, "feature", False, "npy"),
            execute=True
        )
        if not object_uid:
            object_uid = sql_connector.query(
                f"SELECT uid FROM object WHERE account_uid = %s AND object_id = %s",
                (account_uid, "feature")
            )[0][0]
        else:
            object_uid = object_uid[0][0]
        np.save(f"data/objects/{object_uid}", embedding)

        sql_connector.execute(
            "INSERT INTO feature (model_id, name, account_uid, dim_uid, audio_uid) VALUES ('1', %s, %s, %s, %s)",
            (name, account_uid, object_uid, audio_uid)
        )
        print("Background Task - Feature Generated & Saved")

    def generate_embedding(self, waveform):
        processed_waveform = self.average_channels(waveform)
        embedding = self.embedding_model(processed_waveform[None])
        # np.save(f"../data/xdd.npy", embedding)
        return embedding


if __name__ == "__main__":
    file = "../data/objects/23b064ed-c66a-45d9-8a9d-1ee9efed7af6"
    speaker_embedding = SpeakerEmbedding()
    result = speaker_embedding.generate_embedding_from_file(file)

    print()
