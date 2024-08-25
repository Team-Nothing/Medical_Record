CREATE TABLE gender (
    gender_id SERIAL2 NOT NULL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
INSERT INTO gender(name) VALUES('Male');
INSERT INTO gender(name) VALUES ('Female');
SELECT * FROM gender;

CREATE TABLE blood (
    blood_id SERIAL2 NOT NULL PRIMARY KEY,
    name VARCHAR(10) NOT NULL
);
INSERT INTO blood(name) VALUES ('A+');
INSERT INTO blood(name) VALUES ('A-');
INSERT INTO blood(name) VALUES ('B+');
INSERT INTO blood(name) VALUES ('B-');
INSERT INTO blood(name) VALUES ('AB+');
INSERT INTO blood(name) VALUES ('AB-');
INSERT INTO blood(name) VALUES ('O+');
INSERT INTO blood(name) VALUES ('O-');
SELECT * FROM blood;

CREATE TABLE language (
    language_id SERIAL2 NOT NULL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
INSERT INTO language(name) VALUES ('Chinese');
INSERT INTO language(name) VALUES ('English');
INSERT INTO language(name) VALUES ('Japanese');
INSERT INTO language(name) VALUES ('Vietnamese');
SELECT * FROM language;

CREATE TABLE device_type (
    device_type_id SERIAL2 NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT
);
INSERT INTO device_type(name) VALUES ('Screen with Whisper');
SELECT * FROM device_type;

CREATE TABLE device (
    device_id SERIAL4 NOT NULL PRIMARY KEY,
    device_type_id INT NOT NULL,
    bluetooth_mac VARCHAR(50),
    ipv6 VARCHAR(50),
    FOREIGN KEY (device_type_id) REFERENCES device_type(device_type_id)
);
INSERT INTO device(device_type_id, bluetooth_mac, ipv6) VALUES (1, '00:00:00:00:00:00', '2001:0db8:85a3:0000:0000:8a2e:0370:7334');
SELECT * FROM device;

CREATE TABLE model(
    model_id SERIAL2 NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    version VARCHAR(10) NOT NULL,
    description TEXT
);
INSERT INTO model(name, version, description) VALUES ('whisper-small-01', '0.0.1', 'The first version of whisper small model');
SELECT * FROM model;

CREATE TABLE feature (
    feature_id SERIAL8 PRIMARY KEY,
    model_id INT NOT NULL,
    FOREIGN KEY (model_id) REFERENCES model(model_id)
);
INSERT INTO feature(model_id) VALUES (1);
INSERT INTO feature(model_id) VALUES (1);
SELECT * FROM feature;

CREATE TABLE patient (
    uid VARCHAR(50) NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50),
    phone VARCHAR(15) NOT NULL,
    address VARCHAR(250) NOT NULL,
    birth DATE NOT NULL,
    gender_id INT NOT NULL,
    blood_id INT NOT NULL,
    language_id INT NOT NULL,
    feature_id INT,
    FOREIGN KEY (gender_id) REFERENCES Gender(gender_id),
    FOREIGN KEY (blood_id) REFERENCES blood(blood_id),
    FOREIGN KEY (language_id) REFERENCES language(language_id),
    FOREIGN KEY (feature_id) REFERENCES feature(feature_id)
);
INSERT INTO patient VALUES ('dc9f8ae2-5a12-4cba-a366-2120417d6ba0', '張聖坤', 'jdps99119@gmail.com', '0918214333', '台中市烏日區 XDD XDD XDD', '2003-11-18', 1, 4, 1, 1);
INSERT INTO patient VALUES ('1bdcd810-3f7d-4b5b-a647-38c2ec85bb78', 'XDD', 'xdd@gmail.com', '0987877887', '高雄市楠梓區 XDD XDD XDD', '1987-1-1', 2, 6, 2, NULL);
SELECT * FROM patient;

CREATE TABLE family_type(
    family_type_id SERIAL2 NOT NULL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
INSERT INTO family_type(name) VALUES ('Mother');
INSERT INTO family_type(name) VALUES ('Father');
INSERT INTO family_type(name) VALUES ('Brother');
INSERT INTO family_type(name) VALUES ('Sister');
INSERT INTO family_type(name) VALUES ('Grandmother');
INSERT INTO family_type(name) VALUES ('Grandfather');
SELECT * FROM family_type;

CREATE TABLE family (
    uid VARCHAR(50) NOT NULL,
    family_uid VARCHAR(50) NOT NULL,
    family_type_id INT NOT NULL,
    PRIMARY KEY (uid, family_uid),
    FOREIGN KEY (uid) REFERENCES patient(uid),
    FOREIGN KEY (family_uid) REFERENCES patient(uid),
    FOREIGN KEY (family_type_id) REFERENCES family_type(family_type_id)
);
INSERT INTO family VALUES ('dc9f8ae2-5a12-4cba-a366-2120417d6ba0', '1bdcd810-3f7d-4b5b-a647-38c2ec85bb78', 1);
SELECT * FROM family;

CREATE TABLE doctor(
    doctor_id SERIAL4 NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50),
    feature_id INT,
    FOREIGN KEY (feature_id) REFERENCES feature (feature_id)
);
INSERT INTO doctor(name, email, feature_id) VALUES ('Dr. LED', 'ledlab2391@gmail.com', 2);
SELECT * FROM doctor;

CREATE TABLE nurse (
    nurse_id SERIAL4 NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50),
    feature_id INT,
    FOREIGN KEY (feature_id) REFERENCES feature (feature_id)
);
INSERT INTO nurse(name, email, feature_id) VALUES ('Fish', 'fish@gmail.com', NULL);
SELECT * FROM nurse;

CREATE TABLE bed_type (
    bed_type_id SERIAL2 NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
INSERT INTO bed_type(name) VALUES ('Health Insurance Single');
INSERT INTO bed_type(name) VALUES ('Health Insurance Double');
INSERT INTO bed_type(name) VALUES ('Self-paid Single');
INSERT INTO bed_type(name) VALUES ('Self-paid Double');
SELECT * FROM bed_type;

CREATE TABLE bed (
    bed_id SERIAL2 NOT NULL PRIMARY KEY,
    bed_type_id INT NOT NULL,
    floor INT NOT NULL,
    room_number INT NOT NULL,
    bed_position VARCHAR(2) NOT NULL,
    FOREIGN KEY (bed_type_id) REFERENCES bed_type(bed_type_id)
);
INSERT INTO bed(bed_type_id, floor, room_number, bed_position) VALUES (1, 3, 338, 'C');
SELECT * FROM bed;

CREATE TABLE bed_device (
    device_id INT NOT NULL PRIMARY KEY,
    bed_id INT NOT NULL,
    FOREIGN KEY (bed_id) REFERENCES bed(bed_id),
    FOREIGN KEY (device_id) REFERENCES device(device_id)
);
INSERT INTO bed_device(device_id, bed_id) VALUES (1, 1);
SELECT * FROM bed_device;

CREATE TABLE admission_record (
    serial_id SERIAL8 NOT NULL PRIMARY KEY,
    admission_date TIMESTAMP DEFAULT NOW(),
    discharge_date TIMESTAMP,
    uid VARCHAR(50) NOT NULL,
    bed_id INT NOT NULL,
    doctor_id INT NOT NULL,
    nurse_id INT NOT NULL,
    FOREIGN KEY (uid) REFERENCES patient(uid),
    FOREIGN KEY (bed_id) REFERENCES bed(bed_id),
    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id),
    FOREIGN KEY (nurse_id) REFERENCES nurse(nurse_id)
);
INSERT INTO admission_record(uid, bed_id, doctor_id, nurse_id) VALUES ('dc9f8ae2-5a12-4cba-a366-2120417d6ba0', 1, 1, 1);
SELECT * FROM admission_record;

CREATE TABLE transcript_record (
    serial_id SERIAL8 NOT NULL PRIMARY KEY,
    admission_record_id INT NOT NULL,
    feature_id INT,
    datetime TIMESTAMP NOT NULL,
    content TEXT,
    FOREIGN KEY (admission_record_id) REFERENCES admission_record(serial_id),
    FOREIGN KEY (feature_id) REFERENCES feature(feature_id)
);
INSERT INTO transcript_record(admission_record_id, feature_id, datetime, content) VALUES (1, 1, NOW(), 'Hello, I am Dr. LED');

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE account (
    uid UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    last_login TIMESTAMP DEFAULT NOW(),
    last_active TIMESTAMP DEFAULT NOW()
);

SELECT * FROM device;

DELETE FROM device WHERE  device_id = '9';

SELECT * FROM bed;
SELECT * FROM device_type;
SELECT * FROM account;

INSERT INTO device (device_type_id, account_uid, bluetooth_mac, ipv6, ipv4)
VALUES (1, 'f2b62c59-36fa-4f42-9e47-48e7459bbc24', '00:1A:3F:F1:4C:C6', 'fe80::cf44:53e5:5c18:6be9', '192.168.87.87')
ON CONFLICT (device_type_id, account_uid, bluetooth_mac, ipv6, ipv4) DO NOTHING RETURNING device_id;
SELECT device_id FROM device WHERE device_type_id = 1 AND account_uid = 'f2b62c59-36fa-4f42-9e47-48e7459bbc24' AND bluetooth_mac = '00:1A:3F:F1:4C:C6' AND ipv6 = 'fe80::cf44:53e5:5c18:6be9' AND ipv4 = '192.168.87.87';



ALTER TABLE device
ADD CONSTRAINT unique_device_combination UNIQUE (device_type_id, account_uid, bluetooth_mac, ipv6, ipv4);
