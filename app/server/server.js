// Created by Gal Sternad, 27/04/2023
const express = require('express');
const cors = require('cors');
require('dotenv').config();
const db = require('./config/db');

const app = express();
const port = process.env.APP_PORT || 3000;

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const aircraftRoutes = require('./api/routes/aircraftRoutes');
const airlineRoutes = require('./api/routes/airlineRoutes');
const airportRoutes = require('./api/routes/airportRoutes');
const flightRoutes = require('./api/routes/flightRoutes');
app.use('/api/aircrafts', aircraftRoutes);
app.use('/api/airlines', airlineRoutes);
app.use('/api/airports', airportRoutes);
app.use('/api/flights', flightRoutes);

app.listen(port, () => console.log(`Listening on port ${port}`));