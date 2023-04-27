const ImageModel = require('../models/imageModel.js');
 
 
module.exports = new class ImageModel {
   list(req , res) {
       ImageModel.find({} , (err, images) => {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting image.',
                   error: err
               });
           }
           return res.status(200).json(images);
       });
   };
   show(req , res) {
       ImageModel.findById(req.params.id,  (err, image) => {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting image.',
                   error: err
               });
           }
           if (!image) {
               return res.status(404).json({
                   message: 'No such image'
               });
           }
           return res.status(200).json(image);
       });
   };
   create(req , res) {
       let image = new ImageModel({

       });
 
       image.save((err, image) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when creating image',
                   error: err
               });
           }
           return res.status(201).json(image);
       });
   };
   update(req , res) {
       ImageModel.findById(req.params.id ,  (err, image) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when getting image',
                   error: err
               });
           }
           if (!image) {
               return res.status(404).json({
                   message: 'No such image'
               });
           }
           
           image.save((err, image) => {
               if (err) {
                   return res.status(500).json({
                       message: 'Error when updating image.',
                       error: err
                   });
               }
               return res.json(image);
           });
       });
   };
   remove(req , res) {
       ImageModel.findByIdAndRemove(req.params.id,  (err, image) =>  {
           if (err) {
               return res.status(500).json({
                   message: 'Error when deleting the image.',
                   error: err
               });
           }
           return res.status(204).json();
       });
   };
};

