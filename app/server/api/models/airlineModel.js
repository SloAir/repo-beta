const mongoose = require('mongoose');
const Schema   = mongoose.Schema;

const airlineSchema = new Schema({
    name: {
        type: String,
        required: true
    },
    code: {
        iata: {
            type: String,
            required: true
        },
        icao: {
            type: String,
            required: true
        }
    },
    url: {
        type: String,
        required: true
    },
    logo: {
        type: Schema.Types.ObjectId,
        required: true
    },
    scheduledFlights: [
        {
            type: Schema.Types.ObjectId,
            ref: 'Flight',
            required: true
        }
    ],
    flightHistory: [
        {
            type: Schema.Types.ObjectId,
            ref: 'Flight',
            required: true
        }
    ],
    images: [
        {
            src: {
                type: String,
                required: true
            }
        }
    ]
}, {timestamps : true});

module.exports = mongoose.model('Airline', airlineSchema);

