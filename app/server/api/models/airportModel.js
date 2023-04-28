const mongoose = require('mongoose');
const Schema   = mongoose.Schema;

const airportSchema = new Schema({
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
    location: {
        latitude: {
            type: Number,
            required: true
        },
        longitude: {
            type: Number,
            required: true
        },
        altitude: {
            type: Number,
            required: true
        },
        city: {
            type: String,
            required: true
        },
        country: {
            name: {
                type: String,
                required: true
            },
            code: {
                type: String,
                required: true
            }
        }
    },
    timezone: {
        // TODO
    },
    website: {
        type: String,
        required: true
    },
    departures: [
        {
            date: {
                type: String,
                required: true
            },
            time: {
                planned: {
                    type: String,
                    required: true
                },
                estimated: {
                    type: String,
                    required: true
                }
            },
            destination: {
                type: String,
                required: true
            },
            flightId: {
                type: String,
                required: true
            },
            exit: {
                type: String,
                required: true
            },
            checkIn: {
                type: String,
                required: true
            },
            status: {
                type: String,
                required: true
            }
        }
    ],
    arrivals: [
        {
            date: {
                type: String,
                required: true
            },
            time: {
                planned: {
                    type: String,
                    required: true
                },
                estimated: {
                    type: String,
                    required: true
                }
            },
            destination: {
                type: String,
                required: true
            },
            flightId: {
                type: String,
                required: true
            },
            status: {
                type: String,
                required: true
            }
        }
    ],
    // TODO: stats lastnost?
    // polje vseh letalskih združb, ki trenutno letijo na/iz letališča
    airlines: [
        {
            type: Schema.Types.ObjectId,
            ref: 'Airline',
            required: true
        }
    ],
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

module.exports = mongoose.model('Airport', airportSchema);

