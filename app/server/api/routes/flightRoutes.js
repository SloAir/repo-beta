const express = require('express');
const router = express.Router();
const flightController = require('../controllers/flightController.js');
 
 
router.get('/', flightController .list.bind(flightController));
 
router.get('/:id', flightController.show.bind(flightController));
 
router.post('/', flightController.create.bind(flightController));
 
router.put('/:id', flightController.update.bind(flightController));
 
router.delete('/:id', flightController.remove.bind(flightController));
 
module.exports = router;

