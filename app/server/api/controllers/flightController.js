const FlightModel = require('../models/flightModel.js');
 
 
module.exports = new class FlightController {
   list(req , res) {
       FlightModel.find({} , (err, flights) => {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting flight.',
                   error: err
               });
           }
           return res.status(200).json(flights);
       });
   };
   show(req , res) {
       FlightModel.findById(req.params.id,  (err, flight) => {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting flight.',
                   error: err
               });
           }
           if (!flight) {
               return res.status(404).json({
                   message: 'No such flight'
               });
           }
           return res.status(200).json(flight);
       });
   };
   create(req , res) {
       let flight = new FlightModel({

       });
 
       flight.save((err, flight) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when creating flight',
                   error: err
               });
           }
           return res.status(201).json(flight);
       });
   };
   update(req , res) {
       FlightModel.findById(req.params.id ,  (err, flight) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting flight',
                   error: err
               });
           }
           if (!flight) {
               return res.status(404).json({
                   message: 'No such flight'
               });
           }
           
           flight.save((err, flight) => {
               if (err) {
                   return res.status(500).json({
                       message: 'Error when updating flight.',
                       error: err
                   });
               }
               return res.json(flight);
           });
       });
   };
   remove(req , res) {
       FlightModel.findByIdAndRemove(req.params.id,  (err, flight) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when deleting the flight.',
                   error: err
               });
           }
           return res.status(204).json();
       });
   };
};

