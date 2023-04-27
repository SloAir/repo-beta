const express = require('express');
const router = express.Router();
const imageController = require('../controllers/imageController.js');
 
 
router.get('/', imageController .list.bind(imageController));
 
router.get('/:id', imageController.show.bind(imageController));
 
router.post('/', imageController.create.bind(imageController));
 
router.put('/:id', imageController.update.bind(imageController));
 
router.delete('/:id', imageController.remove.bind(imageController));
 
module.exports = router;

