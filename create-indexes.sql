-- index on Customer last name 
CREATE INDEX idx_customer_lname ON Customer(lname);

-- index on Car customer_id so we can find all cars from a certain customer faster
CREATE INDEX idx_car_customer_id ON Car(customer_id);