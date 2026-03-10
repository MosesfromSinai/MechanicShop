-- index on Customer last name 
CREATE INDEX idx_customer_lname ON Customer(lname);

-- index on Car customer_id so we can find all cars from a certain customer faster
CREATE INDEX idx_car_customer_id ON Car(customer_id);

-- index on Service_Request car_vin to find service reqs for cars
CREATE INDEX idx_sr_car_vin ON Service_Request(car_vin);