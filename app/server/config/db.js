// Created by Gal Sternad, 27/04/2023
const mongoose = require('mongoose');
require('dotenv').config({ path: '../' });

mongoose.connect(process.env.DB_URI, {
}).then(() => {
    console.log('Connected to the database!');
}).catch((err) => {
    console.log('Connection failed:', err);
});

module.exports = mongoose.connection;
