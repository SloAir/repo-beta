const AirportModel = require('../models/airportModel.js');
 
 
module.exports = new class AirportController {
   list(req , res) {
       AirportModel.find({} , (err, airports) => {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting airport.',
                   error: err
               });
           }
           return res.status(200).json(airports);
       });
   };
   show(req , res) {
       AirportModel.findById(req.params.id,  (err, airport) => {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting airport.',
                   error: err
               });
           }
           if (!airport) {
               return res.status(404).json({
                   message: 'No such airport'
               });
           }
           return res.status(200).json(airport);
       });
   };
   create(req , res) {
       let airport = new AirportModel({

       });
 
       airport.save((err, airport) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when creating airport',
                   error: err
               });
           }
           return res.status(201).json(airport);
       });
   };
   update(req , res) {
       AirportModel.findById(req.params.id ,  (err, airport) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting airport',
                   error: err
               });
           }
           if (!airport) {
               return res.status(404).json({
                   message: 'No such airport'
               });
           }
           
           airport.save((err, airport) => {
               if (err) {
                   return res.status(500).json({
                       message: 'Error when updating airport.',
                       error: err
                   });
               }
               return res.json(airport);
           });
       });
   };
   remove(req , res) {
       AirportModel.findByIdAndRemove(req.params.id,  (err, airport) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when deleting the airport.',
                   error: err
               });
           }
           return res.status(204).json();
       });
   };
};

