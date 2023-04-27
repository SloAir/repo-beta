const express = require('express');
const router = express.Router();
const airlineController = require('../controllers/airlineController.js');
 
 
router.get('/', airlineController .list.bind(airlineController));
 
router.get('/:id', airlineController.show.bind(airlineController));
 
router.post('/', airlineController.create.bind(airlineController));
 
router.put('/:id', airlineController.update.bind(airlineController));
 
router.delete('/:id', airlineController.remove.bind(airlineController));
 
module.exports = router;

