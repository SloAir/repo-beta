const mongoose = require('mongoose');
const Schema   = mongoose.Schema;

const aircraftSchema = new Schema({
    model: {
        code: {
            type: String,
            required: true
        },
        name: {
            type: String,
            required: true
        }
    },
    registration: {
        type: String,
        required: true
    },
    // letalska združba, kateri pripada letalo
    airline: {
        type: Schema.Types.ObjectId,
        ref: 'Airline',
        required: true
    },
    images: [
        {
            src: {
                type: String,
                required: true
            }
        }
    ]
}, {timestamps : true});

module.exports = mongoose.model('Aircraft', aircraftSchema);
