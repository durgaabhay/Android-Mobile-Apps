const express = require('express');
const router = express.Router()
const mongoose = require('mongoose');
const checkAuth = require('../auth/check-auth');
const twilio = require('twilio');
const accountSid = '';
const authToken = '';
const client = new twilio(accountSid, authToken);
const Customer = require('../models/users');
const Reservation = require('../models/reservation');
const Feedback = require('../models/feedback');


/* GET users listing. */
router.get('/', function(req, res, next) {
  Customer.find().exec().then(result => {
      console.log(result);
      res.status(200).json({result});
  }).catch(err =>{
      res.status(500).json({err});
  })
});

router.post('/searchCustomer',checkAuth,(req,res,next) => {
    console.log('Getting inside customer search' , req.body.phoneNumber);
   Customer.find({phoneNumber: req.body.phoneNumber}).exec()
       .then(result => {
           console.log({result});
           res.status(200).json(result);
       }).catch(err => {

   });
});

router.post('/placeOrder',checkAuth,(req,res,next)=> {
    console.log('getting inside placeOrder ', req.body)
    Customer.find({phoneNumber: req.body.phoneNumber}).exec()
        .then(userExists => {
            if (userExists.length >= 1) {
                console.log('User already exists' , userExists);
                if(userExists.userName === ''){
                    console.log('username not updated', userExists.userName)
                    Customer.updateOne({phoneNumber: req.body.phoneNumber},
                        {$set:{userName:req.body.userName,noOfSeats:req.body.noOfSeats}}).exec();
                }
                client.messages.create({
                    to : req.body.phoneNumber,
                    from : '17047538151',
                    body : 'Welcome "' + req.body.userName + '" to Awesome Food.'
                });
                reserveTable(req,res);
            } else {
                console.log('New user coming in' , req.body);
                const newCustomer = new Customer({
                    _id: mongoose.Types.ObjectId(),
                    userName: req.body.userName,
                    email: req.body.email,
                    phoneNumber: req.body.phoneNumber,
                    noOfSeats : req.body.noOfSeats
                });
                newCustomer.save()
                    .then(result => {
                        console.log('New customer added', result);
                        client.messages.create({
                            to : result.phoneNumber,
                            from : '',
                            body : 'Welcome"' + req.body.userName + '"to Awesome Food.'
                        });
                        reserveTable(req,res);
                    });
            }
        });
});

function reserveTable(req,res){
    console.log('Inside reserve Table ', req.body);
    var dateTime = new Date().toUTCString().
    replace(/T/, ' ').      // replace T with a space
    replace(/\..+/, '');    // delete the dot and everything after;

    Reservation.find({noOfSeats:req.body.noOfSeats,status:'READY',tableType:'ALACARTE'}).exec()
        .then( foundTable => {
            console.log('Found tables ' , foundTable);
            if(foundTable.length >= 1){
                console.log('converted date is : ', dateTime);
                Reservation.updateOne({_id:foundTable[0]._id} , {$set : {userName: req.body.userName, phoneNumber: req.body.phoneNumber ,status:'DINING', bookingDate: dateTime.toString()}})
                    .exec().then( result => {
                    res.status(200).json({
                        message : 'Table assigned to customer',
                        tableDetails : foundTable[0].tableNumber
                    })
                }).catch(err=>{
                    res.status(500).json({
                        message : 'Error assigning table to customer',
                        error : err
                    });
                });
            }else{
                Customer.updateOne({phoneNumber:req.body.phoneNumber}, {$set : {noOfSeats : req.body.noOfSeats, tableStatus : 'INQUEUE', userName: req.body.userName}}).exec()
                    .then( result => {
                        client.messages.create({
                            to : `${req.body.phoneNumber}`,
                            from : '17047538151',
                            body : 'Welcome"' + req.body.userName + '"to Awesome Food. Thank you for your patience. Once a table is ready you will receive a message'
                        });
                        res.status(201).json({
                            message : 'Putting user in queue',
                            customerDetails : {result}
                        })
                    }).catch(err => {
                    res.status(500).json({
                        message : 'Error putting user in the queue',
                        error : err
                    })
                });
            }
        })
}

router.get('/feedback',checkAuth, (req,res,next) =>{
    console.log('Inside user feedbacks');
    Feedback.find().exec()
        .then( result => {
            console.log('User feedbacks are ' , result);
            res.status(200).json({result});
        }).catch( err => {
             res.status(500).json({err});
    });
});

router.post('/checkOut', checkAuth, (req,res,next) => {
   console.log('Inside checkout user' , req.body);
   Customer.updateOne({phoneNumber:req.body.phoneNumber},{$set: {tableStatus:'COMPLETE', noOfSeats:'0'}}).exec();
   Reservation.updateOne({tableNumber:req.body.tableNumber} , {$set :{userName:'', phoneNumber: '', bookingDate: '',status:'READY'}})
       .exec().then( result => {
           console.log('user checkout complete')
           res.status(200).json({result});
            client.messages.create({
                to : `${req.body.phoneNumber}`,
                from : '17047538151',
                body : 'Thank you for your visit. How would you rate our service? Respond back with Yes or No'
       });
   }).catch( err =>{
       res.status(500).json({err});
   });
       //send a twilio message for user feedback

});



router.post('/schedule', (req,res,next)=>{

});

module.exports = router;
