const express = require('express');
const router = express.Router();
const airportController = require('../controllers/airportController.js');
 
 
router.get('/', airportController .list.bind(airportController));
 
router.get('/:id', airportController.show.bind(airportController));
 
router.post('/', airportController.create.bind(airportController));
 
router.put('/:id', airportController.update.bind(airportController));
 
router.delete('/:id', airportController.remove.bind(airportController));
 
module.exports = router;

