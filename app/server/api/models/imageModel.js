const mongoose = require('mongoose');
const Schema   = mongoose.Schema;

const imageSchema = new Schema({
    
}, {timestamps : true});

module.exports = mongoose.model('image', imageSchema);

