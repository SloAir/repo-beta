// Created by Gal Sternad, 26/04/2023
const mongoose = require('mongoose');
const Schema   = mongoose.Schema;

const flightSchema = new Schema({
    // ID leta, ki ga pridobi API
    flightId: {
        type: String,
        required: true
    },
    status: {
        // pove, če je letalo aktivno
        live: {
            type: Boolean,
            required: true
        }
        // TODO: dodaj ostale smiselne lastnosti
    },
    aircraft: {
        type: Schema.Types.ObjectId,
        ref: 'Aircraft',
        required: true
    },
    airport: {
        origin: {
            type: Schema.Types.ObjectId,
            ref: 'Airport',
            required: true
        },
        destination: {
            type: Schema.Types.ObjectId,
            ref: 'Airport',
            required: true
        }
    },
    time: {
        scheduled: {
            departure: {
                type: Date,
                required: true
            },
            arrival: {
                type: Date,
                required: true
            }
        },
        real: {
            departure: {
                type: Date,
                required: true
            },
            arrival: {
                type: Date,
                required: true
            }
        },
        estimated: {
            departure: {
                type: Date,
                required: true
            },
            arrival: {
                type: Date,
                required: true
            }
        },
        metadata: {
            updated: {
                type: Date,
                required: true
            }
        }
    },
    trail: [
        {
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
            speed: {
                type: Number,
                required: true
            },
            timestamp: {
                type: Date,
                required: true
            },
            // smer letala v stopinjah
            direction: {
                type: Number,
                required: true
            }
        }
    ],
    firstTimestamp: {
        type: Date,
        required: true
    }
}, {timestamps : true});

module.exports = mongoose.model('Flight', flightSchema);
