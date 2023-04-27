const express = require('express');
const router = express.Router();
const aircraftController = require('../controllers/aircraftController.js');
 
 
router.get('/', aircraftController .list.bind(aircraftController));
 
router.get('/:id', aircraftController.show.bind(aircraftController));
 
router.post('/', aircraftController.create.bind(aircraftController));
 
router.put('/:id', aircraftController.update.bind(aircraftController));
 
router.delete('/:id', aircraftController.remove.bind(aircraftController));
 
module.exports = router;

