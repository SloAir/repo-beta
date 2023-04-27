const AircraftModel = require('../models/aircraftModel.js');
 
 
module.exports = new class AircraftModel {
   list(req , res) {
       AircraftModel.find({} , (err, aircrafts) => {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting aircraft.',
                   error: err
               });
           }
           return res.status(200).json(aircrafts);
       });
   };
   show(req , res) {
       AircraftModel.findById(req.params.id,  (err, aircraft) => {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting aircraft.',
                   error: err
               });
           }
           if (!aircraft) {
               return res.status(404).json({
                   message: 'No such aircraft'
               });
           }
           return res.status(200).json(aircraft);
       });
   };
   create(req , res) {
       let aircraft = new AircraftModel({

       });
 
       aircraft.save((err, aircraft) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when creating aircraft',
                   error: err
               });
           }
           return res.status(201).json(aircraft);
       });
   };
   update(req , res) {
       AircraftModel.findById(req.params.id ,  (err, aircraft) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting aircraft',
                   error: err
               });
           }
           if (!aircraft) {
               return res.status(404).json({
                   message: 'No such aircraft'
               });
           }
           
           aircraft.save((err, aircraft) => {
               if (err) {
                   return res.status(500).json({
                       message: 'Error when updating aircraft.',
                       error: err
                   });
               }
               return res.json(aircraft);
           });
       });
   };
   remove(req , res) {
       AircraftModel.findByIdAndRemove(req.params.id,  (err, aircraft) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when deleting the aircraft.',
                   error: err
               });
           }
           return res.status(204).json();
       });
   };
};

