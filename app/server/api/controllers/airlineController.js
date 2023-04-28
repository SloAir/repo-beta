const AirlineModel = require('../models/airlineModel.js');
 
 
module.exports = new class AirlineController {
   list(req , res) {
       AirlineModel.find({} , (err, airlines) => {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting airline.',
                   error: err
               });
           }
           return res.status(200).json(airlines);
       });
   };
   show(req , res) {
       AirlineModel.findById(req.params.id,  (err, airline) => {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting airline.',
                   error: err
               });
           }
           if (!airline) {
               return res.status(404).json({
                   message: 'No such airline'
               });
           }
           return res.status(200).json(airline);
       });
   };
   create(req , res) {
       let airline = new AirlineModel({

       });
 
       airline.save((err, airline) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when creating airline',
                   error: err
               });
           }
           return res.status(201).json(airline);
       });
   };
   update(req , res) {
       AirlineModel.findById(req.params.id ,  (err, airline) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting airline',
                   error: err
               });
           }
           if (!airline) {
               return res.status(404).json({
                   message: 'No such airline'
               });
           }
           
           airline.save((err, airline) => {
               if (err) {
                   return res.status(500).json({
                       message: 'Error when updating airline.',
                       error: err
                   });
               }
               return res.json(airline);
           });
       });
   };
   remove(req , res) {
       AirlineModel.findByIdAndRemove(req.params.id,  (err, airline) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when deleting the airline.',
                   error: err
               });
           }
           return res.status(204).json();
       });
   };
};

