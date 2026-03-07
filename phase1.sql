DROP TABLE IF EXISTS Service_Request;
DROP TABLE IF EXISTS Car;
DROP TABLE IF EXISTS Mechanic;
DROP TABLE IF EXISTS Customer;

CREATE TABLE Customer (
    id          INTEGER     NOT NULL,
    fname       CHAR(40)    NOT NULL,
    lname       CHAR(40)    NOT NULL,
    phone       CHAR(40)    NOT NULL,
    address     CHAR(40)    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Mechanic (
    id              INTEGER     NOT NULL,
    fname           CHAR(40)    NOT NULL,
    lname           CHAR(40)    NOT NULL,
    experience      INTEGER     NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Car (
    vin             CHAR(40)    NOT NULL,
    year            INTEGER     NOT NULL,
    make            CHAR(40)    NOT NULL,
    model           CHAR(40)    NOT NULL,
    customer_id     INTEGER     NOT NULL,
    PRIMARY KEY (vin),
    FOREIGN KEY (customer_id) REFERENCES Customer(id)
);

CREATE TABLE Service_Request (
    rid             INTEGER     NOT NULL,
    date            DATE        NOT NULL,
    odometer        INTEGER     NOT NULL,
    complain        TEXT        NOT NULL,
    close_date      DATE,
    comment         TEXT,
    bill            INTEGER,
    car_vin         CHAR(40)    NOT NULL,
    customer_id     INTEGER     NOT NULL,
    mechanic_id     INTEGER     NOT NULL,
    PRIMARY KEY (rid),
    FOREIGN KEY (car_vin)       REFERENCES Car(vin),
    FOREIGN KEY (customer_id)   REFERENCES Customer(id),
    FOREIGN KEY (mechanic_id)   REFERENCES Mechanic(id)
);