CREATE TABLE db (
    name VARCHAR(255) NOT NULL,
    version VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,  -- For the 'Type' enum, use VARCHAR
    author VARCHAR(255) NOT NULL,
    msg TEXT,
    timestamp TIMESTAMP NOT NULL,
    changeData TEXT,
    zones VARCHAR(10) NOT NULL,  -- For the 'Zones' enum, use VARCHAR
    
    PRIMARY KEY (name, timestamp, zones)  -- Composite primary key
);